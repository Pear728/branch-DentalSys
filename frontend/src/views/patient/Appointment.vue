<template>
  <div class="appointment-container">
    <el-card class="appointment-card">
      <div slot="header">
        <span>在线预约挂号</span>
      </div>
      
      <el-form :model="appointmentForm" :rules="rules" ref="appointmentForm" label-width="100px">
        <el-form-item label="就诊项目" prop="treatmentItem">
          <el-select v-model="appointmentForm.treatmentItem" placeholder="请选择就诊项目" style="width: 100%">
            <el-option label="普通检查" value="普通检查"></el-option>
            <el-option label="口腔洁牙" value="口腔洁牙"></el-option>
            <el-option label="牙齿修复" value="牙齿修复"></el-option>
            <el-option label="牙齿矫正" value="牙齿矫正"></el-option>
            <el-option label="智齿拔除" value="智齿拔除"></el-option>
            <el-option label="儿童口腔" value="儿童口腔"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择医生" prop="doctorId">
          <el-select v-model="appointmentForm.doctorId" placeholder="请选择医生" style="width: 100%" @change="handleDoctorChange">
            <el-option
              v-for="doctor in doctorList"
              :key="doctor.id"
              :label="doctor.realName + ' - ' + doctor.title"
              :value="doctor.id">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker
            v-model="appointmentForm.appointmentDate"
            type="date"
            placeholder="选择预约日期"
            :picker-options="datePickerOptions"
            style="width: 100%"
            @change="handleDateChange">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="可选时段" prop="timeSlot">
          <el-radio-group v-model="appointmentForm.timeSlot">
            <el-radio-button 
              v-for="(schedule, index) in availableSchedules" 
              :key="index" 
              :label="schedule.startTime + '-' + schedule.endTime"
              :disabled="schedule.appointedCount >= schedule.maxAppointments">
              {{ schedule.startTime }} - {{ schedule.endTime }}
              (剩余: {{ schedule.maxAppointments - schedule.appointedCount }})
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="症状描述" prop="symptomDesc">
          <el-input
            type="textarea"
            v-model="appointmentForm.symptomDesc"
            placeholder="请简要描述您的症状或需求"
            :rows="4">
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitAppointment" :loading="loading">提交预约</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="my-appointments-card">
      <div slot="header">
        <span>我的预约记录</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshAppointments">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      
      <el-table :data="myAppointments" border style="width: 100%">
        <el-table-column prop="id" label="预约号" width="80"></el-table-column>
        <el-table-column prop="treatmentItem" label="就诊项目" width="120"></el-table-column>
        <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template slot-scope="scope">
            {{ scope.row.appointmentDate }} {{ scope.row.appointmentTime }}
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
              type="danger"
              :disabled="scope.row.status === 2 || scope.row.status === 3"
              @click="cancelAppointment(scope.row)">取消</el-button>
            <el-button
              size="mini"
              type="primary"
              v-if="scope.row.paymentStatus === 0"
              @click="payAppointment(scope.row)">支付</el-button>
            <el-button
              size="mini"
              type="info"
              v-if="scope.row.status === 2"
              @click="viewMedicalRecord(scope.row)">查看病历</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PatientAppointment',
  data() {
    return {
      appointmentForm: {
        treatmentItem: '',
        doctorId: '',
        appointmentDate: '',
        timeSlot: '',
        symptomDesc: ''
      },
      rules: {
        treatmentItem: [
          { required: true, message: '请选择就诊项目', trigger: 'change' }
        ],
        doctorId: [
          { required: true, message: '请选择医生', trigger: 'change' }
        ],
        appointmentDate: [
          { required: true, message: '请选择预约日期', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择预约时段', trigger: 'change' }
        ]
      },
      doctorList: [],
      availableSchedules: [],
      myAppointments: [],
      loading: false,
      datePickerOptions: {
        disabledDate(time) {
          // 禁用过去的日期和90天以后的日期
          const today = new Date()
          today.setHours(0, 0, 0, 0)
          const maxDate = new Date()
          maxDate.setDate(maxDate.getDate() + 90)
          return time.getTime() < today.getTime() || time.getTime() > maxDate.getTime()
        }
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  mounted() {
    this.fetchDoctors()
    this.fetchMyAppointments()
  },
  methods: {
    // 获取医生列表
    fetchDoctors() {
      this.$http.get('/user/doctors')
        .then(response => {
          if (response.data.code === 200) {
            this.doctorList = response.data.data
          } else {
            this.$message.error(response.data.message || '获取医生列表失败')
          }
        })
        .catch(error => {
          console.error('获取医生列表错误:', error)
          this.$message.error('获取医生列表失败，请稍后重试')
        })
    },
    
    // 医生变更处理
    handleDoctorChange() {
      // 如果已选择了日期，则重新获取该医生在该日期的排班
      if (this.appointmentForm.appointmentDate) {
        this.fetchDoctorSchedules()
      }
    },
    
    // 日期变更处理
    handleDateChange() {
      if (this.appointmentForm.doctorId) {
        this.fetchDoctorSchedules()
      }
    },
    
    // 获取医生排班
    fetchDoctorSchedules() {
      if (!this.appointmentForm.doctorId || !this.appointmentForm.appointmentDate) {
        return
      }
      
      // 格式化日期
      const date = this.formatDateToString(this.appointmentForm.appointmentDate)
      
      this.$http.get(`/schedule/doctor/${this.appointmentForm.doctorId}/date?date=${date}`)
        .then(response => {
          if (response.data.code === 200) {
            this.availableSchedules = response.data.data
            
            // 如果没有可用排班
            if (this.availableSchedules.length === 0) {
              this.$message.warning('所选医生在该日期没有出诊安排，请选择其他日期')
            }
            
            // 重置时段选择
            this.appointmentForm.timeSlot = ''
          } else {
            this.$message.error(response.data.message || '获取排班信息失败')
          }
        })
        .catch(error => {
          console.error('获取排班信息错误:', error)
          this.$message.error('获取排班信息失败，请稍后重试')
        })
    },
    
    // 获取我的预约记录
    fetchMyAppointments() {
      this.$http.get(`/appointment/patient/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 处理预约记录数据，添加医生姓名
            this.myAppointments = response.data.data.map(item => {
              // 实际应用中需要从医生列表中查找对应医生的姓名
              const doctor = this.doctorList.find(doc => doc.id === item.doctorId)
              return {
                ...item,
                doctorName: doctor ? doctor.realName : `医生`
              }
            })
          } else {
            this.$message.error(response.data.message || '获取预约记录失败')
          }
        })
        .catch(error => {
          console.error('获取预约记录错误:', error)
          this.$message.error('获取预约记录失败，请稍后重试')
        })
    },
    
    // 提交预约
    submitAppointment() {
      this.$refs.appointmentForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // 将日期和时段组合成预约时间
          const [startTime] = this.appointmentForm.timeSlot.split('-')
          const appointmentDate = new Date(this.appointmentForm.appointmentDate)
          const [hours, minutes] = startTime.split(':').map(Number)
          appointmentDate.setHours(hours, minutes, 0, 0)
          
          // 获取当前选择的时间段对应的排班
          const selectedTimeSlot = this.appointmentForm.timeSlot;
          const selectedSchedule = this.availableSchedules.find(schedule => 
            `${schedule.startTime}-${schedule.endTime}` === selectedTimeSlot
          );
          
          if (!selectedSchedule || !selectedSchedule.id) {
            this.$message.error('无法获取排班信息，请重新选择时间段');
            this.loading = false;
            return;
          }
          
          // 构建预约数据
          const appointmentData = {
            patientId: this.currentUser.id,
            doctorId: this.appointmentForm.doctorId,
            scheduleId: selectedSchedule.id, // 添加排班 ID
            appointmentDate: this.formatDateToString(this.appointmentForm.appointmentDate), // 日期部分
            appointmentTime: this.appointmentForm.timeSlot, // 时间段部分
            treatmentItem: this.appointmentForm.treatmentItem,
            symptomDesc: this.appointmentForm.symptomDesc,
            status: 0, // 待确认
            paymentStatus: 0 // 未支付
          }
          
          // 发送预约请求
          this.$http.post('/appointment', appointmentData)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success(response.data.message || '预约提交成功')
                this.resetForm()
                this.fetchMyAppointments() // 刷新预约列表
              } else {
                this.$message.error(response.data.message || '预约提交失败')
              }
            })
            .catch(error => {
              console.error('提交预约错误:', error)
              this.$message.error('预约提交失败，请稍后重试')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    
    // 取消预约
    cancelAppointment(row) {
      this.$confirm('确定要取消此预约吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.put(`/appointment/${row.id}/cancel`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('预约已取消')
              this.fetchMyAppointments() // 刷新预约列表
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
    
    // 支付预约费用
    payAppointment(row) {
      // 弹出支付确认对话框
      this.$confirm('是否立即支付该预约费用？', '支付确认', {
        confirmButtonText: '立即支付',
        cancelButtonText: '去缴费管理',
        type: 'info'
      }).then(() => {
        // 直接在预约页面支付
        const amount = this.getFeeByTreatment(row.treatmentItem)
        this.$http.put(`/appointment/${row.id}/pay?amount=${amount}`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('支付成功')
              this.fetchMyAppointments() // 刷新预约列表
            } else {
              this.$message.error(response.data.message || '支付失败')
            }
          })
          .catch(error => {
            console.error('支付错误:', error)
            this.$message.error('支付失败，请稍后重试')
          })
      }).catch(() => {
        // 去缴费管理页面
        this.$router.push({
          path: '/patient/payment',
          query: { appointmentId: row.id }
        })
      })
    },
    
    // 根据治疗项目获取费用
    getFeeByTreatment(treatment) {
      const fees = {
        '普通检查': 100,
        '洗牙': 200,
        '补牙': 500,
        '拔牙': 300,
        '正牙': 5000,
        '种植牙': 8000
      }
      return fees[treatment] || 300 // 默认300元
    },
    
    // 查看病历
    viewMedicalRecord(row) {
      // 跳转到病历详情页
      this.$router.push({
        path: '/patient/records',
        query: { appointmentId: row.id }
      })
    },
    
    // 刷新预约记录
    refreshAppointments() {
      this.fetchMyAppointments()
    },
    
    // 重置表单
    resetForm() {
      this.$refs.appointmentForm.resetFields()
      this.availableSchedules = []
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 格式化日期为字符串 yyyy-MM-dd
    formatDateToString(date) {
      const d = new Date(date)
      return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
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
.appointment-container {
  height: 100%;
}

.appointment-card {
  margin-bottom: 20px;
}

.my-appointments-card {
  margin-bottom: 20px;
}

/* 修复时段选择按钮组宽度问题 */
.el-radio-button {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>
