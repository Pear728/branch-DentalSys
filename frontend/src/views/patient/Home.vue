<template>
  <div class="patient-home">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="welcome-card">
          <div class="welcome-message">
            <h2>您好，{{ currentUser.realName }}！</h2>
            <p>欢迎使用智能齿科就诊系统患者端</p>
          </div>
          <div class="welcome-tips">
            <h3>使用提示：</h3>
            <ul>
              <li>您可以通过"在线预约"功能选择医生和时段进行挂号</li>
              <li>在"病历查询"中可以查看您的历史就诊记录</li>
              <li>"缴费管理"可以处理您的待缴费项目</li>
              <li>有任何问题请联系医院客服电话：0755-12345678</li>
            </ul>
          </div>
        </el-card>
        
        <el-card class="announcement-card">
          <div slot="header">
            <span>医院公告</span>
          </div>
          <div class="announcement-content">
            <div class="announcement-item" v-for="(item, index) in announcements" :key="index">
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
              <div class="announcement-time">{{ item.time }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="appointment-card">
          <div slot="header">
            <span>近期预约</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToAppointment">
              <i class="el-icon-plus"></i> 新预约
            </el-button>
          </div>
          <div v-if="recentAppointments.length > 0">
            <div class="appointment-item" v-for="appointment in recentAppointments" :key="appointment.id">
              <div class="appointment-info">
                <div class="appointment-doctor">{{ appointment.doctorName }}</div>
                <div class="appointment-time">{{ appointment.appointmentDate }} {{ appointment.appointmentTime }}</div>
              </div>
              <div class="appointment-status" :class="getStatusClass(appointment.status)">
                {{ getStatusText(appointment.status) }}
              </div>
            </div>
          </div>
          <div v-else class="empty-data">
            <i class="el-icon-date"></i>
            <p>暂无近期预约</p>
          </div>
        </el-card>
        
        <el-card class="payment-card">
          <div slot="header">
            <span>缴费提醒</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToPayment">
              查看全部
            </el-button>
          </div>
          <div v-if="unpaidAppointments.length > 0">
            <div class="payment-item" v-for="item in unpaidAppointments" :key="item.id">
              <div class="payment-info">
                <div class="payment-title">预约号: {{ item.id }}</div>
                <div class="payment-amount">金额: ¥{{ item.paymentAmount }}</div>
              </div>
              <el-button size="mini" type="primary" @click="handlePay(item)">缴费</el-button>
            </div>
          </div>
          <div v-else class="empty-data">
            <i class="el-icon-wallet"></i>
            <p>暂无待缴费项目</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PatientHome',
  data() {
    return {
      recentAppointments: [],
      unpaidAppointments: [],
      doctorList: [],
      announcements: [
        {
          title: '口腔健康日活动通知',
          content: '9月20日是全国爱牙日，我院将举办免费口腔检查活动，欢迎广大市民参与。',
          time: '2025-04-10'
        },
        {
          title: '系统维护通知',
          content: '系统将于本周六凌晨2:00-4:00进行维护升级，期间预约功能暂停使用。',
          time: '2025-04-12'
        },
        {
          title: '新设备投入使用',
          content: '我院新引进的德国3D口腔扫描仪已正式投入使用，为患者提供更精准的诊疗方案。',
          time: '2025-04-14'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  mounted() {
    this.fetchDoctors()
    this.fetchRecentAppointments()
    this.fetchUnpaidAppointments()
  },
  methods: {
    // 获取医生列表
    fetchDoctors() {
      this.$http.get('/user/doctors')
        .then(response => {
          if (response.data.code === 200) {
            this.doctorList = response.data.data || []
          }
        })
        .catch(error => {
          console.error('获取医生列表错误:', error)
        })
    },
    
    fetchRecentAppointments() {
      // 获取患者近期预约
      this.$http.get(`/appointment/patient/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 获取最近5条预约记录
            this.recentAppointments = response.data.data
              .slice(0, 5)
              .map(item => {
                // 通过医生列表查找医生名称
                const doctor = this.doctorList.find(doc => doc.id === item.doctorId)
                return {
                  ...item,
                  doctorName: doctor ? doctor.realName : `医生`
                }
              })
          }
        })
        .catch(error => {
          console.error('获取预约记录失败:', error)
        })
    },
    fetchUnpaidAppointments() {
      // 获取患者未支付的预约
      this.$http.get(`/appointment/patient/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            // 筛选未支付的预约
            this.unpaidAppointments = response.data.data
              .filter(item => item.paymentStatus === 0) // 使用paymentStatus而不是isPaid
              .slice(0, 3)
          }
        })
        .catch(error => {
          console.error('获取未支付预约失败:', error)
        })
    },
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    getStatusText(status) {
      switch (status) {
        case 0: return '待确认'
        case 1: return '已确认'
        case 2: return '已完成'
        case 3: return '已取消'
        default: return '未知状态'
      }
    },
    getStatusClass(status) {
      switch (status) {
        case 0: return 'status-pending'
        case 1: return 'status-confirmed'
        case 2: return 'status-completed'
        case 3: return 'status-canceled'
        default: return ''
      }
    },
    goToAppointment() {
      this.$router.push('/patient/appointment')
    },
    goToPayment() {
      this.$router.push('/patient/payment')
    },
    handlePay(item) {
      this.$router.push({
        path: '/patient/payment',
        query: { appointmentId: item.id }
      })
    }
  }
}
</script>

<style scoped>
.patient-home {
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

.announcement-card,
.appointment-card,
.payment-card {
  margin-bottom: 20px;
}

.announcement-item {
  padding-bottom: 15px;
  margin-bottom: 15px;
  border-bottom: 1px dashed #eee;
}

.announcement-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.announcement-item h4 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #333;
}

.announcement-time {
  text-align: right;
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

.appointment-item,
.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.appointment-item:last-child,
.payment-item:last-child {
  border-bottom: none;
}

.appointment-doctor,
.payment-title {
  font-weight: bold;
}

.appointment-time,
.payment-amount {
  color: #666;
  font-size: 13px;
  margin-top: 5px;
}

.appointment-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-pending {
  background-color: #e6a23c;
  color: white;
}

.status-confirmed {
  background-color: #409eff;
  color: white;
}

.status-completed {
  background-color: #67c23a;
  color: white;
}

.status-canceled {
  background-color: #f56c6c;
  color: white;
}

.empty-data {
  text-align: center;
  padding: 20px 0;
  color: #909399;
}

.empty-data i {
  font-size: 30px;
  margin-bottom: 10px;
}
</style>
