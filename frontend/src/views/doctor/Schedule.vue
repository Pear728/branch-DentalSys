<template>
  <div class="schedule-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="schedule-card">
          <div slot="header">
            <span>我的排班</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshSchedules">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          
          <el-calendar class="schedule-calendar">
            <template slot="dateCell" slot-scope="{date, data}">
              <div 
                class="calendar-cell"
                :class="{
                  'has-schedule': hasSchedule(data.day),
                  'selected': isDateSelected(data.day)
                }"
                @click="handleDateClick(data.day)">
                <div class="calendar-day">{{ data.day.split('-')[2] }}</div>
                <div v-if="hasSchedule(data.day)" class="schedule-time">
                  {{ getScheduleTime(data.day) }}
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="date-detail-card">
          <div slot="header">
            <span>{{ selectedDate ? formatDate(selectedDate) + ' 排班详情' : '日期详情' }}</span>
            <el-button 
              v-if="selectedDate"
              style="float: right; padding: 3px 0" 
              type="text" 
              @click="createSchedule">
              {{ currentSchedule ? '修改排班' : '添加排班' }}
            </el-button>
          </div>
          
          <div v-if="!selectedDate" class="empty-detail">
            <i class="el-icon-date"></i>
            <p>请先在日历中选择一个日期</p>
          </div>
          
          <div v-else-if="currentSchedule" class="schedule-detail">
            <el-descriptions border direction="vertical" :column="1">
              <el-descriptions-item label="开始时间">{{ currentSchedule.startTime }}</el-descriptions-item>
              <el-descriptions-item label="结束时间">{{ currentSchedule.endTime }}</el-descriptions-item>
              <el-descriptions-item label="最大预约数">{{ currentSchedule.maxAppointments }}</el-descriptions-item>
              <el-descriptions-item label="已预约数">{{ currentSchedule.appointedCount }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="currentSchedule.status === 1 ? 'success' : 'danger'">
                  {{ currentSchedule.status === 1 ? '可预约' : '不可用' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="备注">{{ currentSchedule.remark || '无' }}</el-descriptions-item>
            </el-descriptions>
            
            <div class="schedule-operations">
              <el-button 
                type="primary" 
                size="small"
                :disabled="currentSchedule.appointedCount > 0"
                @click="createSchedule">
                修改排班
              </el-button>
              <el-button 
                :type="currentSchedule.status === 1 ? 'danger' : 'success'" 
                size="small"
                @click="toggleScheduleStatus">
                {{ currentSchedule.status === 1 ? '设为不可约' : '设为可预约' }}
              </el-button>
            </div>
          </div>
          
          <div v-else class="empty-schedule">
            <i class="el-icon-time"></i>
            <p>该日期暂无排班</p>
            <el-button type="primary" @click="createSchedule">添加排班</el-button>
          </div>
        </el-card>
        
        <el-card class="batch-card">
          <div slot="header">
            <span>批量排班</span>
          </div>
          
          <el-form :model="batchForm" :rules="batchRules" ref="batchForm" label-width="100px">
            <el-form-item label="日期范围" prop="dateRange">
              <el-date-picker
                v-model="batchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :picker-options="datePickerOptions"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
            
            <el-form-item label="时间段" required>
              <el-row :gutter="10">
                <el-col :span="11">
                  <el-form-item prop="startTime">
                    <el-time-picker
                      v-model="batchForm.startTime"
                      format="HH:mm"
                      placeholder="开始时间"
                      style="width: 100%">
                    </el-time-picker>
                  </el-form-item>
                </el-col>
                <el-col class="center" :span="2">至</el-col>
                <el-col :span="11">
                  <el-form-item prop="endTime">
                    <el-time-picker
                      v-model="batchForm.endTime"
                      format="HH:mm"
                      placeholder="结束时间"
                      style="width: 100%">
                    </el-time-picker>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item label="最大预约数" prop="maxAppointments">
              <el-input-number
                v-model="batchForm.maxAppointments"
                :min="1"
                :max="50"
                style="width: 100%">
              </el-input-number>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitBatchSchedule" :loading="batchLoading">批量创建</el-button>
              <el-button @click="resetBatchForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 排班编辑对话框 -->
    <el-dialog :title="currentSchedule ? '修改排班' : '添加排班'" :visible.sync="scheduleDialogVisible" width="500px">
      <el-form :model="scheduleForm" :rules="scheduleRules" ref="scheduleForm" label-width="100px">
        <el-form-item label="日期">
          <el-input v-model="formattedSelectedDate" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-time-picker
            v-model="scheduleForm.startTime"
            format="HH:mm"
            placeholder="开始时间"
            style="width: 100%">
          </el-time-picker>
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-time-picker
            v-model="scheduleForm.endTime"
            format="HH:mm"
            placeholder="结束时间"
            style="width: 100%">
          </el-time-picker>
        </el-form-item>
        
        <el-form-item label="最大预约数" prop="maxAppointments">
          <el-input-number
            v-model="scheduleForm.maxAppointments"
            :min="1"
            :max="50"
            style="width: 100%">
          </el-input-number>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="scheduleForm.status">
            <el-radio :label="1">可预约</el-radio>
            <el-radio :label="0">不可用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input
            type="textarea"
            v-model="scheduleForm.remark"
            placeholder="请输入备注信息"
            :rows="2">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSchedule" :loading="submitLoading">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DoctorSchedule',
  data() {
    // 验证结束时间必须大于开始时间
    const validateEndTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择结束时间'))
      } else if (!this.scheduleForm.startTime) {
        callback(new Error('请先选择开始时间'))
      } else {
        const startTime = new Date(this.scheduleForm.startTime)
        const endTime = new Date(value)
        if (endTime <= startTime) {
          callback(new Error('结束时间必须大于开始时间'))
        } else {
          callback()
        }
      }
    }
    
    // 批量创建时验证结束时间
    const validateBatchEndTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请选择结束时间'))
      } else if (!this.batchForm.startTime) {
        callback(new Error('请先选择开始时间'))
      } else {
        const startTime = new Date(this.batchForm.startTime)
        const endTime = new Date(value)
        if (endTime <= startTime) {
          callback(new Error('结束时间必须大于开始时间'))
        } else {
          callback()
        }
      }
    }
    
    return {
      schedules: [],
      selectedDate: null,
      currentSchedule: null,
      submitLoading: false,
      batchLoading: false,
      
      // 排班表单
      scheduleDialogVisible: false,
      scheduleForm: {
        id: null,
        doctorId: null,
        scheduleDate: null,
        startTime: null,
        endTime: null,
        maxAppointments: 10,
        status: 1,
        remark: ''
      },
      scheduleRules: {
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' },
          { validator: validateEndTime, trigger: 'change' }
        ],
        maxAppointments: [
          { required: true, message: '请设置最大预约数', trigger: 'change' }
        ]
      },
      
      // 批量排班表单
      batchForm: {
        dateRange: null,
        startTime: null,
        endTime: null,
        maxAppointments: 10
      },
      batchRules: {
        dateRange: [
          { required: true, message: '请选择日期范围', trigger: 'change' }
        ],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' },
          { validator: validateBatchEndTime, trigger: 'change' }
        ],
        maxAppointments: [
          { required: true, message: '请设置最大预约数', trigger: 'change' }
        ]
      },
      
      // 日期选择器选项
      datePickerOptions: {
        disabledDate(time) {
          // 禁用过去的日期
          const today = new Date()
          today.setHours(0, 0, 0, 0)
          return time.getTime() < today.getTime()
        }
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    
    // 格式化后的选中日期
    formattedSelectedDate() {
      return this.selectedDate ? this.formatDate(this.selectedDate) : ''
    }
  },
  mounted() {
    this.fetchSchedules()
  },
  methods: {
    // 获取排班列表
    fetchSchedules() {
      this.$http.get(`/schedule/doctor/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            this.schedules = response.data.data
            
            // 如果有选中的日期，更新当前排班信息
            if (this.selectedDate) {
              this.updateCurrentSchedule()
            }
          }
        })
        .catch(error => {
          console.error('获取排班列表错误:', error)
          this.$message.error('获取排班列表失败，请稍后重试')
        })
    },
    
    // 刷新排班列表
    refreshSchedules() {
      this.fetchSchedules()
    },
    
    // 检查指定日期是否有排班
    hasSchedule(day) {
      return this.schedules.some(schedule => {
        const scheduleDate = this.formatDate(new Date(schedule.scheduleDate))
        return scheduleDate === day
      })
    },
    
    // 获取指定日期的排班时间
    getScheduleTime(day) {
      const schedule = this.schedules.find(schedule => {
        const scheduleDate = this.formatDate(new Date(schedule.scheduleDate))
        return scheduleDate === day
      })
      
      if (schedule) {
        return `${schedule.startTime}-${schedule.endTime}`
      }
      
      return ''
    },
    
    // 处理日期点击
    handleDateClick(day) {
      // 转换为Date对象
      const date = new Date(day)
      
      // 如果点击相同日期，则取消选择
      if (this.selectedDate && this.formatDate(this.selectedDate) === day) {
        this.selectedDate = null
        this.currentSchedule = null
      } else {
        this.selectedDate = date
        this.updateCurrentSchedule()
      }
    },
    
    // 检查日期是否被选中
    isDateSelected(day) {
      return this.selectedDate && this.formatDate(this.selectedDate) === day
    },
    
    // 更新当前选中日期的排班信息
    updateCurrentSchedule() {
      if (!this.selectedDate) return
      
      const selectedDateStr = this.formatDate(this.selectedDate)
      const schedule = this.schedules.find(schedule => {
        const scheduleDate = this.formatDate(new Date(schedule.scheduleDate))
        return scheduleDate === selectedDateStr
      })
      
      this.currentSchedule = schedule || null
    },
    
    // 创建/修改排班
    createSchedule() {
      if (!this.selectedDate) {
        this.$message.warning('请先选择一个日期')
        return
      }
      
      if (this.currentSchedule && this.currentSchedule.appointedCount > 0) {
        this.$message.warning('该排班已有患者预约，无法修改')
        return
      }
      
      // 初始化表单
      this.scheduleForm = this.currentSchedule ? { ...this.currentSchedule } : {
        id: null,
        doctorId: this.currentUser.id,
        scheduleDate: this.selectedDate,
        startTime: null,
        endTime: null,
        maxAppointments: 10,
        status: 1,
        remark: ''
      }
      
      // 如果是修改排班，需要转换startTime和endTime为Date对象
      if (this.currentSchedule) {
        const [startHour, startMinute] = this.currentSchedule.startTime.split(':').map(Number)
        const [endHour, endMinute] = this.currentSchedule.endTime.split(':').map(Number)
        
        const startTime = new Date()
        startTime.setHours(startHour, startMinute, 0, 0)
        
        const endTime = new Date()
        endTime.setHours(endHour, endMinute, 0, 0)
        
        this.scheduleForm.startTime = startTime
        this.scheduleForm.endTime = endTime
      }
      
      this.scheduleDialogVisible = true
      
      // 等待Dialog渲染完成后，重置表单验证
      this.$nextTick(() => {
        this.$refs.scheduleForm && this.$refs.scheduleForm.clearValidate()
      })
    },
    
    // 提交排班
    submitSchedule() {
      this.$refs.scheduleForm.validate(valid => {
        if (valid) {
          this.submitLoading = true
          
          // 处理表单数据
          const formData = { ...this.scheduleForm }
          
          // 处理日期和时间
          formData.scheduleDate = this.formatDate(this.selectedDate)
          formData.startTime = this.formatTime(formData.startTime)
          formData.endTime = this.formatTime(formData.endTime)
          
          // 判断是创建还是更新
          const method = formData.id ? 'put' : 'post'
          const url = '/schedule'
          
          this.$http[method](url, formData)
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success(formData.id ? '排班修改成功' : '排班创建成功')
                this.scheduleDialogVisible = false
                
                // 刷新排班列表
                this.fetchSchedules()
              } else {
                this.$message.error(response.data.message || (formData.id ? '排班修改失败' : '排班创建失败'))
              }
            })
            .catch(error => {
              console.error('提交排班错误:', error)
              this.$message.error(formData.id ? '排班修改失败，请稍后重试' : '排班创建失败，请稍后重试')
            })
            .finally(() => {
              this.submitLoading = false
            })
        }
      })
    },
    
    // 切换排班状态
    toggleScheduleStatus() {
      if (!this.currentSchedule) return
      
      const newStatus = this.currentSchedule.status === 1 ? 0 : 1
      
      this.$http.put(`/schedule/${this.currentSchedule.id}/status/${newStatus}`)
        .then(response => {
          if (response.data.code === 200) {
            this.$message.success(`排班状态已修改为${newStatus === 1 ? '可预约' : '不可用'}`)
            
            // 更新排班状态
            this.currentSchedule.status = newStatus
          } else {
            this.$message.error(response.data.message || '修改排班状态失败')
          }
        })
        .catch(error => {
          console.error('修改排班状态错误:', error)
          this.$message.error('修改排班状态失败，请稍后重试')
        })
    },
    
    // 批量创建排班
    submitBatchSchedule() {
      this.$refs.batchForm.validate(valid => {
        if (valid) {
          this.batchLoading = true
          
          // 处理日期和时间
          const startDate = this.formatDate(this.batchForm.dateRange[0])
          const endDate = this.formatDate(this.batchForm.dateRange[1])
          const startTime = this.formatTime(this.batchForm.startTime)
          const endTime = this.formatTime(this.batchForm.endTime)
          
          const params = {
            doctorId: this.currentUser.id,
            startDate,
            endDate,
            startTime,
            endTime,
            maxAppointments: this.batchForm.maxAppointments
          }
          
          this.$http.post('/schedule/batch', null, { params })
            .then(response => {
              if (response.data.code === 200) {
                this.$message.success('批量排班创建成功')
                this.resetBatchForm()
                
                // 刷新排班列表
                this.fetchSchedules()
              } else {
                this.$message.error(response.data.message || '批量排班创建失败')
              }
            })
            .catch(error => {
              console.error('批量排班错误:', error)
              this.$message.error('批量排班创建失败，请稍后重试')
            })
            .finally(() => {
              this.batchLoading = false
            })
        }
      })
    },
    
    // 重置批量排班表单
    resetBatchForm() {
      this.$refs.batchForm.resetFields()
    },
    
    // 格式化日期为 YYYY-MM-DD
    formatDate(date) {
      if (!date) return ''
      
      const d = new Date(date)
      const year = d.getFullYear()
      const month = (d.getMonth() + 1).toString().padStart(2, '0')
      const day = d.getDate().toString().padStart(2, '0')
      
      return `${year}-${month}-${day}`
    },
    
    // 格式化时间为 HH:MM
    formatTime(date) {
      if (!date) return ''
      
      const d = new Date(date)
      const hours = d.getHours().toString().padStart(2, '0')
      const minutes = d.getMinutes().toString().padStart(2, '0')
      
      return `${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.schedule-container {
  height: 100%;
}

.schedule-card,
.date-detail-card,
.batch-card {
  margin-bottom: 20px;
}

.schedule-calendar {
  background-color: #fff;
}

.calendar-cell {
  height: 100%;
  padding: 4px;
  text-align: center;
  position: relative;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.calendar-cell:hover {
  background-color: #f5f7fa;
}

.calendar-day {
  font-weight: bold;
}

.has-schedule {
  background-color: #f0f9eb;
}

.has-schedule:hover {
  background-color: #e1f3d8;
}

.schedule-time {
  font-size: 12px;
  color: #67c23a;
  margin-top: 2px;
}

.selected {
  background-color: #e6f7ff;
  border: 1px solid #1890ff;
}

.selected.has-schedule {
  background-color: #d5f3c9;
}

.empty-detail,
.empty-schedule {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.empty-detail i,
.empty-schedule i {
  font-size: 40px;
  margin-bottom: 10px;
}

.schedule-detail {
  padding: 10px 0;
}

.schedule-operations {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.center {
  text-align: center;
  line-height: 40px;
}
</style>
