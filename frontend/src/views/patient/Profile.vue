<template>
  <div class="patient-profile">
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
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="profileForm.phone" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider></el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日">
              <el-date-picker
                v-model="profileForm.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                :disabled="!isEditing">
              </el-date-picker>
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="profileForm.idCard" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人">
              <el-input v-model="profileForm.emergencyContact" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="紧急联系电话">
              <el-input v-model="profileForm.emergencyPhone" :disabled="!isEditing"></el-input>
            </el-form-item>
            <el-form-item label="住址">
              <el-input v-model="profileForm.address" :disabled="!isEditing"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider>医疗信息</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="病史">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请填写您的病史信息"
                v-model="profileForm.medicalHistory"
                :disabled="!isEditing">
              </el-input>
            </el-form-item>
            <el-form-item label="过敏史">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请填写您的过敏史信息"
                v-model="profileForm.allergies"
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
            <el-button type="primary" :disabled="emailCooldown > 0" @click="sendEmailCode">
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
  name: 'PatientProfile',
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
        birthDate: '',
        idCard: '',
        address: '',
        emergencyContact: '',
        emergencyPhone: '',
        medicalHistory: '',
        allergies: ''
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
      // 先从后端重新获取最新的用户信息
      this.$http.get(`/user/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            const userData = response.data.data;
            
            // 使用最新的用户数据更新Vuex存储
            this.$store.commit('updateUser', userData);
            
            // 使用最新的数据填充表单
            this.profileForm.username = userData.username;
            this.profileForm.realName = userData.realName;
            this.profileForm.gender = userData.gender;
            this.profileForm.phone = userData.phone;
            this.profileForm.email = userData.email;
            this.profileForm.avatar = userData.avatar;
            
            // 获取患者详细信息
            return this.$http.get(`/patient/detail/${userData.id}`);
          } else {
            this.$message.error('获取用户信息失败');
            return Promise.reject(new Error('获取用户信息失败'));
          }
        })
        .then(response => {
          if (response && response.data.code === 200) {
            const data = response.data.data;
            if (data) {
              // 填充患者特有的信息
              this.profileForm.birthDate = data.birthDate ? new Date(data.birthDate) : '';
              this.profileForm.idCard = data.idCard;
              this.profileForm.address = data.address;
              this.profileForm.emergencyContact = data.emergencyContact;
              this.profileForm.emergencyPhone = data.emergencyPhone;
              this.profileForm.medicalHistory = data.medicalHistory;
              this.profileForm.allergies = data.allergies;
            }
          }
        })
        .catch(error => {
          console.error('获取个人资料失败', error);
          this.$message.error('获取个人资料失败，请稍后重试');
        })
    },
    
    // 编辑个人资料
    editProfile() {
      this.isEditing = true
    },
    
    // 保存个人资料
    saveProfile() {
      // 将表单中的个人信息和患者专有信息分开
      const basicInfo = {
        id: this.currentUser.id,
        realName: this.profileForm.realName,
        gender: this.profileForm.gender,
        phone: this.profileForm.phone,
        email: this.profileForm.email
      }
      
      const patientInfo = {
        userId: this.currentUser.id,
        birthDate: this.profileForm.birthDate ? this.formatDate(this.profileForm.birthDate) : null,
        idCard: this.profileForm.idCard,
        address: this.profileForm.address,
        emergencyContact: this.profileForm.emergencyContact,
        emergencyPhone: this.profileForm.emergencyPhone,
        medicalHistory: this.profileForm.medicalHistory,
        allergies: this.profileForm.allergies
      }
      
      // 更新基本信息
      this.$http.put('/user', basicInfo)
        .then(response => {
          if (response.data.code === 200) {
            // 成功更新用户基本信息，继续更新患者信息
            return this.$http.put('/patient/profile', patientInfo)
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
          } else {
            this.$message.error(response.data.message || '更新患者资料失败')
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
          // 调用后端API进行密码修改
          this.$http.put('/patient/change-password', null, {
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
              
              // 退出登录并跳转到登录页
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
      
      // 模拟生成验证码
      const mockCode = Math.floor(100000 + Math.random() * 900000).toString() // 生成六位数字验证码
      
      // 自动填入验证码
      this.phoneForm.verifyCode = mockCode
      
      // 显示成功提示
      this.$message.success(`模拟验证码已生成：${mockCode}`)
      
      // 启动倒计时
      this.cooldown = 60
      const timer = setInterval(() => {
        this.cooldown--
        if (this.cooldown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    
    // 修改手机号
    changePhone() {
      this.$refs.phoneForm.validate(valid => {
        if (valid) {
          // 创建用户对象，只包含id和新手机号
          const userData = {
            id: this.currentUser.id,
            phone: this.phoneForm.newPhone
          }
          
          // 调用后端API更新手机号
          this.$http.put('/user', userData)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('手机号修改成功')
                this.showChangePhone = false
                
                // 更新表单和存储的用户信息
                this.profileForm.phone = this.phoneForm.newPhone
                this.$store.commit('updateUser', { phone: this.phoneForm.newPhone })
                
                // 清空表单
                this.$refs.phoneForm.resetFields()
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
    sendEmailCode() {
      if (!this.emailForm.newEmail) {
        this.$message.warning('请先输入新邮箱')
        return
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(this.emailForm.newEmail)) {
        this.$message.warning('请输入正确的邮箱地址')
        return
      }
      
      // 模拟生成验证码
      const mockCode = Math.floor(100000 + Math.random() * 900000).toString() // 生成六位数字验证码
      
      // 自动填入验证码
      this.emailForm.verifyCode = mockCode
      
      // 显示成功提示
      this.$message.success(`模拟验证码已生成：${mockCode}`)
      
      // 启动倒计时
      this.emailCooldown = 60
      const timer = setInterval(() => {
        this.emailCooldown--
        if (this.emailCooldown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    
    // 修改邮箱
    changeEmail() {
      this.$refs.emailForm.validate(valid => {
        if (valid) {
          // 模拟邮箱修改成功
          this.$message.success('邮箱修改成功')
          this.showChangeEmail = false
          
          // 更新表单和存储的用户信息
          this.profileForm.email = this.emailForm.newEmail
          this.$store.commit('updateUser', { email: this.emailForm.newEmail })
          
          // 清空表单
          this.$refs.emailForm.resetFields()
        }
      })
    },
    
    // 格式化日期
    formatDate(date) {
      if (!date) return null
      
      const d = new Date(date)
      const year = d.getFullYear()
      const month = (d.getMonth() + 1).toString().padStart(2, '0')
      const day = d.getDate().toString().padStart(2, '0')
      
      return `${year}-${month}-${day}`
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
.patient-profile {
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
