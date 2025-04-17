<template>
  <div class="settings-container">
    <el-card class="main-card">
      <div slot="header">
        <span>系统设置</span>
      </div>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="120px" :rules="basicRules" ref="basicForm">
            <el-form-item label="诊所名称" prop="clinicName">
              <el-input v-model="basicSettings.clinicName"></el-input>
            </el-form-item>
            
            <el-form-item label="诊所电话" prop="clinicPhone">
              <el-input v-model="basicSettings.clinicPhone"></el-input>
            </el-form-item>
            
            <el-form-item label="营业时间" prop="businessHours">
              <el-time-picker
                is-range
                v-model="basicSettings.businessHours"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                style="width: 100%">
              </el-time-picker>
            </el-form-item>
            
            <el-form-item label="诊所地址" prop="clinicAddress">
              <el-input v-model="basicSettings.clinicAddress" type="textarea" :rows="2"></el-input>
            </el-form-item>
            
            <el-form-item label="诊所简介" prop="clinicDescription">
              <el-input v-model="basicSettings.clinicDescription" type="textarea" :rows="4"></el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">保存设置</el-button>
              <el-button @click="resetBasicSettings">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="预约设置" name="appointment">
          <el-form :model="appointmentSettings" label-width="120px" :rules="appointmentRules" ref="appointmentForm">
            <el-form-item label="预约时间间隔" prop="timeSlotDuration">
              <el-select v-model="appointmentSettings.timeSlotDuration" placeholder="请选择时间间隔" style="width: 100%">
                <el-option label="15分钟" :value="15"></el-option>
                <el-option label="20分钟" :value="20"></el-option>
                <el-option label="30分钟" :value="30"></el-option>
                <el-option label="40分钟" :value="40"></el-option>
                <el-option label="60分钟" :value="60"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="提前预约天数" prop="maxDaysInAdvance">
              <el-input-number 
                v-model="appointmentSettings.maxDaysInAdvance" 
                :min="1" 
                :max="60"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
            
            <el-form-item label="最大排队人数" prop="maxQueueSize">
              <el-input-number 
                v-model="appointmentSettings.maxQueueSize" 
                :min="0" 
                :max="100"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
            
            <el-form-item label="允许取消时间" prop="cancelThreshold">
              <el-select v-model="appointmentSettings.cancelThreshold" placeholder="请选择允许取消时间" style="width: 100%">
                <el-option label="随时可取消" :value="0"></el-option>
                <el-option label="提前1小时" :value="1"></el-option>
                <el-option label="提前2小时" :value="2"></el-option>
                <el-option label="提前4小时" :value="4"></el-option>
                <el-option label="提前12小时" :value="12"></el-option>
                <el-option label="提前24小时" :value="24"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveAppointmentSettings">保存设置</el-button>
              <el-button @click="resetAppointmentSettings">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="通知设置" name="notification">
          <el-form :model="notificationSettings" label-width="120px" ref="notificationForm">
            <el-form-item label="短信通知">
              <el-switch v-model="notificationSettings.smsEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="预约提醒" v-if="notificationSettings.smsEnabled">
              <el-checkbox-group v-model="notificationSettings.smsNotificationTypes">
                <el-checkbox label="appointment">预约确认</el-checkbox>
                <el-checkbox label="reminder">预约提醒</el-checkbox>
                <el-checkbox label="cancel">预约取消</el-checkbox>
                <el-checkbox label="reschedule">预约改期</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            
            <el-form-item label="预约提醒时间" v-if="notificationSettings.smsEnabled">
              <el-select v-model="notificationSettings.reminderTime" placeholder="请选择提醒时间" style="width: 100%">
                <el-option label="提前15分钟" :value="15"></el-option>
                <el-option label="提前30分钟" :value="30"></el-option>
                <el-option label="提前1小时" :value="60"></el-option>
                <el-option label="提前2小时" :value="120"></el-option>
                <el-option label="提前1天" :value="1440"></el-option>
              </el-select>
            </el-form-item>
            
            <el-divider></el-divider>
            
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationSettings.emailEnabled"></el-switch>
            </el-form-item>
            
            <el-form-item label="邮件通知类型" v-if="notificationSettings.emailEnabled">
              <el-checkbox-group v-model="notificationSettings.emailNotificationTypes">
                <el-checkbox label="appointment">预约确认</el-checkbox>
                <el-checkbox label="reminder">预约提醒</el-checkbox>
                <el-checkbox label="cancel">预约取消</el-checkbox>
                <el-checkbox label="reschedule">预约改期</el-checkbox>
                <el-checkbox label="medicalReport">医疗报告</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveNotificationSettings">保存设置</el-button>
              <el-button @click="resetNotificationSettings">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'AdminSettings',
  data() {
    return {
      activeTab: 'basic',
      
      // 基本设置
      basicSettings: {
        clinicName: '',
        clinicPhone: '',
        businessHours: [new Date(2020, 0, 1, 9, 0), new Date(2020, 0, 1, 18, 0)],
        clinicAddress: '',
        clinicDescription: ''
      },
      
      // 基本设置验证规则
      basicRules: {
        clinicName: [
          { required: true, message: '请输入诊所名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        clinicPhone: [
          { required: true, message: '请输入诊所电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
        ],
        businessHours: [
          { required: true, message: '请选择营业时间', trigger: 'change' }
        ],
        clinicAddress: [
          { required: true, message: '请输入诊所地址', trigger: 'blur' }
        ]
      },
      
      // 预约设置
      appointmentSettings: {
        timeSlotDuration: 30,
        maxDaysInAdvance: 14,
        maxQueueSize: 10,
        cancelThreshold: 4
      },
      
      // 预约设置验证规则
      appointmentRules: {
        timeSlotDuration: [
          { required: true, message: '请选择预约时间间隔', trigger: 'change' }
        ],
        maxDaysInAdvance: [
          { required: true, message: '请输入提前预约天数', trigger: 'blur' }
        ],
        maxQueueSize: [
          { required: true, message: '请输入最大排队人数', trigger: 'blur' }
        ],
        cancelThreshold: [
          { required: true, message: '请选择允许取消时间', trigger: 'change' }
        ]
      },
      
      // 通知设置
      notificationSettings: {
        smsEnabled: true,
        smsNotificationTypes: ['appointment', 'reminder'],
        reminderTime: 60,
        emailEnabled: true,
        emailNotificationTypes: ['appointment', 'reminder', 'medicalReport']
      }
    }
  },
  created() {
    this.getSystemSettings()
  },
  methods: {
    // 获取系统设置
    getSystemSettings() {
      this.$http.get('/admin/settings')
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data
            
            // 设置基本设置
            this.basicSettings.clinicName = data.clinicName
            this.basicSettings.clinicPhone = data.clinicPhone
            this.basicSettings.clinicAddress = data.clinicAddress
            this.basicSettings.clinicDescription = data.clinicDescription
            
            // 设置营业时间
            if (data.openTime && data.closeTime) {
              const [openHour, openMinute] = data.openTime.split(':').map(Number)
              const [closeHour, closeMinute] = data.closeTime.split(':').map(Number)
              this.basicSettings.businessHours = [
                new Date(2020, 0, 1, openHour, openMinute),
                new Date(2020, 0, 1, closeHour, closeMinute)
              ]
            }
            
            // 设置预约设置
            this.appointmentSettings.timeSlotDuration = data.timeSlotDuration
            this.appointmentSettings.maxDaysInAdvance = data.maxDaysInAdvance
            this.appointmentSettings.maxQueueSize = data.maxQueueSize
            this.appointmentSettings.cancelThreshold = data.cancelThreshold
            
            // 设置通知设置
            this.notificationSettings.smsEnabled = data.smsEnabled
            this.notificationSettings.smsNotificationTypes = data.smsNotificationTypes ? data.smsNotificationTypes.split(',') : []
            this.notificationSettings.reminderTime = data.reminderTime
            this.notificationSettings.emailEnabled = data.emailEnabled
            this.notificationSettings.emailNotificationTypes = data.emailNotificationTypes ? data.emailNotificationTypes.split(',') : []
          }
        })
        .catch(error => {
          console.error('获取系统设置失败', error)
          this.$message.error('获取系统设置失败，请稍后重试')
        })
    },
    
    // 保存基本设置
    saveBasicSettings() {
      this.$refs.basicForm.validate(valid => {
        if (valid) {
          // 格式化营业时间
          const formatTime = time => {
            const hours = time.getHours().toString().padStart(2, '0')
            const minutes = time.getMinutes().toString().padStart(2, '0')
            return `${hours}:${minutes}`
          }
          
          const settings = {
            clinicName: this.basicSettings.clinicName,
            clinicPhone: this.basicSettings.clinicPhone,
            openTime: formatTime(this.basicSettings.businessHours[0]),
            closeTime: formatTime(this.basicSettings.businessHours[1]),
            clinicAddress: this.basicSettings.clinicAddress,
            clinicDescription: this.basicSettings.clinicDescription
          }
          
          this.$http.put('/admin/settings/basic', settings)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('基本设置保存成功')
              } else {
                this.$message.error(response.data.message || '保存基本设置失败')
              }
            })
            .catch(error => {
              console.error('保存基本设置失败', error)
              this.$message.error('保存基本设置失败，请稍后重试')
            })
        }
      })
    },
    
    // 重置基本设置
    resetBasicSettings() {
      this.$refs.basicForm.resetFields()
    },
    
    // 保存预约设置
    saveAppointmentSettings() {
      this.$refs.appointmentForm.validate(valid => {
        if (valid) {
          this.$http.put('/admin/settings/appointment', this.appointmentSettings)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('预约设置保存成功')
              } else {
                this.$message.error(response.data.message || '保存预约设置失败')
              }
            })
            .catch(error => {
              console.error('保存预约设置失败', error)
              this.$message.error('保存预约设置失败，请稍后重试')
            })
        }
      })
    },
    
    // 重置预约设置
    resetAppointmentSettings() {
      this.$refs.appointmentForm.resetFields()
    },
    
    // 保存通知设置
    saveNotificationSettings() {
      const settings = {
        smsEnabled: this.notificationSettings.smsEnabled,
        smsNotificationTypes: this.notificationSettings.smsNotificationTypes.join(','),
        reminderTime: this.notificationSettings.reminderTime,
        emailEnabled: this.notificationSettings.emailEnabled,
        emailNotificationTypes: this.notificationSettings.emailNotificationTypes.join(',')
      }
      
      this.$http.put('/admin/settings/notification', settings)
        .then(response => {
          if (response.data.code === 200) {
            this.$message.success('通知设置保存成功')
          } else {
            this.$message.error(response.data.message || '保存通知设置失败')
          }
        })
        .catch(error => {
          console.error('保存通知设置失败', error)
          this.$message.error('保存通知设置失败，请稍后重试')
        })
    },
    
    // 重置通知设置
    resetNotificationSettings() {
      this.$refs.notificationForm.resetFields()
    }
  }
}
</script>

<style scoped>
.settings-container {
  padding-bottom: 20px;
}

.main-card {
  margin-bottom: 20px;
}
</style>
