<template>
  <div class="data-container">
    <el-card class="main-card">
      <div slot="header">
        <span>数据统计</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          align="right"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
          @change="handleDateChange"
          style="float: right; margin-top: -5px; width: 350px;"
        ></el-date-picker>
      </div>
      
      <el-row :gutter="20" class="summary-row">
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-title">预约总数</div>
            <div class="summary-value">{{ statisticsData.totalAppointments }}</div>
            <div class="summary-footer">
              <span :class="[statisticsData.appointmentRateClass]">
                <i :class="statisticsData.appointmentRateIcon"></i>
                {{ statisticsData.appointmentRateText }}
              </span>
              <span>较上期</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-title">完成预约</div>
            <div class="summary-value">{{ statisticsData.completedAppointments }}</div>
            <div class="summary-footer">
              <span :class="[statisticsData.completionRateClass]">
                <i :class="statisticsData.completionRateIcon"></i>
                {{ statisticsData.completionRateText }}
              </span>
              <span>较上期</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-title">新增患者</div>
            <div class="summary-value">{{ statisticsData.newPatients }}</div>
            <div class="summary-footer">
              <span :class="[statisticsData.newPatientRateClass]">
                <i :class="statisticsData.newPatientRateIcon"></i>
                {{ statisticsData.newPatientRateText }}
              </span>
              <span>较上期</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="summary-title">总收入</div>
            <div class="summary-value">¥{{ Number(statisticsData.totalRevenue).toFixed(2) }}</div>
            <div class="summary-footer">
              <span :class="[statisticsData.revenueRateClass]">
                <i :class="statisticsData.revenueRateIcon"></i>
                {{ statisticsData.revenueRateText }}
              </span>
              <span>较上期</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">
              <span>预约统计</span>
            </div>
            <div id="appointment-chart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">
              <span>收入统计</span>
            </div>
            <div id="revenue-chart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">
              <span>科室分布</span>
            </div>
            <div id="department-chart" class="chart"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header">
              <span>医生工作量</span>
            </div>
            <div id="doctor-workload-chart" class="chart"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'AdminData',
  data() {
    return {
      dateRange: [new Date(new Date().getTime() - 30 * 24 * 60 * 60 * 1000), new Date()],
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      // 统计数据
      statisticsData: {
        totalAppointments: 0,
        appointmentRateText: '0%',
        appointmentRateIcon: 'el-icon-caret-top',
        appointmentRateClass: 'rate-up',
        
        completedAppointments: 0,
        completionRateText: '0%',
        completionRateIcon: 'el-icon-caret-top',
        completionRateClass: 'rate-up',
        
        newPatients: 0,
        newPatientRateText: '0%',
        newPatientRateIcon: 'el-icon-caret-top',
        newPatientRateClass: 'rate-up',
        
        totalRevenue: 0.00,
        revenueRateText: '0%',
        revenueRateIcon: 'el-icon-caret-top',
        revenueRateClass: 'rate-up'
      },
      
      // 图表实例
      appointmentChart: null,
      revenueChart: null,
      departmentChart: null,
      doctorWorkloadChart: null,
      
      // 数据
      appointmentData: [],
      revenueData: [],
      departmentData: [],
      doctorWorkloadData: []
    }
  },
  mounted() {
    this.initCharts()
    this.fetchData()
    
    // 监听窗口大小变化，重绘图表
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    
    // 销毁图表实例
    this.appointmentChart && this.appointmentChart.dispose()
    this.revenueChart && this.revenueChart.dispose()
    this.departmentChart && this.departmentChart.dispose()
    this.doctorWorkloadChart && this.doctorWorkloadChart.dispose()
  },
  methods: {
    // 初始化图表
    initCharts() {
      // 预约统计图表
      this.appointmentChart = echarts.init(document.getElementById('appointment-chart'))
      
      // 收入统计图表
      this.revenueChart = echarts.init(document.getElementById('revenue-chart'))
      
      // 科室分布图表
      this.departmentChart = echarts.init(document.getElementById('department-chart'))
      
      // 医生工作量图表
      this.doctorWorkloadChart = echarts.init(document.getElementById('doctor-workload-chart'))
    },
    
    // 重绘图表
    resizeCharts() {
      this.appointmentChart && this.appointmentChart.resize()
      this.revenueChart && this.revenueChart.resize()
      this.departmentChart && this.departmentChart.resize()
      this.doctorWorkloadChart && this.doctorWorkloadChart.resize()
    },
    
    // 处理日期变化
    handleDateChange() {
      this.fetchData()
    },
    
    // 获取数据
    fetchData() {
      const startDate = this.formatDate(this.dateRange[0])
      const endDate = this.formatDate(this.dateRange[1])
      
      // 获取统计数据
      this.$http.get(`/admin/statistics?startDate=${startDate}&endDate=${endDate}`)
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data
            
            // 更新统计数据
            this.statisticsData.totalAppointments = data.totalAppointments || 0
            this.statisticsData.appointmentRateText = (data.appointmentRate || 0) + '%'
            this.statisticsData.appointmentRateIcon = (data.appointmentRate || 0) >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'
            this.statisticsData.appointmentRateClass = (data.appointmentRate || 0) >= 0 ? 'rate-up' : 'rate-down'
            
            this.statisticsData.completedAppointments = data.completedAppointments || 0
            this.statisticsData.completionRateText = (data.completionRate || 0) + '%'
            this.statisticsData.completionRateIcon = (data.completionRate || 0) >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'
            this.statisticsData.completionRateClass = (data.completionRate || 0) >= 0 ? 'rate-up' : 'rate-down'
            
            this.statisticsData.newPatients = data.totalPatients || 0
            this.statisticsData.newPatientRateText = (data.newPatientRate || 0) + '%'
            this.statisticsData.newPatientRateIcon = (data.newPatientRate || 0) >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'
            this.statisticsData.newPatientRateClass = (data.newPatientRate || 0) >= 0 ? 'rate-up' : 'rate-down'
            
            this.statisticsData.totalRevenue = Number(data.totalIncome || 0)
            this.statisticsData.revenueRateText = (data.revenueRate || 0) + '%'
            this.statisticsData.revenueRateIcon = (data.revenueRate || 0) >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'
            this.statisticsData.revenueRateClass = (data.revenueRate || 0) >= 0 ? 'rate-up' : 'rate-down'
            
            // 更新图表数据
            this.appointmentData = data.appointmentTrendData || []
            this.revenueData = data.incomeTrendData || []
            
            // 转换科室数据格式
            const departmentData = []
            if (Array.isArray(data.departmentData)) {
              data.departmentData.forEach(item => {
                if (item && item.department) {
                  departmentData.push({
                    name: item.department,
                    value: Number(item.doctorCount || 0)
                  })
                }
              })
            }
            this.departmentData = departmentData
            
            // 转换医生工作量数据格式
            const doctorData = []
            if (Array.isArray(data.topDoctors)) {
              data.topDoctors.forEach(item => {
                if (item) {
                  doctorData.push({
                    name: item.realName || item.username || '未知医生',
                    appointments: Number(item.appointmentCount || 0)
                  })
                }
              })
            }
            this.doctorWorkloadData = doctorData
            
            // 更新图表
            this.updateAppointmentChart()
            this.updateRevenueChart()
            this.updateDepartmentChart()
            this.updateDoctorWorkloadChart()
          }
        })
        .catch(error => {
          console.error('获取统计数据失败', error)
          this.$message.error('获取统计数据失败，请稍后重试')
        })
    },
    
    // 更新预约统计图表
    updateAppointmentChart() {
      if (!this.appointmentData || this.appointmentData.length === 0) {
        this.appointmentChart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center'
          }
        })
        return
      }
      
      const dates = this.appointmentData.map(item => item.date)
      const appointments = this.appointmentData.map(item => item.appointments)
      const completed = this.appointmentData.map(item => item.completed)
      const canceled = this.appointmentData.map(item => item.canceled)
      
      this.appointmentChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['总预约数', '已完成', '已取消'],
          bottom: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '12%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总预约数',
            type: 'line',
            data: appointments,
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '已完成',
            type: 'bar',
            data: completed,
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '已取消',
            type: 'bar',
            data: canceled,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      })
    },
    
    // 更新收入统计图表
    updateRevenueChart() {
      if (!this.revenueData || this.revenueData.length === 0) {
        this.revenueChart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center'
          }
        })
        return
      }
      
      const dates = this.revenueData.map(item => item.date)
      const revenues = this.revenueData.map(item => item.revenue)
      
      this.revenueChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: function(params) {
            return params[0].name + '<br/>' + params[0].seriesName + ': ¥' + params[0].value.toFixed(2)
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '金额（元）',
          axisLabel: {
            formatter: function(value) {
              return '¥' + value
            }
          }
        },
        series: [
          {
            name: '收入',
            type: 'line',
            data: revenues,
            smooth: true,
            areaStyle: {},
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      })
    },
    
    // 更新科室分布图表
    updateDepartmentChart() {
      // 确保有数据并且数据格式正确
      if (!this.departmentData || this.departmentData.length === 0) {
        if (this.departmentChart) {
          this.departmentChart.setOption({
            title: {
              text: '暂无数据',
              left: 'center',
              top: 'center'
            },
            series: []  // 清空系列数据
          })
        }
        return
      }

      // 准备图表需要的名称列表
      const names = this.departmentData.map(item => item.name)

      this.departmentChart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: names
        },
        series: [
          {
            name: '科室分布',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.departmentData
          }
        ]
      })
    },
    
    // 更新医生工作量图表
    updateDoctorWorkloadChart() {
      // 确保有数据并且数据格式正确
      if (!this.doctorWorkloadData || this.doctorWorkloadData.length === 0) {
        if (this.doctorWorkloadChart) {
          this.doctorWorkloadChart.setOption({
            title: {
              text: '暂无数据',
              left: 'center',
              top: 'center'
            },
            series: [] // 清空系列数据
          })
        }
        return
      }
      
      // 查找有效的医生名字和预约数
      const doctors = []
      const appointments = []
      
      this.doctorWorkloadData.forEach(item => {
        if (item && item.name) {
          doctors.push(item.name)
          appointments.push(item.appointments || 0)
        }
      })
      
      if (doctors.length === 0) {
        if (this.doctorWorkloadChart) {
          this.doctorWorkloadChart.setOption({
            title: {
              text: '数据格式不正确',
              left: 'center',
              top: 'center'
            },
            series: [] // 清空系列数据
          })
        }
        return
      }
      
      this.doctorWorkloadChart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: doctors,
          axisLabel: {
            interval: 0,
            rotate: 30
          }
        },
        series: [
          {
            name: '完成预约数',
            type: 'bar',
            data: appointments,
            itemStyle: {
              color: function(params) {
                // 颜色列表
                const colorList = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
                return colorList[params.dataIndex % colorList.length]
              }
            }
          }
        ]
      })
    },
    
    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.data-container {
  padding-bottom: 20px;
}

.main-card {
  margin-bottom: 20px;
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  height: 120px;
  text-align: center;
}

.summary-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.summary-footer {
  font-size: 12px;
  color: #909399;
}

.rate-up {
  color: #67C23A;
}

.rate-down {
  color: #F56C6C;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 350px;
  margin-bottom: 20px;
}

.chart {
  height: 280px;
}
</style>
