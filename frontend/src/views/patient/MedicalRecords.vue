<template>
  <div class="medical-records-container">
    <el-card class="medical-records-card">
      <div slot="header">
        <span>我的病历记录</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshRecords">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      
      <el-table
        :data="medicalRecords"
        border
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载中...">
        <el-table-column prop="id" label="病历号" width="80"></el-table-column>
        <el-table-column prop="visitDate" label="就诊日期" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.visitDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="主诊医生" width="120"></el-table-column>
        <el-table-column prop="diagnosis" label="诊断结果" width="200"></el-table-column>
        <el-table-column prop="treatmentPlan" label="治疗方案" width="200"></el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="viewRecordDetail(scope.row)">详情</el-button>
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

    <!-- 病历详情对话框 -->
    <el-dialog title="病历详情" :visible.sync="dialogVisible" width="70%">
      <div v-if="currentRecord" class="record-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="病历号">{{ currentRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="主诊医生">{{ currentRecord.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ formatDate(currentRecord.visitDate) }}</el-descriptions-item>
          <el-descriptions-item label="就诊项目">{{ currentRecord.appointmentInfo ? currentRecord.appointmentInfo.treatmentItem : '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <el-divider></el-divider>
        
        <el-descriptions title="诊断信息" :column="1" border>
          <el-descriptions-item label="主诉">{{ currentRecord.chiefComplaint || '无' }}</el-descriptions-item>
          <el-descriptions-item label="诊断结果">{{ currentRecord.diagnosis || '无' }}</el-descriptions-item>
          <el-descriptions-item label="治疗方案">{{ currentRecord.treatmentPlan || '无' }}</el-descriptions-item>
          <el-descriptions-item label="处方">{{ currentRecord.prescription || '无' }}</el-descriptions-item>
          <el-descriptions-item label="检查建议">{{ currentRecord.examinationAdvice || '无' }}</el-descriptions-item>
          <el-descriptions-item label="注意事项">{{ currentRecord.precautions || '无' }}</el-descriptions-item>
          <el-descriptions-item label="随访计划">{{ currentRecord.followUpPlan || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="printRecord">打印</el-button>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PatientMedicalRecords',
  data() {
    return {
      medicalRecords: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      currentRecord: null,
      doctorList: [],
      appointmentId: null // 用于直接查看特定预约的病历
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  created() {
    // 检查URL参数是否有appointmentId
    if (this.$route.query.appointmentId) {
      this.appointmentId = this.$route.query.appointmentId
    }
  },
  mounted() {
    this.fetchDoctors()
    this.fetchMedicalRecords()
    
    // 如果有特定预约ID，直接查看该预约的病历
    if (this.appointmentId) {
      this.viewRecordByAppointmentId(this.appointmentId)
    }
  },
  methods: {
    // 获取医生列表
    fetchDoctors() {
      this.$http.get('/user/doctors')
        .then(response => {
          if (response.data.code === 200) {
            this.doctorList = response.data.data
          }
        })
        .catch(error => {
          console.error('获取医生列表错误:', error)
        })
    },
    
    // 获取病历记录
    fetchMedicalRecords() {
      this.loading = true
      this.$http.get(`/medical-record/patient/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 处理病历数据，添加医生姓名
            this.medicalRecords = response.data.data.map(record => {
              const doctor = this.doctorList.find(doc => doc.id === record.doctorId)
              return {
                ...record,
                doctorName: doctor ? doctor.realName : `医生(ID:${record.doctorId})`
              }
            })
            this.total = this.medicalRecords.length
          } else {
            this.$message.error(response.data.message || '获取病历记录失败')
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
    
    // 根据预约ID查看病历
    viewRecordByAppointmentId(appointmentId) {
      this.$http.get(`/medical-record/appointment/${appointmentId}`)
        .then(response => {
          if (response.data.code === 200) {
            const record = response.data.data
            const doctor = this.doctorList.find(doc => doc.id === record.doctorId)
            
            // 获取预约信息
            this.$http.get(`/appointment/${record.appointmentId}`)
              .then(appointmentResponse => {
                if (appointmentResponse.data.code === 200) {
                  this.currentRecord = {
                    ...record,
                    doctorName: doctor ? doctor.realName : `医生(ID:${record.doctorId})`,
                    appointmentInfo: appointmentResponse.data.data
                  }
                  this.dialogVisible = true
                }
              })
              .catch(error => {
                console.error('获取预约信息错误:', error)
                
                // 即使获取预约信息失败，仍然显示病历详情
                this.currentRecord = {
                  ...record,
                  doctorName: doctor ? doctor.realName : `医生(ID:${record.doctorId})`
                }
                this.dialogVisible = true
              })
          } else {
            this.$message.error(response.data.message || '获取病历详情失败')
          }
        })
        .catch(error => {
          console.error('获取病历详情错误:', error)
          this.$message.error('获取病历详情失败，请稍后重试')
        })
    },
    
    // 查看病历详情
    viewRecordDetail(record) {
      // 获取预约信息
      if (record.appointmentId) {
        this.$http.get(`/appointment/${record.appointmentId}`)
          .then(response => {
            if (response.data.code === 200) {
              this.currentRecord = {
                ...record,
                appointmentInfo: response.data.data
              }
            } else {
              this.currentRecord = record
            }
            this.dialogVisible = true
          })
          .catch(error => {
            console.error('获取预约信息错误:', error)
            this.currentRecord = record
            this.dialogVisible = true
          })
      } else {
        this.currentRecord = record
        this.dialogVisible = true
      }
    },
    
    // 刷新病历记录
    refreshRecords() {
      this.fetchMedicalRecords()
    },
    
    // 分页处理
    handleCurrentChange(page) {
      this.currentPage = page
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 打印病历
    printRecord() {
      this.$message.success('打印功能已触发，请检查打印机状态')
      
      // 这里可以实现实际的打印功能，例如使用window.print()或生成PDF
      const printWindow = window.open('', '_blank')
      if (printWindow) {
        const record = this.currentRecord
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
                  <div class="value">${this.currentUser.realName}</div>
                </div>
                <div class="item">
                  <div class="label">主诊医生:</div>
                  <div class="value">${record.doctorName}</div>
                </div>
                <div class="item">
                  <div class="label">就诊日期:</div>
                  <div class="value">${this.formatDate(record.visitDate)}</div>
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
                <p>医院签章: ____________</p>
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
    }
  }
}
</script>

<style scoped>
.medical-records-container {
  height: 100%;
}

.medical-records-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.record-detail {
  padding: 0 20px;
}

.el-descriptions {
  margin-bottom: 20px;
}
</style>
