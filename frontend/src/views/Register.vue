<template>
  <div class="register-container">
    <el-card class="register-card">
      <div class="register-title">用户注册</div>
      <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="80px" class="register-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" placeholder="请再次输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="registerForm.age" :min="1" :max="120"></el-input-number>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="账号类型" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio :label="1">患者</el-radio>
            <el-radio :label="2">医生</el-radio>
            <el-radio :label="3">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 2" label="职称" prop="title">
          <el-select v-model="registerForm.title" placeholder="请选择职称">
            <el-option label="主治医师" value="主治医师"></el-option>
            <el-option label="副主治医师" value="副主治医师"></el-option>
            <el-option label="主任医师" value="主任医师"></el-option>
            <el-option label="住院医师" value="住院医师"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="registerForm.role === 2" label="专长" prop="specialty">
          <el-select v-model="registerForm.specialty" placeholder="请选择专长领域">
            <el-option label="普通口腔科" value="普通口腔科"></el-option>
            <el-option label="正牙科" value="正牙科"></el-option>
            <el-option label="牙周科" value="牙周科"></el-option>
            <el-option label="口腔修复科" value="口腔修复科"></el-option>
            <el-option label="儿童口腔科" value="儿童口腔科"></el-option>
            <el-option label="口腔颗面外科" value="口腔颗面外科"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
          <el-button @click="goToLogin">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    // 自定义校验规则
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.registerForm.confirmPassword !== '') {
          this.$refs.registerForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        gender: '男',
        age: 30,
        phone: '',
        email: '',
        role: 1, // 默认注册为患者
        title: '', // 医生职称
        specialty: '' // 医生专长
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePass2, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          // 构建注册数据，移除确认密码字段
          const registerData = { ...this.registerForm }
          delete registerData.confirmPassword
          
          // 如果不是医生，删除医生相关字段
          if (registerData.role !== 2) {
            delete registerData.title
            delete registerData.specialty
          }
          
          // 将role字段重命名为roleId，以符合后端接口要求
          registerData.roleId = registerData.role
          delete registerData.role
          
          // 发送注册请求
          this.$http.post('/user/register', registerData)
            .then(response => {
              const { code, message } = response.data
              if (code === 200) {
                this.$message.success('注册成功，请登录')
                this.$router.push('/login')
              } else {
                this.$message.error(message || '注册失败')
              }
            })
            .catch(error => {
              console.error('注册错误:', error)
              this.$message.error('注册失败，请稍后重试')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.register-card {
  width: 500px;
  border-radius: 5px;
}

.register-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #409EFF;
}

.register-form {
  margin-top: 20px;
}
</style>
