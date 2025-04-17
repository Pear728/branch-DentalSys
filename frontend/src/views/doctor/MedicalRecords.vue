<template>
  <div class="medical-records-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            @change="handleFilterChange">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="filterForm.patientName" placeholder="请输入患者姓名" clearable @clear="handleFilterChange"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilterChange">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="records-card">
      <div slot="header">
        <span>诊疗记录列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshRecords">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      
      <el-table
        :data="filteredRecords"
        v-loading="loading"
        border
        style="width: 100%">
        <el-table-column prop="id" label="病历号" width="80"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
        <el-table-column prop="visitDate" label="就诊日期" width="180">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.visitDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="chiefComplaint" label="主诉" width="200">
          <template slot-scope="scope">
            <el-tooltip v-if="scope.row.chiefComplaint" :content="scope.row.chiefComplaint" placement="top">
              <div class="ellipsis">{{ scope.row.chiefComplaint }}</div>
            </el-tooltip>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="diagnosis" label="诊断结果" width="200">
          <template slot-scope="scope">
            <el-tooltip v-if="scope.row.diagnosis" :content="scope.row.diagnosis" placement="top">
              <div class="ellipsis">{{ scope.row.diagnosis }}</div>
            </el-tooltip>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="viewRecord(scope.row)">查看</el-button>
            <el-button
              size="mini"
              type="success"
              @click="editRecord(scope.row)">编辑</el-button>
            <el-button
              size="mini"
              type="info"
              @click="printRecord(scope.row)">打印</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 查看/编辑病历对话框 -->
    <el-dialog :title="dialogType === 'view' ? '病历详情' : '编辑病历'" :visible.sync="recordDialogVisible" width="70%" :close-on-click-modal="false">
      <el-form :model="recordForm" :rules="recordRules" ref="recordForm" label-width="100px" :disabled="dialogType === 'view'">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名">
              <el-input v-model="currentPatientName" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊时间" prop="visitDate">
              <el-date-picker
                v-model="recordForm.visitDate"
                type="datetime"
                placeholder="选择就诊时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input
            type="textarea"
            v-model="recordForm.chiefComplaint"
            placeholder="请输入患者主诉"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input
            type="textarea"
            v-model="recordForm.diagnosis"
            placeholder="请输入诊断结果"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="治疗方案" prop="treatmentPlan">
          <el-input
            type="textarea"
            v-model="recordForm.treatmentPlan"
            placeholder="请输入治疗方案"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="处方" prop="prescription">
          <el-input
            type="textarea"
            v-model="recordForm.prescription"
            placeholder="请输入处方"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="检查建议">
          <el-input
            type="textarea"
            v-model="recordForm.examinationAdvice"
            placeholder="请输入检查建议"
            :rows="2">
          </el-input>
        </el-form-item>
        
        <el-form-item label="注意事项">
          <el-input
            type="textarea"
            v-model="recordForm.precautions"
            placeholder="请输入注意事项"
            :rows="2">
          </el-input>
        </el-form-item>
        
        <el-form-item label="随访计划">
          <el-input
            type="textarea"
            v-model="recordForm.followUpPlan"
            placeholder="请输入随访计划"
            :rows="2">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="recordDialogVisible = false">{{ dialogType === 'view' ? '关闭' : '取消' }}</el-button>
        <el-button v-if="dialogType === 'view'" type="primary" @click="printCurrentRecord">打印</el-button>
        <el-button v-if="dialogType === 'edit'" type="primary" @click="submitRecord" :loading="submitLoading">更新</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DoctorMedicalRecords',
  data() {
    return {
      loading: false,
      submitLoading: false,
      allRecords: [],
      filterForm: {
        dateRange: null,
        patientName: ''
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      
      // 查看/编辑病历
      recordDialogVisible: false,
      dialogType: 'view', // 'view' 或 'edit'
      currentPatientName: '',
      recordForm: {
        id: null,
        appointmentId: null,
        patientId: null,
        doctorId: null,
        visitDate: null,
        chiefComplaint: '',
        diagnosis: '',
        treatmentPlan: '',
        prescription: '',
        examinationAdvice: '',
        precautions: '',
        followUpPlan: ''
      },
      recordRules: {
        visitDate: [
          { required: true, message: '请选择就诊时间', trigger: 'change' }
        ],
        chiefComplaint: [
          { required: true, message: '请输入患者主诉', trigger: 'blur' }
        ],
        diagnosis: [
          { required: true, message: '请输入诊断结果', trigger: 'blur' }
        ],
        treatmentPlan: [
          { required: true, message: '请输入治疗方案', trigger: 'blur' }
        ]
      },
      patientList: []
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    
    // 筛选后的病历
    filteredRecords() {
      let result = [...this.allRecords]
      
      // 按日期范围筛选
      if (this.filterForm.dateRange && this.filterForm.dateRange.length === 2) {
        const startDate = new Date(this.filterForm.dateRange[0])
        startDate.setHours(0, 0, 0, 0)
        
        const endDate = new Date(this.filterForm.dateRange[1])
        endDate.setHours(23, 59, 59, 999)
        
        result = result.filter(item => {
          const visitDate = new Date(item.visitDate)
          return visitDate >= startDate && visitDate <= endDate
        })
      }
      
      // 按患者姓名筛选
      if (this.filterForm.patientName) {
        result = result.filter(item => 
          item.patientName && item.patientName.includes(this.filterForm.patientName)
        )
      }
      
      // 计算总数
      this.total = result.length
      
      // 分页
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      
      return result.slice(start, end)
    }
  },
  mounted() {
    this.fetchPatients()
    this.fetchRecords()
  },
  methods: {
    // 获取患者列表
    fetchPatients() {
      this.$http.get('/user/search?role=1')
        .then(response => {
          if (response.data.code === 200) {
            this.patientList = response.data.data
          }
        })
        .catch(error => {
          console.error('获取患者列表错误:', error)
        })
    },
    
    // 获取病历记录
    fetchRecords() {
      this.loading = true
      this.$http.get(`/medical-record/doctor/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 处理病历数据，添加患者姓名
            this.allRecords = response.data.data.map(item => {
              const patient = this.patientList.find(p => p.id === item.patientId)
              return {
                ...item,
                patientName: patient ? patient.realName : `患者(ID:${item.patientId})`
              }
            })
            
            // 按就诊日期排序（降序）
            this.allRecords.sort((a, b) => {
              return new Date(b.visitDate) - new Date(a.visitDate)
            })
          }
        })
        .catch(error => {
          console.error('获取病历记录错误:', error)
          this.$message.error('获取病历记录失败，请稍后重试')
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 查看病历
    viewRecord(record) {
      this.dialogType = 'view'
      this.recordForm = { ...record }
      this.currentPatientName = record.patientName
      this.recordDialogVisible = true
    },
    
    // 编辑病历
    editRecord(record) {
      this.dialogType = 'edit'
      this.recordForm = { ...record }
      this.currentPatientName = record.patientName
      this.recordDialogVisible = true
    },
    
    // 提交更新的病历
    submitRecord() {
      this.$refs.recordForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          
          this.$http.put('/medical-record', this.recordForm)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('病历更新成功')
                this.recordDialogVisible = false
                
                // 刷新病历列表
                this.fetchRecords()
              } else {
                this.$message.error(response.data.message || '病历更新失败')
              }
            })
            .catch(error => {
              console.error('更新病历错误:', error)
              this.$message.error('病历更新失败，请稍后重试')
            })
            .finally(() => {
              this.submitLoading = false
            })
        }
      })
    },
    
    // 打印当前查看的病历
    printCurrentRecord() {
      this.printRecord(this.recordForm)
    },
    
    // 打印病历
    printRecord(record) {
      this.$message.success('打印功能已触发，请检查打印机状态')
      
      // 这里可以实现实际的打印功能，例如使用window.print()或生成PDF
      const printWindow = window.open('', '_blank')
      if (printWindow) {
        const html = `
          <html>
            <head>
              <title>病历详情 - ${record.id}</title>
              <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { text-align: center; }
                .section { margin-bottom: 20px; }
                .section-title { font-weight: bold; border-bottom: 1px solid #ccc; padding-bottom: 5px; }
                .item { display: flex; margin: 10px 0; }
                .label { width: 120px; font-weight: bold; }
                .value { flex: 1; }
              </style>
            </head>
            <body>
              <h1>智能齿科就诊系统 - 病历详情</h1>
              
              <div class="section">
                <div class="section-title">基本信息</div>
                <div class="item">
                  <div class="label">病历号:</div>
                  <div class="value">${record.id}</div>
                </div>
                <div class="item">
                  <div class="label">患者姓名:</div>
                  <div class="value">${record.patientName}</div>
                </div>
                <div class="item">
                  <div class="label">主诊医生:</div>
                  <div class="value">${this.currentUser.realName}</div>
                </div>
                <div class="item">
                  <div class="label">就诊日期:</div>
                  <div class="value">${this.formatDateTime(record.visitDate)}</div>
                </div>
              </div>
              
              <div class="section">
                <div class="section-title">诊断信息</div>
                <div class="item">
                  <div class="label">主诉:</div>
                  <div class="value">${record.chiefComplaint || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">诊断结果:</div>
                  <div class="value">${record.diagnosis || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">治疗方案:</div>
                  <div class="value">${record.treatmentPlan || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">处方:</div>
                  <div class="value">${record.prescription || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">检查建议:</div>
                  <div class="value">${record.examinationAdvice || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">注意事项:</div>
                  <div class="value">${record.precautions || '无'}</div>
                </div>
                <div class="item">
                  <div class="label">随访计划:</div>
                  <div class="value">${record.followUpPlan || '无'}</div>
                </div>
              </div>
              
              <div style="text-align: center; margin-top: 50px;">
                <p>医生签名: ____________</p>
                <p>打印日期: ${new Date().toLocaleDateString()}</p>
              </div>
            </body>
          </html>
        `
        printWindow.document.write(html)
        printWindow.document.close()
        printWindow.onload = function() {
          printWindow.print()
          // printWindow.close()
        }
      }
    },
    
    // 刷新病历记录
    refreshRecords() {
      this.fetchRecords()
    },
    
    // 处理筛选条件变更
    handleFilterChange() {
      this.currentPage = 1
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        dateRange: null,
        patientName: ''
      }
      this.handleFilterChange()
    },
    
    // 处理分页变更
    handleCurrentChange(page) {
      this.currentPage = page
    },
    
    // 格式化日期时间
    formatDateTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.medical-records-container {
  height: 100%;
}

.filter-card,
.records-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.ellipsis {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
</style>
