<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="logo">智能齿科就诊系统 - 患者端</div>
      <div class="user-info">
        <span>欢迎您，{{ currentUser.realName }}</span>
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <i class="el-icon-user"></i> 个人中心<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    
    <el-container>
      <el-aside width="200px" class="layout-aside">
        <el-menu
          :default-active="activeIndex"
          class="layout-menu"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b">
          <el-menu-item index="/patient/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="/patient/appointment">
            <i class="el-icon-date"></i>
            <span slot="title">在线预约</span>
          </el-menu-item>
          <el-menu-item index="/patient/records">
            <i class="el-icon-document"></i>
            <span slot="title">病历查询</span>
          </el-menu-item>
          <el-menu-item index="/patient/payment">
            <i class="el-icon-wallet"></i>
            <span slot="title">缴费管理</span>
          </el-menu-item>
          <el-menu-item index="/patient/profile">
            <i class="el-icon-user"></i>
            <span slot="title">个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PatientLayout',
  computed: {
    ...mapGetters(['currentUser']),
    activeIndex() {
      return this.$route.path
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/patient/profile')
      } else if (command === 'logout') {
        this.logout()
      }
    },
    logout() {
      this.$confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('logout')
        this.$message.success('退出登录成功')
        this.$router.push('/login')
      }).catch(() => {
        // 取消退出
      })
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.layout-aside {
  background-color: #545c64;
  color: white;
}

.layout-menu {
  border-right: none;
  height: 100%;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-info span {
  margin-right: 15px;
}

.el-dropdown-link {
  color: white;
  cursor: pointer;
}
</style>
