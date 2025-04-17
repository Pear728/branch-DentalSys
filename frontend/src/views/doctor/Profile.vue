<template>
  <div class="doctor-profile">
    <el-card class="profile-card">
      <div slot="header">
        <span>个人资料</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="editProfile">
          <i class="el-icon-edit"></i> 编辑资料
        </el-button>
      </div>
      
      <el-form :model="profileForm" label-width="100px" class="profile-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="avatar-container">
              <el-avatar :size="120" :src="profileForm.avatar || defaultAvatar"></el-avatar>
              <div class="upload-avatar" v-if="isEditing">
                <el-button type="primary" size="small" icon="el-icon-upload">更换头像</el-button>
              </div>
            </div>
          </el-col>
          <el-col :span="16">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="profileForm.realName" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="profileForm.gender" :disabled="!isEditing">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
              <!-- 显示当前性别值，方便调试 -->
              <span style="color: #999; font-size: 12px; margin-left: 10px;">
                (当前值: {{profileForm.gender === 0 ? '女(0)' : profileForm.gender === 1 ? '男(1)' : '未知(' + profileForm.gender + ')'}})
              </span>
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="profileForm.phone" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider></el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科室">
              <el-input v-model="profileForm.department" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="职称">
              <el-select v-model="profileForm.professionalTitle" placeholder="请选择职称" style="width: 100%" :disabled="!isEditing">
                <el-option label="主任医师" value="主任医师"></el-option>
                <el-option label="副主任医师" value="副主任医师"></el-option>
                <el-option label="主治医师" value="主治医师"></el-option>
                <el-option label="住院医师" value="住院医师"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="执业证号">
              <el-input v-model="profileForm.certificateNo" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="从业年限">
              <el-input-number v-model="profileForm.yearsOfExperience" :min="0" :max="50" :disabled="!isEditing" style="width: 100%"></el-input-number>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="专长">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请填写您的专业特长"
                v-model="profileForm.specialty"
                :disabled="!isEditing">
              </el-input>
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input
                type="textarea"
                :rows="4"
                placeholder="请填写您的个人简介"
                v-model="profileForm.introduction"
                :disabled="!isEditing">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item v-if="isEditing">
          <el-button type="primary" @click="saveProfile">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="security-card">
      <div slot="header">
        <span>账号安全</span>
      </div>
      
      <div class="security-item">
        <div class="security-info">
          <i class="el-icon-lock"></i>
          <div class="security-text">
            <h4>修改密码</h4>
            <p>定期修改密码可以保护您的账号安全</p>
          </div>
        </div>
        <el-button size="small" @click="showChangePassword = true">修改</el-button>
      </div>
      
      <div class="security-item">
        <div class="security-info">
          <i class="el-icon-mobile-phone"></i>
          <div class="security-text">
            <h4>绑定手机</h4>
            <p>已绑定手机：{{ maskedPhone(profileForm.phone) }}</p>
          </div>
        </div>
        <el-button size="small" @click="showChangePhone = true">修改</el-button>
      </div>
      
      <div class="security-item">
        <div class="security-info">
          <i class="el-icon-message"></i>
          <div class="security-text">
            <h4>绑定邮箱</h4>
            <p>已绑定邮箱：{{ maskedEmail(profileForm.email) }}</p>
          </div>
        </div>
        <el-button size="small" @click="showChangeEmail = true">修改</el-button>
      </div>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" :visible.sync="showChangePassword" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </div>
    </el-dialog>
    
    <!-- 修改手机对话框 -->
    <el-dialog title="修改手机号" :visible.sync="showChangePhone" width="400px">
      <el-form :model="phoneForm" :rules="phoneRules" ref="phoneForm" label-width="100px">
        <el-form-item label="当前手机号">
          <el-input v-model="profileForm.phone" disabled></el-input>
        </el-form-item>
        <el-form-item label="新手机号" prop="newPhone">
          <el-input v-model="phoneForm.newPhone"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verifyCode">
          <div class="verify-code-container">
            <el-input v-model="phoneForm.verifyCode" class="verify-code-input"></el-input>
            <el-button type="primary" :disabled="cooldown > 0" @click="sendVerifyCode">
              {{ cooldown > 0 ? `${cooldown}秒后重新获取` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showChangePhone = false">取消</el-button>
        <el-button type="primary" @click="changePhone">确认修改</el-button>
      </div>
    </el-dialog>
    
    <!-- 修改邮箱对话框 -->
    <el-dialog title="修改邮箱" :visible.sync="showChangeEmail" width="400px">
      <el-form :model="emailForm" :rules="emailRules" ref="emailForm" label-width="100px">
        <el-form-item label="当前邮箱">
          <el-input v-model="profileForm.email" disabled></el-input>
        </el-form-item>
        <el-form-item label="新邮箱" prop="newEmail">
          <el-input v-model="emailForm.newEmail"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verifyCode">
          <div class="verify-code-container">
            <el-input v-model="emailForm.verifyCode" class="verify-code-input"></el-input>
            <el-button type="primary" :disabled="emailCooldown > 0" @click="sendEmailVerifyCode">
              {{ emailCooldown > 0 ? `${emailCooldown}秒后重新获取` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showChangeEmail = false">取消</el-button>
        <el-button type="primary" @click="changeEmail">确认修改</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DoctorProfile',
  data() {
    // 验证新密码
    const validateNewPassword = (rule, value, callback) => {
      if (value === this.passwordForm.oldPassword) {
        callback(new Error('新密码不能与当前密码相同'))
      } else {
        callback()
      }
    }
    
    // 验证确认密码
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      isEditing: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      
      // 个人资料表单
      profileForm: {
        username: '',
        realName: '',
        gender: 1,
        phone: '',
        email: '',
        avatar: '',
        department: '',
        professionalTitle: '',
        specialty: '',
        introduction: '',
        certificateNo: '',
        yearsOfExperience: 0
      },
      
      // 账号安全相关
      showChangePassword: false,
      showChangePhone: false,
      showChangeEmail: false,
      
      // 修改密码表单
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      
      // 密码表单验证规则
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度最少为6个字符', trigger: 'blur' },
          { validator: validateNewPassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      
      // 修改手机表单
      phoneForm: {
        newPhone: '',
        verifyCode: ''
      },
      
      // 手机表单验证规则
      phoneRules: {
        newPhone: [
          { required: true, message: '请输入新手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
        ]
      },
      
      // 修改邮箱表单
      emailForm: {
        newEmail: '',
        verifyCode: ''
      },
      
      // 邮箱表单验证规则
      emailRules: {
        newEmail: [
          { required: true, message: '请输入新邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码长度应为6位', trigger: 'blur' }
        ]
      },
      
      // 验证码倒计时
      cooldown: 0,
      emailCooldown: 0
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  created() {
    this.getProfileData()
  },
  methods: {
    // 获取个人资料数据
    getProfileData() {
      console.log('正在获取个人资料...')
      
      // 设置空白表单，防止旧数据污染
      this.profileForm = {
        username: '',
        realName: '',
        gender: 0,
        phone: '',
        email: '',
        avatar: '',
        department: '',
        professionalTitle: '',
        specialty: '',
        introduction: '',
        certificateNo: '',
        yearsOfExperience: 0
      }
      
      // 直接获取医生详细信息，不依赖Vuex存储的数据
      this.$http.get(`/doctor/profile/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            console.log('获取到个人资料:', response.data.data)
            const data = response.data.data
            
            // 填充基本信息
            this.profileForm.username = data.username
            this.profileForm.realName = data.realName
            
            // 确保性别值为数字
            let genderValue = data.gender
            if (genderValue !== 0 && genderValue !== 1) {
              genderValue = parseInt(genderValue) || 0
            }
            this.profileForm.gender = genderValue
            console.log('从后端获取的性别值：', genderValue, typeof genderValue)
            
            this.profileForm.phone = data.phone || ''
            this.profileForm.email = data.email || ''
            
            // 填充医生特有的信息
            this.profileForm.professionalTitle = data.title || ''
            this.profileForm.specialty = data.specialty || ''
            this.profileForm.introduction = data.introduction || ''
            
            // 其他可能的字段
            if (data.department) this.profileForm.department = data.department
            if (data.certificateNo) this.profileForm.certificateNo = data.certificateNo
            if (data.yearsOfExperience) this.profileForm.yearsOfExperience = data.yearsOfExperience
          }
        })
        .catch(error => {
          console.error('获取个人资料失败', error)
          this.$message.error('获取个人资料失败，请稍后重试')
        })
    },
    
    // 编辑个人资料
    editProfile() {
      this.isEditing = true
    },
    
    // 保存个人资料
    saveProfile() {
      // 将表单中的个人信息和医生专有信息分开
      const basicInfo = {
        id: this.currentUser.id,
        realName: this.profileForm.realName,
        // 确保性别值为数字类型
        gender: parseInt(this.profileForm.gender), 
        phone: this.profileForm.phone,
        email: this.profileForm.email
      }
      
      console.log('提交的性别值:', basicInfo.gender, typeof basicInfo.gender)
      
      // 对齐后端字段名：professionalTitle -> title
      // 在医生信息中同时添加id和userId，确保后端能够找到正确的用户
      const doctorInfo = {
        id: this.currentUser.id, // 添加id字段
        userId: this.currentUser.id,
        department: this.profileForm.department,
        title: this.profileForm.professionalTitle, // 前端是professionalTitle，后端是title
        specialty: this.profileForm.specialty,
        introduction: this.profileForm.introduction,
        certificateNo: this.profileForm.certificateNo,
        yearsOfExperience: this.profileForm.yearsOfExperience
      }
      
      console.log('更新用户资料，用户ID:', this.currentUser.id)
      
      // 更新基本信息
      this.$http.put('/user', basicInfo)
        .then(response => {
          if (response.data.code === 200) {
            // 成功更新用户基本信息，继续更新医生信息
            return this.$http.put('/doctor/profile', doctorInfo)
          } else {
            this.$message.error(response.data.message || '更新个人资料失败')
            return Promise.reject(new Error('更新个人资料失败'))
          }
        })
        .then(response => {
          if (response.data.code === 200) {
            this.$message.success('更新个人资料成功')
            this.isEditing = false
            
            // 更新Vuex中的用户信息
            this.$store.commit('updateUser', basicInfo)
            
            // 重新获取最新的个人资料数据
            console.log('重新获取用户资料数据...')
            setTimeout(() => {
              this.getProfileData()
            }, 500) // 延迟500毫秒再获取，确保后端数据已更新
          } else {
            this.$message.error(response.data.message || '更新医生资料失败')
          }
        })
        .catch(error => {
          console.error('更新个人资料失败', error)
          this.$message.error('更新个人资料失败，请稍后重试')
        })
    },
    
    // 取消编辑
    cancelEdit() {
      this.isEditing = false
      this.getProfileData() // 重新获取数据，恢复原始值
    },
    
    // 修改密码
    changePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          // 使用正确的医生密码修改接口
          this.$http.put('/doctor/change-password', null, {
            params: {
              userId: this.currentUser.id,
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            }
          })
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('密码修改成功，请重新登录')
                this.showChangePassword = false
                
                // 清空表单
                this.$refs.passwordForm.resetFields()
                
                // 退出登录
                setTimeout(() => {
                  this.$store.dispatch('logout')
                  this.$router.push('/login')
                }, 1500)
              } else {
                this.$message.error(response.data.message || '密码修改失败')
              }
            })
            .catch(error => {
              console.error('密码修改失败', error)
              this.$message.error('密码修改失败，请稍后重试')
            })
        }
      })
    },
    // 发送手机验证码
    sendVerifyCode() {
      if (!this.phoneForm.newPhone) {
        this.$message.warning('请先输入新手机号')
        return
      }
      
      if (!/^1[3-9]\d{9}$/.test(this.phoneForm.newPhone)) {
        this.$message.warning('请输入正确的手机号码')
        return
      }
      
      // 调用后端验证码发送接口
      this.$http.post('/verification/send-sms', {
        phone: this.phoneForm.newPhone,
        type: 'changePhone'
      })
        .then(response => {
          if (response.data.code === 200) {
            // 开发模式下，后端会返回验证码，直接使用
            const verifyCode = response.data.data
            this.phoneForm.verifyCode = verifyCode
            this.$message.success(`验证码已发送并自动填入: ${verifyCode}`)
            
            // 启动倒计时
            this.cooldown = 60
            const timer = setInterval(() => {
              this.cooldown--
              if (this.cooldown <= 0) {
                clearInterval(timer)
              }
            }, 1000)
          } else {
            this.$message.error(response.data.message || '验证码发送失败')
          }
        })
        .catch(error => {
          console.error('验证码发送失败', error)
          this.$message.error('验证码发送失败，请稍后重试')
        })
    },
    
    // 修改手机号
    changePhone() {
      this.$refs.phoneForm.validate(valid => {
        if (valid) {
          // 检查是否有验证码
          if (!this.phoneForm.verifyCode) {
            this.$message.warning('请先获取验证码')
            return
          }
          
          // 使用新的验证码验证接口
          this.$http.put('/verification/verify-phone', {
            userId: this.currentUser.id,
            phone: this.phoneForm.newPhone,
            code: this.phoneForm.verifyCode
          })
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('手机号修改成功')
                this.showChangePhone = false
                
                // 更新表单和存储的用户信息
                this.profileForm.phone = this.phoneForm.newPhone
                this.$store.commit('updateUser', { phone: this.phoneForm.newPhone })
                
                // 清空表单
                this.$refs.phoneForm.resetFields()
                
                // 重新获取个人资料
                this.getProfileData()
              } else {
                this.$message.error(response.data.message || '手机号修改失败')
              }
            })
            .catch(error => {
              console.error('手机号修改失败', error)
              this.$message.error('手机号修改失败，请稍后重试')
            })
        }
      })
    },
    
    // 发送邮箱验证码
    sendEmailVerifyCode() {
      if (!this.emailForm.newEmail) {
        this.$message.warning('请先输入新邮箱')
        return
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(this.emailForm.newEmail)) {
        this.$message.warning('请输入正确的邮箱地址')
        return
      }
      
      // 调用后端验证码发送接口
      this.$http.post('/verification/send-email', {
        email: this.emailForm.newEmail,
        type: 'changeEmail'
      })
        .then(response => {
          if (response.data.code === 200) {
            // 开发模式下，后端会返回验证码，直接使用
            const verifyCode = response.data.data
            this.emailForm.verifyCode = verifyCode
            this.$message.success(`验证码已发送并自动填入: ${verifyCode}`)
            
            // 启动倒计时
            this.emailCooldown = 60
            const timer = setInterval(() => {
              this.emailCooldown--
              if (this.emailCooldown <= 0) {
                clearInterval(timer)
              }
            }, 1000)
          } else {
            this.$message.error(response.data.message || '验证码发送失败')
          }
        })
        .catch(error => {
          console.error('验证码发送失败', error)
          this.$message.error('验证码发送失败，请稍后重试')
        })
    },
    
    // 修改邮箱
    changeEmail() {
      this.$refs.emailForm.validate(valid => {
        if (valid) {
          // 检查是否有验证码
          if (!this.emailForm.verifyCode) {
            this.$message.warning('请先获取验证码')
            return
          }
          
          // 使用新的验证码验证接口
          this.$http.put('/verification/verify-email', {
            userId: this.currentUser.id,
            email: this.emailForm.newEmail,
            code: this.emailForm.verifyCode
          })
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('邮箱修改成功')
                this.showChangeEmail = false
                
                // 更新表单和存储的用户信息
                this.profileForm.email = this.emailForm.newEmail
                this.$store.commit('updateUser', { email: this.emailForm.newEmail })
                
                // 清空表单
                this.$refs.emailForm.resetFields()
              } else {
                this.$message.error(response.data.message || '邮箱修改失败')
              }
            })
            .catch(error => {
              console.error('邮箱修改失败', error)
              this.$message.error('邮箱修改失败，请稍后重试')
            })
        }
      })
    },
    
    // 手机号脱敏显示
    maskedPhone(phone) {
      if (!phone) return '未绑定'
      return phone.substr(0, 3) + '****' + phone.substr(7)
    },
    
    // 邮箱脱敏显示
    maskedEmail(email) {
      if (!email) return '未绑定'
      
      const parts = email.split('@')
      if (parts.length !== 2) return email
      
      let username = parts[0]
      const domain = parts[1]
      
      if (username.length <= 3) {
        username = username.charAt(0) + '***'
      } else {
        username = username.substr(0, 3) + '***'
      }
      
      return username + '@' + domain
    }
  }
}
</script>

<style scoped>
.doctor-profile {
  padding-bottom: 20px;
}

.profile-card,
.security-card {
  margin-bottom: 20px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.upload-avatar {
  margin-top: 15px;
}

.verify-code-container {
  display: flex;
  align-items: center;
}

.verify-code-input {
  margin-right: 10px;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info {
  display: flex;
  align-items: center;
}

.security-info i {
  font-size: 24px;
  color: #409EFF;
  margin-right: 15px;
}

.security-text h4 {
  margin: 0;
  margin-bottom: 5px;
}

.security-text p {
  margin: 0;
  font-size: 13px;
  color: #909399;
}
</style>
