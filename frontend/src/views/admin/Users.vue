<template>
  <div class="admin-users">
    <el-card class="user-card">
      <div slot="header">
        <span>用户管理</span>
        <div class="header-operations">
          <el-button type="primary" size="small" @click="handleCreateUser">
            <i class="el-icon-plus"></i> 新增用户
          </el-button>
          <el-button size="small" @click="refreshUserList">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>
      
      <!-- 搜索区域 -->
      <el-row :gutter="20" class="search-bar">
        <el-col :span="5">
          <el-select v-model="searchForm.role" placeholder="角色" clearable style="width: 100%">
            <el-option label="患者" value="PATIENT"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="searchForm.username"
            placeholder="用户名/账号"
            clearable>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="searchForm.realName"
            placeholder="姓名"
            clearable>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="searchForm.phone"
            placeholder="手机号"
            clearable>
          </el-input>
        </el-col>
        <el-col :span="1">
          <el-button type="primary" icon="el-icon-search" @click="searchUsers"></el-button>
        </el-col>
      </el-row>
      
      <!-- 用户列表 -->
      <el-table
        :data="userList"
        v-loading="loading"
        border
        style="width: 100%"
        class="users-table">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120"></el-table-column>
        <el-table-column prop="realName" label="姓名" min-width="100"></el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">{{ translateRole(scope.row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="160">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(scope.row, val)"
              active-color="#13ce66"
              inactive-color="#ff4949">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEditUser(scope.row)">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDeleteUser(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 用户编辑对话框 -->
    <el-dialog :title="userForm.id ? '编辑用户' : '新增用户'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="userForm.id"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="患者" value="PATIENT"></el-option>
            <el-option label="医生" value="DOCTOR"></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!userForm.id">
          <el-input v-model="userForm.confirmPassword" type="password"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="userForm.remark" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitLoading">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AdminUsers',
  data() {
    // 验证密码与确认密码是否一致
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.userForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      // 搜索表单
      searchForm: {
        role: '',
        username: '',
        realName: '',
        phone: ''
      },
      
      // 用户列表
      userList: [],
      loading: false,
      
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      
      // 用户表单
      dialogVisible: false,
      submitLoading: false,
      userForm: {
        id: null,
        username: '',
        realName: '',
        role: 'PATIENT',
        password: '',
        confirmPassword: '',
        phone: '',
        email: '',
        gender: 1,
        status: 1,
        remark: ''
      },
      
      // 表单验证规则
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 4, max: 20, message: '长度在 4 到 20 个字符', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.fetchUserList()
  },
  methods: {
    // 获取用户列表
    fetchUserList() {
      this.loading = true
      
      const params = {
        page: this.pagination.currentPage,
        size: this.pagination.pageSize,
        ...this.searchForm
      }
      
      this.$http.get('/user/list', { params })
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data
            this.userList = data
            this.pagination.total = data.length
          } else {
            this.$message.error(response.data.message || '获取用户列表失败')
          }
        })
        .catch(error => {
          console.error('获取用户列表错误:', error)
          this.$message.error('获取用户列表失败，请稍后重试')
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 刷新用户列表
    refreshUserList() {
      this.fetchUserList()
    },
    
    // 搜索用户
    searchUsers() {
      this.pagination.currentPage = 1
      this.fetchUserList()
    },
    
    // 新增用户
    handleCreateUser() {
      this.userForm = {
        id: null,
        username: '',
        realName: '',
        role: 'PATIENT',
        password: '',
        confirmPassword: '',
        phone: '',
        email: '',
        gender: 1,
        status: 1,
        remark: ''
      }
      
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.userForm && this.$refs.userForm.clearValidate()
      })
    },
    
    // 编辑用户
    handleEditUser(row) {
      this.$http.get(`/user/${row.id}`)
        .then(response => {
          if (response.data.code === 200) {
            this.userForm = { ...response.data.data }
            // 密码不显示
            this.userForm.password = ''
            this.userForm.confirmPassword = ''
            
            this.dialogVisible = true
            this.$nextTick(() => {
              this.$refs.userForm && this.$refs.userForm.clearValidate()
            })
          } else {
            this.$message.error(response.data.message || '获取用户详情失败')
          }
        })
        .catch(error => {
          console.error('获取用户详情错误:', error)
          this.$message.error('获取用户详情失败，请稍后重试')
        })
    },
    
    // 删除用户
    handleDeleteUser(row) {
      this.$confirm(`确认删除用户 ${row.realName} (${row.username}) ?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/user/${row.id}`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('删除用户成功')
              this.fetchUserList()
            } else {
              this.$message.error(response.data.message || '删除用户失败')
            }
          })
          .catch(error => {
            console.error('删除用户错误:', error)
            this.$message.error('删除用户失败，请稍后重试')
          })
      }).catch(() => {
        // 取消删除
      })
    },
    
    // 修改用户状态
    handleStatusChange(row, status) {
      this.$http.put(`/user/${row.id}/status/${status}`)
        .then(response => {
          if (response.data.code === 200) {
            this.$message.success(`用户状态已${status === 1 ? '启用' : '禁用'}`)
          } else {
            // 恢复原状态
            row.status = status === 1 ? 0 : 1
            this.$message.error(response.data.message || '修改用户状态失败')
          }
        })
        .catch(error => {
          // 恢复原状态
          row.status = status === 1 ? 0 : 1
          console.error('修改用户状态错误:', error)
          this.$message.error('修改用户状态失败，请稍后重试')
        })
    },
    
    // 提交用户表单
    submitUserForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          
          // 创建提交的表单数据
          const formData = { ...this.userForm }
          // 创建用户时提交密码，编辑用户时不提交密码
          if (formData.id) {
            delete formData.password
            delete formData.confirmPassword
          } else {
            delete formData.confirmPassword
          }
          
          // 判断是创建还是更新
          const method = formData.id ? 'put' : 'post'
          const url = formData.id ? `/user/${formData.id}` : '/user'
          
          this.$http[method](url, formData)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success(formData.id ? '更新用户成功' : '创建用户成功')
                this.dialogVisible = false
                this.fetchUserList()
              } else {
                this.$message.error(response.data.message || (formData.id ? '更新用户失败' : '创建用户失败'))
              }
            })
            .catch(error => {
              console.error('提交用户表单错误:', error)
              this.$message.error(formData.id ? '更新用户失败，请稍后重试' : '创建用户失败，请稍后重试')
            })
            .finally(() => {
              this.submitLoading = false
            })
        }
      })
    },
    
    // 处理分页大小变化
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.fetchUserList()
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.fetchUserList()
    },
    
    // 角色标签类型
    getRoleTagType(role) {
      switch (role) {
        case 'ADMIN':
          return 'danger'
        case 'DOCTOR':
          return 'success'
        case 'PATIENT':
          return 'primary'
        default:
          return 'info'
      }
    },
    
    // 翻译角色
    translateRole(role) {
      switch (role) {
        case 'ADMIN':
          return '管理员'
        case 'DOCTOR':
          return '医生'
        case 'PATIENT':
          return '患者'
        default:
          return '未知'
      }
    },
    
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      const seconds = date.getSeconds().toString().padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
  }
}
</script>

<style scoped>
.admin-users {
  height: 100%;
}

.user-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-operations {
  float: right;
}

.search-bar {
  margin-bottom: 20px;
}

.users-table {
  margin-bottom: 20px;
  flex: 1;
}

.pagination-container {
  text-align: right;
  padding: 10px 0;
}

.el-input,
.el-select {
  width: 90%;
}
</style>
