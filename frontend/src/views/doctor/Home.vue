<template>
  <div class="doctor-home">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="welcome-card">
          <div class="welcome-message">
            <h2>您好，{{ currentUser.realName }} 医生！</h2>
            <p>欢迎使用智能齿科就诊系统医生端</p>
          </div>
          <div class="welcome-tips">
            <h3>使用提示：</h3>
            <ul>
              <li>您可以在"预约患者"中查看和管理今日的预约</li>
              <li>"诊疗记录"中可以录入和查看患者的诊断记录</li>
              <li>"排班管理"可以设置您的出诊时间</li>
              <li>有任何问题请联系系统管理员</li>
            </ul>
          </div>
        </el-card>
        
        <el-card class="today-card">
          <div slot="header">
            <span>今日概览</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshTodayData">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          <el-row :gutter="20" class="data-overview">
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-primary">
                  <i class="el-icon-user"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">今日预约</div>
                  <div class="data-value">{{ todayAppointments.length }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-success">
                  <i class="el-icon-finished"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">已完成</div>
                  <div class="data-value">{{ getCompletedAppointments().length }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-warning">
                  <i class="el-icon-time"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">待就诊</div>
                  <div class="data-value">{{ getPendingAppointments().length }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <el-card class="schedule-card">
          <div slot="header">
            <span>我的排班</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToSchedule">
              管理排班
            </el-button>
          </div>
          <div class="schedule-content">
            <el-calendar v-model="currentDate">
              <template slot="dateCell" slot-scope="{date, data}">
                <div class="calendar-cell" :class="{'has-schedule': hasSchedule(data.day)}">
                  <div class="calendar-day">{{ data.day.split('-')[2] }}</div>
                  <div v-if="hasSchedule(data.day)" class="schedule-time">
                    {{ getScheduleTime(data.day) }}
                  </div>
                </div>
              </template>
            </el-calendar>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="today-appointments-card">
          <div slot="header">
            <span>今日预约患者</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToAppointments">
              查看全部
            </el-button>
          </div>
          <div v-if="todayAppointments.length > 0" class="appointments-list">
            <div 
              v-for="appointment in todayAppointments" 
              :key="appointment.id" 
              class="appointment-item"
              :class="{'completed': appointment.status === 2}">
              <div class="appointment-time">{{ formatTime(appointment.appointmentTime) }}</div>
              <div class="appointment-info">
                <div class="patient-name">{{ appointment.patientName }}</div>
                <div class="treatment-item">{{ appointment.treatmentItem }}</div>
              </div>
              <div class="appointment-status">
                <el-tag :type="getStatusType(appointment.status)" size="mini">
                  {{ getStatusText(appointment.status) }}
                </el-tag>
              </div>
            </div>
          </div>
          <div v-else class="empty-data">
            <i class="el-icon-date"></i>
            <p>今日暂无预约患者</p>
          </div>
        </el-card>
        
        <el-card class="recent-records-card">
          <div slot="header">
            <span>最近诊疗记录</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToRecords">
              查看全部
            </el-button>
          </div>
          <div v-if="recentRecords.length > 0" class="records-list">
            <div v-for="record in recentRecords" :key="record.id" class="record-item">
              <div class="record-date">{{ formatDate(record.visitDate) }}</div>
              <div class="record-info">
                <div class="patient-name">{{ record.patientName }}</div>
                <div class="diagnosis">{{ record.diagnosis }}</div>
              </div>
            </div>
          </div>
          <div v-else class="empty-data">
            <i class="el-icon-document"></i>
            <p>暂无诊疗记录</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DoctorHome',
  data() {
    return {
      todayAppointments: [],
      recentRecords: [],
      schedules: [],
      currentDate: new Date(),
      patientList: []
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  mounted() {
    this.fetchPatients()
    this.fetchTodayAppointments()
    this.fetchRecentRecords()
    this.fetchSchedules()
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
    
    // 获取今日预约
    fetchTodayAppointments() {
      const today = new Date()
      const formattedDate = this.formatDateToString(today)
      
      this.$http.get(`/appointment/doctor/${this.currentUser.id}/date?date=${formattedDate}`)
        .then(response => {
          if (response.data.code === 200) {
            // 处理预约数据，添加患者姓名
            this.todayAppointments = response.data.data.map(item => {
              const patient = this.patientList.find(p => p.id === item.patientId)
              return {
                ...item,
                patientName: patient ? patient.realName : `患者(ID:${item.patientId})`
              }
            })
            
            // 按预约时间排序
            this.todayAppointments.sort((a, b) => {
              return new Date(a.appointmentTime) - new Date(b.appointmentTime)
            })
          }
        })
        .catch(error => {
          console.error('获取今日预约错误:', error)
        })
    },
    
    // 获取最近诊疗记录
    fetchRecentRecords() {
      this.$http.get(`/medical-record/doctor/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 只获取最近5条记录
            const records = response.data.data.slice(0, 5)
            
            // 添加患者姓名
            this.recentRecords = records.map(record => {
              const patient = this.patientList.find(p => p.id === record.patientId)
              return {
                ...record,
                patientName: patient ? patient.realName : `患者(ID:${record.patientId})`
              }
            })
          }
        })
        .catch(error => {
          console.error('获取诊疗记录错误:', error)
        })
    },
    
    // 获取排班信息
    fetchSchedules() {
      this.$http.get(`/schedule/doctor/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            this.schedules = response.data.data
          }
        })
        .catch(error => {
          console.error('获取排班信息错误:', error)
        })
    },
    
    // 刷新今日数据
    refreshTodayData() {
      this.fetchTodayAppointments()
      this.fetchRecentRecords()
    },
    
    // 获取已完成预约
    getCompletedAppointments() {
      return this.todayAppointments.filter(item => item.status === 2)
    },
    
    // 获取待就诊预约
    getPendingAppointments() {
      return this.todayAppointments.filter(item => item.status === 0 || item.status === 1)
    },
    
    // 检查指定日期是否有排班
    hasSchedule(day) {
      return this.schedules.some(schedule => {
        const scheduleDate = this.formatDateToString(new Date(schedule.scheduleDate))
        return scheduleDate === day
      })
    },
    
    // 获取指定日期的排班时间
    getScheduleTime(day) {
      const schedule = this.schedules.find(schedule => {
        const scheduleDate = this.formatDateToString(new Date(schedule.scheduleDate))
        return scheduleDate === day
      })
      
      if (schedule) {
        return `${schedule.startTime}-${schedule.endTime}`
      }
      
      return ''
    },
    
    // 格式化时间为 HH:MM
    formatTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 格式化日期为 YYYY-MM-DD
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
    },
    
    // 格式化日期为字符串 YYYY-MM-DD
    formatDateToString(date) {
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
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
    },
    
    // 跳转到预约管理
    goToAppointments() {
      this.$router.push('/doctor/appointments')
    },
    
    // 跳转到诊疗记录
    goToRecords() {
      this.$router.push('/doctor/records')
    },
    
    // 跳转到排班管理
    goToSchedule() {
      this.$router.push('/doctor/schedule')
    }
  }
}
</script>

<style scoped>
.doctor-home {
  height: 100%;
}

.welcome-card {
  margin-bottom: 20px;
  background-color: #f0f9eb;
}

.welcome-message {
  margin-bottom: 20px;
}

.welcome-message h2 {
  margin-top: 0;
  color: #67c23a;
}

.welcome-tips ul {
  padding-left: 20px;
}

.welcome-tips li {
  margin-bottom: 8px;
}

.today-card,
.schedule-card,
.today-appointments-card,
.recent-records-card {
  margin-bottom: 20px;
}

.data-overview {
  padding: 10px 0;
}

.data-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 4px;
  background-color: #f8f8f8;
}

.data-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.data-info {
  flex: 1;
}

.data-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.bg-primary {
  background-color: #409eff;
}

.bg-success {
  background-color: #67c23a;
}

.bg-warning {
  background-color: #e6a23c;
}

.calendar-cell {
  height: 100%;
  padding: 4px;
  text-align: center;
  position: relative;
}

.calendar-day {
  font-weight: bold;
}

.has-schedule {
  background-color: #f0f9eb;
}

.schedule-time {
  font-size: 12px;
  color: #67c23a;
  margin-top: 2px;
}

.appointments-list,
.records-list {
  max-height: 400px;
  overflow-y: auto;
}

.appointment-item,
.record-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.appointment-item:last-child,
.record-item:last-child {
  border-bottom: none;
}

.appointment-time,
.record-date {
  width: 60px;
  font-weight: bold;
  color: #606266;
}

.appointment-info,
.record-info {
  flex: 1;
  margin: 0 10px;
}

.patient-name {
  font-weight: bold;
  margin-bottom: 3px;
}

.treatment-item,
.diagnosis {
  font-size: 13px;
  color: #606266;
}

.completed {
  opacity: 0.7;
}

.empty-data {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.empty-data i {
  font-size: 30px;
  margin-bottom: 10px;
}
</style>
