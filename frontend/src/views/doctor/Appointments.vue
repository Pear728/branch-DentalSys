<template>
  <div class="appointments-container">
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
            @change="handleDateChange">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable @change="handleFilterChange">
            <el-option label="待确认" :value="0"></el-option>
            <el-option label="已确认" :value="1"></el-option>
            <el-option label="已完成" :value="2"></el-option>
            <el-option label="已取消" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilterChange">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="今日预约" name="today">
        <el-card class="appointments-card">
          <div slot="header">
            <span>今日预约列表</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshAppointments">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          
          <el-table
            :data="todayAppointments"
            v-loading="loading"
            border
            style="width: 100%">
            <el-table-column prop="id" label="预约号" width="80"></el-table-column>
            <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
            <el-table-column label="联系方式" width="120">
              <template slot-scope="scope">
                {{ scope.row.patientPhone || '无' }}
              </template>
            </el-table-column>
            <el-table-column prop="appointmentTimeFormatted" label="预约时间" width="180">
              <template slot-scope="scope">
                {{ scope.row.appointmentTimeFormatted || `${scope.row.appointmentDate} ${scope.row.appointmentTime}` }}
              </template>
            </el-table-column>
            <el-table-column prop="treatmentItem" label="就诊项目" width="120"></el-table-column>
            <el-table-column prop="symptomDesc" label="症状描述" width="200">
              <template slot-scope="scope">
                <el-tooltip v-if="scope.row.symptomDesc" :content="scope.row.symptomDesc" placement="top">
                  <div class="ellipsis">{{ scope.row.symptomDesc }}</div>
                </el-tooltip>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentStatus" label="支付状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.paymentStatus === 1 ? 'success' : 'danger'">
                  {{ scope.row.paymentStatus === 1 ? '已支付' : '未支付' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  v-if="scope.row.status === 0"
                  @click="confirmAppointment(scope.row)">确认</el-button>
                <el-button
                  size="mini"
                  type="success"
                  v-if="scope.row.status === 1"
                  @click="createMedicalRecord(scope.row)">录入病历</el-button>
                <el-button
                  size="mini"
                  type="info"
                  v-if="scope.row.status === 2"
                  @click="viewMedicalRecord(scope.row)">查看病历</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  v-if="scope.row.status === 0 || scope.row.status === 1"
                  @click="cancelAppointment(scope.row)">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="全部预约" name="all">
        <el-card class="appointments-card">
          <div slot="header">
            <span>预约列表</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshAppointments">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          
          <el-table
            :data="filteredAppointments"
            v-loading="loading"
            border
            style="width: 100%">
            <el-table-column prop="id" label="预约号" width="80"></el-table-column>
            <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
            <el-table-column label="联系方式" width="120">
              <template slot-scope="scope">
                {{ scope.row.patientPhone || '无' }}
              </template>
            </el-table-column>
            <el-table-column prop="appointmentTimeFormatted" label="预约时间" width="180">
              <template slot-scope="scope">
                {{ scope.row.appointmentTimeFormatted || `${scope.row.appointmentDate} ${scope.row.appointmentTime}` }}
              </template>
            </el-table-column>
            <el-table-column prop="treatmentItem" label="就诊项目" width="120"></el-table-column>
            <el-table-column prop="symptomDesc" label="症状描述" width="200">
              <template slot-scope="scope">
                <el-tooltip v-if="scope.row.symptomDesc" :content="scope.row.symptomDesc" placement="top">
                  <div class="ellipsis">{{ scope.row.symptomDesc }}</div>
                </el-tooltip>
                <span v-else>无</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentStatus" label="支付状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.paymentStatus === 1 ? 'success' : 'danger'">
                  {{ scope.row.paymentStatus === 1 ? '已支付' : '未支付' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="primary"
                  v-if="scope.row.status === 0"
                  @click="confirmAppointment(scope.row)">确认</el-button>
                <el-button
                  size="mini"
                  type="success"
                  v-if="scope.row.status === 1"
                  @click="createMedicalRecord(scope.row)">录入病历</el-button>
                <el-button
                  size="mini"
                  type="info"
                  v-if="scope.row.status === 2"
                  @click="viewMedicalRecord(scope.row)">查看病历</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  v-if="scope.row.status === 0 || scope.row.status === 1"
                  @click="cancelAppointment(scope.row)">取消</el-button>
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
      </el-tab-pane>
    </el-tabs>
    
    <!-- 录入病历对话框 -->
    <el-dialog title="录入病历" :visible.sync="medicalRecordDialogVisible" width="70%" :close-on-click-modal="false">
      <el-form :model="medicalRecordForm" :rules="medicalRecordRules" ref="medicalRecordForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="患者姓名">
              <el-input v-model="currentPatientName" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就诊时间" prop="visitDate">
              <el-date-picker
                v-model="medicalRecordForm.visitDate"
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
            v-model="medicalRecordForm.chiefComplaint"
            placeholder="请输入患者主诉"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.diagnosis"
            placeholder="请输入诊断结果"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="治疗方案" prop="treatmentPlan">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.treatmentPlan"
            placeholder="请输入治疗方案"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="处方" prop="prescription">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.prescription"
            placeholder="请输入处方"
            :rows="3">
          </el-input>
        </el-form-item>
        
        <el-form-item label="检查建议">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.examinationAdvice"
            placeholder="请输入检查建议"
            :rows="2">
          </el-input>
        </el-form-item>
        
        <el-form-item label="注意事项">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.precautions"
            placeholder="请输入注意事项"
            :rows="2">
          </el-input>
        </el-form-item>
        
        <el-form-item label="随访计划">
          <el-input
            type="textarea"
            v-model="medicalRecordForm.followUpPlan"
            placeholder="请输入随访计划"
            :rows="2">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="medicalRecordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMedicalRecord" :loading="submitLoading">提交</el-button>
      </span>
    </el-dialog>
    
    <!-- 查看病历对话框 -->
    <el-dialog title="病历详情" :visible.sync="viewRecordDialogVisible" width="70%">
      <div v-if="currentRecord" class="record-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="病历号">{{ currentRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="就诊医生">{{ currentUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ formatDateTime(currentRecord.visitDate) }}</el-descriptions-item>
          <el-descriptions-item label="患者姓名">{{ currentPatientName }}</el-descriptions-item>
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
        <el-button @click="viewRecordDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DoctorAppointments',
  data() {
    return {
      activeTab: 'today',
      loading: false,
      submitLoading: false,
      allAppointments: [],
      filterForm: {
        dateRange: null,
        status: null
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      patientList: [],
      
      // 录入病历相关
      medicalRecordDialogVisible: false,
      viewRecordDialogVisible: false,
      currentAppointment: null,
      currentPatientName: '',
      currentRecord: null,
      medicalRecordForm: {
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
      medicalRecordRules: {
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
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    
    // 今日预约
    todayAppointments() {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const todayStr = today.toISOString().split('T')[0] // 获取今天的日期字符串 YYYY-MM-DD
      
      return this.allAppointments.filter(item => {
        // 直接比较appointmentDate字符串
        return item.appointmentDate === todayStr
      })
    },
    
    // 筛选后的预约
    filteredAppointments() {
      let result = [...this.allAppointments]
      
      // 按日期范围筛选
      if (this.filterForm.dateRange && this.filterForm.dateRange.length === 2) {
        const startDateStr = this.filterForm.dateRange[0]
        const endDateStr = this.filterForm.dateRange[1]
        
        result = result.filter(item => {
          // 直接比较日期字符串
          return item.appointmentDate >= startDateStr && item.appointmentDate <= endDateStr
        })
      }
      
      // 按状态筛选
      if (this.filterForm.status !== null) {
        result = result.filter(item => item.status === this.filterForm.status)
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
    // 先获取患者列表，然后再获取预约信息
    this.fetchPatients().then(() => {
      this.fetchAppointments()
    })
  },
  methods: {
    // 获取患者列表
    fetchPatients() {
      return new Promise((resolve) => {
        this.$http.get('/user/search?role=1')
          .then(response => {
            if (response.data.code === 200) {
              this.patientList = response.data.data
              console.log('获取到患者列表:', this.patientList.length, '条记录')
            }
            resolve()
          })
          .catch(error => {
            console.error('获取患者列表错误:', error)
            resolve() // 即使出错也要resolve，确保流程继续
          })
      })
    },
    
    // 获取预约列表
    fetchAppointments() {
      this.loading = true
      console.log('获取医生ID为', this.currentUser.id, '的预约')
      this.$http.get(`/appointment/doctor/${this.currentUser.id}`)
        .then(response => {
          console.log('预约数据返回:', response.data)
          if (response.data.code === 200) {
            // 处理预约数据，添加患者信息
            this.allAppointments = response.data.data.map(item => {
              const patient = this.patientList.find(p => p.id === item.patientId)
              // 构建完整的预约时间字符串
              const appointmentDateStr = item.appointmentDate
              const appointmentTimeStr = item.appointmentTime
              return {
                ...item,
                patientName: patient ? patient.realName : `患者(ID:${item.patientId})`,
                patientPhone: patient ? patient.phone : null,
                // 使用appointmentDate和appointmentTime构建完整的时间信息
                appointmentTimeFormatted: `${appointmentDateStr} ${appointmentTimeStr}`
              }
            })
            
            // 按预约时间排序（降序）
            this.allAppointments.sort((a, b) => {
              return new Date(b.appointmentTime) - new Date(a.appointmentTime)
            })
          }
        })
        .catch(error => {
          console.error('获取预约列表错误:', error)
          this.$message.error('获取预约列表失败，请稍后重试')
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 确认预约
    confirmAppointment(appointment) {
      this.$confirm('确定要确认此预约吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$http.put(`/appointment/${appointment.id}/status/1`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('预约已确认')
              // 更新预约状态
              appointment.status = 1
            } else {
              this.$message.error(response.data.message || '确认预约失败')
            }
          })
          .catch(error => {
            console.error('确认预约错误:', error)
            this.$message.error('确认预约失败，请稍后重试')
          })
      }).catch(() => {
        // 取消操作，不做处理
      })
    },
    
    // 取消预约
    cancelAppointment(appointment) {
      this.$confirm('确定要取消此预约吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.put(`/appointment/${appointment.id}/cancel`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('预约已取消')
              // 更新预约状态
              appointment.status = 3
            } else {
              this.$message.error(response.data.message || '取消预约失败')
            }
          })
          .catch(error => {
            console.error('取消预约错误:', error)
            this.$message.error('取消预约失败，请稍后重试')
          })
      }).catch(() => {
        // 取消操作，不做处理
      })
    },
    
    // 录入病历
    createMedicalRecord(appointment) {
      this.currentAppointment = appointment
      const patient = this.patientList.find(p => p.id === appointment.patientId)
      this.currentPatientName = patient ? patient.realName : `患者(ID:${appointment.patientId})`
      
      // 初始化表单
      this.medicalRecordForm = {
        appointmentId: appointment.id,
        patientId: appointment.patientId,
        doctorId: this.currentUser.id,
        visitDate: new Date(),
        chiefComplaint: appointment.symptomDesc || '',
        diagnosis: '',
        treatmentPlan: '',
        prescription: '',
        examinationAdvice: '',
        precautions: '',
        followUpPlan: ''
      }
      
      this.medicalRecordDialogVisible = true
    },
    
    // 提交病历
    submitMedicalRecord() {
      this.$refs.medicalRecordForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          
          this.$http.post('/medical-record', this.medicalRecordForm)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('病历提交成功')
                this.medicalRecordDialogVisible = false
                
                // 更新预约状态为已完成
                this.currentAppointment.status = 2
              } else {
                this.$message.error(response.data.message || '病历提交失败')
              }
            })
            .catch(error => {
              console.error('提交病历错误:', error)
              this.$message.error('病历提交失败，请稍后重试')
            })
            .finally(() => {
              this.submitLoading = false
            })
        }
      })
    },
    
    // 查看病历
    viewMedicalRecord(appointment) {
      this.$http.get(`/medical-record/appointment/${appointment.id}`)
        .then(response => {
          if (response.data.code === 200) {
            this.currentRecord = response.data.data
            const patient = this.patientList.find(p => p.id === appointment.patientId)
            this.currentPatientName = patient ? patient.realName : `患者(ID:${appointment.patientId})`
            this.viewRecordDialogVisible = true
          } else {
            this.$message.error(response.data.message || '获取病历失败')
          }
        })
        .catch(error => {
          console.error('获取病历错误:', error)
          this.$message.error('获取病历失败，请稍后重试')
        })
    },
    
    // 打印病历
    printRecord() {
      this.$message.success('打印功能已触发，请检查打印机状态')
      
      // 这里可以实现实际的打印功能，类似患者端病历打印的实现
    },
    
    // 刷新预约列表
    refreshAppointments() {
      this.fetchAppointments()
    },
    
    // 处理日期变更
    handleDateChange() {
      this.handleFilterChange()
    },
    
    // 处理筛选条件变更
    handleFilterChange() {
      this.currentPage = 1
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        dateRange: null,
        status: null
      }
      this.handleFilterChange()
    },
    
    // 处理分页变更
    handleCurrentChange(page) {
      this.currentPage = page
    },
    
    // 处理标签页切换
    handleTabClick() {
      // 切换到全部预约时，重置筛选条件
      if (this.activeTab === 'all') {
        this.resetFilter()
      }
    },
    
    // 格式化日期时间
    formatDateTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 0: return '待确认'
        case 1: return '已确认'
        case 2: return '已完成'
        case 3: return '已取消'
        default: return '未知状态'
      }
    },
    
    // 获取状态类型（用于标签颜色）
    getStatusType(status) {
      switch (status) {
        case 0: return 'warning'
        case 1: return 'primary'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
      }
    }
  }
}
</script>

<style scoped>
.appointments-container {
  height: 100%;
}

.filter-card,
.appointments-card {
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

.record-detail {
  padding: 0 20px;
}

.el-descriptions {
  margin-bottom: 20px;
}
</style>
