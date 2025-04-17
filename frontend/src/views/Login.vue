<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-title">智能齿科就诊系统</div>
      <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="0px" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="loginForm.role">
            <el-radio :label="1">患者</el-radio>
            <el-radio :label="2">医生</el-radio>
            <el-radio :label="3">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-button">登录</el-button>
        </el-form-item>
        <div class="login-options">
          <span>没有账号？</span>
          <router-link to="/register">注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        role: 1
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.post('/user/login', this.loginForm)
            .then(response => {
              const { code, message, data } = response.data
              if (code === 200) {
                // 登录成功
                this.$message.success(message)
                
                // 保存用户信息和token
                this.$store.dispatch('login', {
                  user: data,
                  token: 'Bearer ' + data.id // 简单模拟token，实际应从后端获取
                })
                
                // 根据角色跳转到对应的首页
                // 根据角色跳转到对应的首页
                const redirectPath = this.getRedirectPath(data.role)
                // 避免重复导航，确保一定会跳转
                if (this.$route.path !== redirectPath) {
                  this.$router.push(redirectPath).catch(err => {
                    if (err.name !== 'NavigationDuplicated') {
                      throw err
                    }
                  })
                } else {
                  // 如果已经在目标路径，强制刷新页面
                  window.location.reload()
                }
              } else {
                this.$message.error(message || '登录失败')
              }
            })
            .catch(error => {
              console.error('登录错误:', error)
              this.$message.error('登录失败，请稍后重试')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    getRedirectPath(role) {
      // 角色值可能是数字或字符串，需要进行转换和适配两种情况
      const roleValue = parseInt(role) || role;
      
      switch (roleValue) {
        // 数字类型的角色值
        case 1:
        case '1':
        case 'PATIENT':
          return '/patient/home'
        case 2:
        case '2':
        case 'DOCTOR':
          return '/doctor/home'
        case 3:
        case '3':
        case 'ADMIN':
          return '/admin/home'
        default:
          return '/login'
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
  padding: 20px 35px;
  border-radius: 5px;
}

.login-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #409EFF;
}

.login-form {
  margin-top: 30px;
}

.login-button {
  width: 100%;
}

.login-options {
  text-align: center;
  margin-top: 15px;
  font-size: 14px;
}

.login-options a {
  color: #409EFF;
  margin-left: 5px;
}
</style>
