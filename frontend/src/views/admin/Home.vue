<template>
  <div class="admin-home">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="welcome-card">
          <div class="welcome-message">
            <h2>您好，{{ currentUser ? currentUser.realName : 'Admin' }} 管理员！</h2>
            <p>欢迎使用智能齿科就诊系统管理员端</p>
          </div>
          <div class="welcome-tips">
            <h3>使用提示：</h3>
            <ul>
              <li>您可以在"用户管理"中查看和管理系统所有用户</li>
              <li>"系统设置"中可以配置系统的基本参数</li>
              <li>"数据维护"可以进行数据的导入、导出和备份</li>
              <li>系统目前已加载Apache Shiro依赖，但尚未启用权限拦截</li>
            </ul>
          </div>
        </el-card>
        
        <el-card class="overview-card">
          <div slot="header">
            <span>系统概览</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshData">
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
                  <div class="data-title">用户总数</div>
                  <div class="data-value">{{ statistics.totalUsers }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-success">
                  <i class="el-icon-first-aid-kit"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">医生数量</div>
                  <div class="data-value">{{ statistics.doctorCount }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-warning">
                  <i class="el-icon-date"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">今日预约</div>
                  <div class="data-value">{{ statistics.todayAppointments }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="20" class="data-overview">
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-danger">
                  <i class="el-icon-document"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">病历总数</div>
                  <div class="data-value">{{ statistics.totalRecords }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-info">
                  <i class="el-icon-money"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">收入总额</div>
                  <div class="data-value">¥{{ statistics.totalIncome }}</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="data-item">
                <div class="data-icon bg-default">
                  <i class="el-icon-s-data"></i>
                </div>
                <div class="data-info">
                  <div class="data-title">本月预约</div>
                  <div class="data-value">{{ statistics.monthlyAppointments }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <el-card class="chart-card">
          <div slot="header">
            <span>预约趋势（近7天）</span>
          </div>
          <div class="chart-container" ref="appointmentChart"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="quick-actions-card">
          <div slot="header">
            <span>快捷操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-user-solid" @click="goToUserManagement">用户管理</el-button>
            <el-button type="success" icon="el-icon-setting" @click="goToSettings">系统设置</el-button>
            <el-button type="warning" icon="el-icon-download" @click="goToDataExport">数据导出</el-button>
            <el-button type="info" icon="el-icon-s-operation" @click="goToDataBackup">数据备份</el-button>
          </div>
        </el-card>
        
        <el-card class="notification-card">
          <div slot="header">
            <span>系统通知</span>
            <el-button style="float: right; padding: 3px 0" type="text">
              全部标记为已读
            </el-button>
          </div>
          <div class="notification-list">
            <div class="notification-item unread">
              <i class="el-icon-warning notification-icon warning"></i>
              <div class="notification-content">
                <div class="notification-title">系统提醒</div>
                <div class="notification-desc">系统将于本周六凌晨2点进行维护升级</div>
                <div class="notification-time">1小时前</div>
              </div>
            </div>
            <div class="notification-item">
              <i class="el-icon-success notification-icon success"></i>
              <div class="notification-content">
                <div class="notification-title">备份完成</div>
                <div class="notification-desc">数据库备份已成功完成，备份文件已保存</div>
                <div class="notification-time">昨天</div>
              </div>
            </div>
            <div class="notification-item">
              <i class="el-icon-info notification-icon info"></i>
              <div class="notification-content">
                <div class="notification-title">新用户注册</div>
                <div class="notification-desc">今日有5名新患者注册了系统账号</div>
                <div class="notification-time">2天前</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="log-card">
          <div slot="header">
            <span>最近系统日志</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToLogs">
              查看全部
            </el-button>
          </div>
          <div class="log-list">
            <div class="log-item">
              <span class="log-time">{{ formatDateTime(new Date()) }}</span>
              <span class="log-type success">INFO</span>
              <span class="log-message">管理员 {{ currentUser.realName }} 登录系统</span>
            </div>
            <div class="log-item">
              <span class="log-time">{{ formatDateTime(new Date(Date.now() - 30 * 60 * 1000)) }}</span>
              <span class="log-type">INFO</span>
              <span class="log-message">系统自动备份任务已完成</span>
            </div>
            <div class="log-item">
              <span class="log-time">{{ formatDateTime(new Date(Date.now() - 2 * 60 * 60 * 1000)) }}</span>
              <span class="log-type warning">WARN</span>
              <span class="log-message">用户尝试多次登录失败</span>
            </div>
            <div class="log-item">
              <span class="log-time">{{ formatDateTime(new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)) }}</span>
              <span class="log-type danger">ERROR</span>
              <span class="log-message">数据库连接异常，已自动恢复</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import * as echarts from 'echarts'

export default {
  name: 'AdminHome',
  data() {
    return {
      statistics: {
        totalUsers: 0,
        doctorCount: 0,
        todayAppointments: 0,
        totalRecords: 0,
        totalIncome: 0,
        monthlyAppointments: 0
      },
      chartInstance: null,
      isChartInitialized: false
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  mounted() {
    this.fetchStatistics()
    this.initChart()
    
    // 添加窗口大小变化时重绘图表
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    // 移除事件监听
    window.removeEventListener('resize', this.resizeChart)
    
    // 销毁图表实例
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    // 获取系统统计数据
    fetchStatistics() {
      this.$http.get('/admin/home/overview')
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data;
            this.statistics = {
              totalUsers: data.totalUsers || 0,
              doctorCount: data.doctorCount || 0,
              todayAppointments: data.todayAppointments || 0,
              totalRecords: data.totalRecords || 0,
              totalIncome: data.totalIncome || 0,
              monthlyAppointments: data.monthlyAppointments || 0
            }
            
            // 更新预约趋势图表数据
            if (data.appointmentTrend && this.chartInstance) {
              const dates = [];
              const counts = [];
              
              data.appointmentTrend.forEach(item => {
                dates.push(item.date);
                counts.push(item.count);
              });
              
              this.chartInstance.setOption({
                xAxis: {
                  data: dates
                },
                series: [{
                  data: counts
                }]
              });
            }
          } else {
            this.$message.error('获取系统概览数据失败');
          }
        })
        .catch(error => {
          console.error('获取系统概览数据出错', error);
          this.$message.error('系统错误，请稍后重试');
        });
    },
    
    // 初始化图表
    initChart() {
      // 等待DOM渲染完成
      this.$nextTick(() => {
        const chartDom = this.$refs.appointmentChart
        if (!chartDom) return
        
        this.chartInstance = echarts.init(chartDom)
        this.isChartInitialized = true
        
        // 生成近7天的日期标签
        const dateLabels = []
        for (let i = 6; i >= 0; i--) {
          const date = new Date()
          date.setDate(date.getDate() - i)
          dateLabels.push(this.formatDate(date))
        }
        
        // 模拟预约数据
        const appointmentData = [18, 25, 20, 30, 22, 28, 25]
        
        // 配置图表选项
        const option = {
          tooltip: {
            trigger: 'axis'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: dateLabels
          },
          yAxis: {
            type: 'value',
            minInterval: 1
          },
          series: [
            {
              name: '预约数',
              type: 'line',
              data: appointmentData,
              smooth: true,
              symbol: 'circle',
              symbolSize: 6,
              itemStyle: {
                color: '#409EFF'
              },
              lineStyle: {
                width: 3
              },
              areaStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
                    { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
                  ]
                }
              }
            }
          ]
        }
        
        this.chartInstance.setOption(option)
      })
    },
    
    // 重新调整图表大小
    resizeChart() {
      if (this.chartInstance) {
        this.chartInstance.resize()
      }
    },
    
    // 刷新数据
    refreshData() {
      this.fetchStatistics()
      
      // 重新初始化图表
      if (this.chartInstance) {
        this.chartInstance.dispose()
      }
      this.initChart()
    },
    
    // 跳转到用户管理
    goToUserManagement() {
      this.$router.push('/admin/users')
    },
    
    // 跳转到系统设置
    goToSettings() {
      this.$router.push('/admin/settings')
    },
    
    // 跳转到数据导出
    goToDataExport() {
      this.$router.push('/admin/data')
    },
    
    // 跳转到数据备份
    goToDataBackup() {
      this.$router.push('/admin/data')
    },
    
    // 跳转到日志页面
    goToLogs() {
      this.$router.push('/admin/settings')
    },
    
    // 格式化日期为 YYYY-MM-DD
    formatDate(date) {
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      
      return `${month}-${day}`
    },
    
    // 格式化日期时间
    formatDateTime(date) {
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.admin-home {
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

.overview-card,
.chart-card,
.quick-actions-card,
.notification-card,
.log-card {
  margin-bottom: 20px;
}

.data-overview {
  padding: 10px 0;
}

.data-overview:first-child {
  margin-bottom: 10px;
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

.bg-danger {
  background-color: #f56c6c;
}

.bg-info {
  background-color: #909399;
}

.bg-default {
  background-color: #35495e;
}

.chart-container {
  height: 300px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-actions .el-button {
  flex: 1 0 calc(50% - 5px);
  margin: 0;
}

.notification-list,
.log-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #f4f4f5;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.notification-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 5px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.unread .notification-title {
  color: #409eff;
}

.warning {
  color: #e6a23c;
}

.success {
  color: #67c23a;
}

.info {
  color: #909399;
}

.log-item {
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  font-size: 13px;
}

.log-item:last-child {
  border-bottom: none;
}

.log-time {
  width: 150px;
  color: #909399;
}

.log-type {
  width: 60px;
  margin-right: 10px;
  color: #409eff;
}

.log-type.success {
  color: #67c23a;
}

.log-type.warning {
  color: #e6a23c;
}

.log-type.danger {
  color: #f56c6c;
}

.log-message {
  flex: 1;
}
</style>
