<template>
  <div class="payment-container">
    <el-card class="payment-card">
      <div slot="header">
        <span>缴费管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshPayments">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      
      <!-- 待支付项目 -->
      <div class="payment-section">
        <h3>待支付项目</h3>
        <div v-if="unpaidAppointments.length > 0">
          <el-table
            :data="unpaidAppointments"
            style="width: 100%"
            border>
            <el-table-column prop="id" label="预约号" width="80"></el-table-column>
            <el-table-column prop="treatmentItem" label="就诊项目" width="150"></el-table-column>
            <el-table-column prop="appointmentTime" label="预约时间" width="180">
              <template slot-scope="scope">
                {{ scope.row.appointmentDate }} {{ scope.row.appointmentTime }}
              </template>
            </el-table-column>
            <el-table-column prop="doctorName" label="医生" width="120"></el-table-column>
            <el-table-column prop="status" label="预约状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="paymentAmount" label="应付金额" width="120">
              <template slot-scope="scope">
                ¥{{ scope.row.paymentAmount || getFeeByTreatment(scope.row.treatmentItem) }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button 
                  type="primary" 
                  size="mini" 
                  @click="payNow(scope.row)"
                  :disabled="scope.row.status === 3">
                  立即支付
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-data">
          <i class="el-icon-wallet"></i>
          <p>暂无待缴费项目</p>
        </div>
      </div>
      
      <!-- 支付记录 -->
      <div class="payment-section">
        <h3>支付记录</h3>
        <div v-if="paidAppointments.length > 0">
          <el-table
            :data="paidAppointments"
            style="width: 100%"
            border>
            <el-table-column prop="id" label="预约号" width="80"></el-table-column>
            <el-table-column prop="treatmentItem" label="就诊项目" width="150"></el-table-column>
            <el-table-column prop="appointmentTime" label="预约时间" width="180">
              <template slot-scope="scope">
                {{ scope.row.appointmentDate }} {{ scope.row.appointmentTime }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentTime" label="支付时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.paymentTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="paymentAmount" label="支付金额" width="120">
              <template slot-scope="scope">
                ¥{{ scope.row.paymentAmount }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button type="info" size="mini" @click="viewInvoice(scope.row)">查看发票</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-data">
          <i class="el-icon-document"></i>
          <p>暂无支付记录</p>
        </div>
      </div>
    </el-card>
    
    <!-- 支付对话框 -->
    <el-dialog title="支付确认" :visible.sync="paymentDialogVisible" width="500px">
      <div v-if="currentPayment" class="payment-dialog-content">
        <div class="payment-info">
          <div class="payment-item">
            <span class="label">预约号：</span>
            <span class="value">{{ currentPayment.id }}</span>
          </div>
          <div class="payment-item">
            <span class="label">就诊项目：</span>
            <span class="value">{{ currentPayment.treatmentItem }}</span>
          </div>
          <div class="payment-item">
            <span class="label">预约时间：</span>
            <span class="value">{{ currentPayment.appointmentDate }} {{ currentPayment.appointmentTime }}</span>
          </div>
          <div class="payment-item payment-amount">
            <span class="label">应付金额：</span>
            <span class="value amount">¥{{ currentPayment.paymentAmount || getFeeByTreatment(currentPayment.treatmentItem) }}</span>
          </div>
        </div>
        
        <div class="payment-method">
          <div class="label">支付方式：</div>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="unionpay">银联云闪付</el-radio>
          </el-radio-group>
        </div>
        
        <div class="payment-qrcode">
          <img src="https://via.placeholder.com/200x200.png?text=支付二维码" alt="支付二维码" />
          <p class="text-center">请使用{{ getPaymentMethodText() }}扫码支付</p>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPayment" :loading="paymentLoading">确认已支付</el-button>
      </span>
    </el-dialog>
    
    <!-- 发票对话框 -->
    <el-dialog title="电子发票" :visible.sync="invoiceDialogVisible" width="650px">
      <div v-if="currentInvoice" class="invoice-content">
        <div class="invoice-header">
          <h2>电子发票</h2>
          <p>发票编号：INV{{ currentInvoice.id }}{{ Math.floor(Math.random() * 10000).toString().padStart(4, '0') }}</p>
        </div>
        
        <div class="invoice-info">
          <div class="invoice-row">
            <div class="invoice-label">患者姓名：</div>
            <div class="invoice-value">{{ currentUser.realName }}</div>
          </div>
          <div class="invoice-row">
            <div class="invoice-label">就诊项目：</div>
            <div class="invoice-value">{{ currentInvoice.treatmentItem }}</div>
          </div>
          <div class="invoice-row">
            <div class="invoice-label">就诊医生：</div>
            <div class="invoice-value">{{ currentInvoice.doctorName }}</div>
          </div>
          <div class="invoice-row">
            <div class="invoice-label">预约时间：</div>
            <div class="invoice-value">{{ currentInvoice.appointmentDate }} {{ currentInvoice.appointmentTime }}</div>
          </div>
          <div class="invoice-row">
            <div class="invoice-label">开票日期：</div>
            <div class="invoice-value">{{ formatDate(currentInvoice.paymentTime) }}</div>
          </div>
        </div>
        
        <div class="invoice-table">
          <table>
            <thead>
              <tr>
                <th>项目</th>
                <th>数量</th>
                <th>单价(元)</th>
                <th>金额(元)</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{{ currentInvoice.treatmentItem }}</td>
                <td>1</td>
                <td>{{ currentInvoice.paymentAmount }}</td>
                <td>{{ currentInvoice.paymentAmount }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="3" class="text-right">合计金额：</td>
                <td>{{ currentInvoice.paymentAmount }}</td>
              </tr>
              <tr>
                <td colspan="3" class="text-right">实收金额：</td>
                <td>{{ currentInvoice.paymentAmount }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
        
        <div class="invoice-footer">
          <p>备注：此电子发票可作为报销凭证，与纸质发票具有同等法律效力。</p>
          <div class="invoice-stamp">
            <span>智能齿科医院电子印章</span>
          </div>
        </div>
      </div>
      
      <span slot="footer" class="dialog-footer">
        <el-button @click="invoiceDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="printInvoice">打印发票</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PatientPayment',
  data() {
    return {
      doctorList: [],
      unpaidAppointments: [],
      paidAppointments: [],
      paymentDialogVisible: false,
      invoiceDialogVisible: false,
      currentPayment: null,
      currentInvoice: null,
      paymentMethod: 'wechat',
      paymentLoading: false,
      treatmentFees: {
        '普通检查': 100,
        '洗牙': 200,
        '补牙': 500,
        '拔牙': 300,
        '正牙': 5000,
        '种植牙': 8000
      }
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  mounted() {
    this.fetchDoctors()
    this.fetchAppointments()
  },
  methods: {
    // 获取医生列表
    fetchDoctors() {
      this.$http.get('/user/doctors')
        .then(response => {
          if (response.data.code === 200) {
            this.doctorList = response.data.data
          }
        })
        .catch(error => {
          console.error('获取医生列表错误:', error)
        })
    },
    
    // 获取预约列表
    fetchAppointments() {
      this.$http.get(`/appointment/patient/${this.currentUser.id}`)
        .then(response => {
          if (response.data.code === 200) {
            const appointments = response.data.data || []
            
            // 分类预约：已支付和未支付
            this.unpaidAppointments = appointments
              .filter(item => item.paymentStatus === 0 && item.status !== 3)
              .map(item => {
                return {
                  ...item,
                  doctorName: item.doctorName || '医生' + item.doctorId, // 实际应该从后端获取医生名称
                  paymentAmount: item.orderAmount || this.getFeeByTreatment(item.treatmentItem)
                }
              })
            
            this.paidAppointments = appointments
              .filter(item => item.paymentStatus === 1)
              .map(item => {
                return {
                  ...item,
                  doctorName: item.doctorName || '医生' + item.doctorId,
                  paymentAmount: item.orderAmount || this.getFeeByTreatment(item.treatmentItem)
                }
              })
          }
        })
        .catch(error => {
          console.error('获取预约记录失败:', error)
          this.$message.error('获取预约记录失败，请稍后重试')
        })
    },
    
    // 根据治疗项目获取费用
    getFeeByTreatment(treatment) {
      return this.treatmentFees[treatment] || 300 // 默认300元
    },
    
    // 立即支付
    payNow(appointment) {
      this.currentPayment = { ...appointment }
      this.paymentMethod = 'wechat'
      this.paymentDialogVisible = true
    },
    
    // 确认支付
    confirmPayment() {
      if (!this.currentPayment) return
      
      this.paymentLoading = true
      
      // 计算支付金额
      const amount = this.currentPayment.paymentAmount || this.getFeeByTreatment(this.currentPayment.treatmentItem)
      
      // 模拟支付请求
      setTimeout(() => {
        this.$http.put(`/appointment/${this.currentPayment.id}/pay?amount=${amount}&paymentMethod=${this.paymentMethod}`)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('支付成功')
              this.paymentDialogVisible = false
              
              // 在前端更新当前支付对象的状态，避免刷新
              const updatedAppointment = response.data.data || this.currentPayment
              updatedAppointment.paymentStatus = 1
              updatedAppointment.paymentTime = new Date()
              
              // 从未支付列表中移除
              const index = this.unpaidAppointments.findIndex(item => item.id === this.currentPayment.id)
              if (index !== -1) {
                this.unpaidAppointments.splice(index, 1)
              }
              
              // 添加到已支付列表
              this.paidAppointments.unshift({
                ...updatedAppointment,
                doctorName: this.currentPayment.doctorName,
                paymentAmount: amount
              })
              
              // 刷新支付记录
              this.fetchAppointments()
            } else {
              this.$message.error(response.data.message || '支付失败')
            }
          })
          .catch(error => {
            console.error('支付错误:', error)
            this.$message.error('支付失败，请稍后重试')
          })
          .finally(() => {
            this.paymentLoading = false
          })
      }, 1500) // 模拟网络延迟
    },
    
    // 查看发票
    viewInvoice(appointment) {
      this.currentInvoice = { ...appointment }
      this.invoiceDialogVisible = true
    },
    
    // 打印发票
    printInvoice() {
      this.$message.success('打印功能已触发，请检查打印机状态')
      
      // 这里可以实现实际的打印功能，例如使用window.print()或生成PDF
      const printWindow = window.open('', '_blank')
      const invoice = this.currentInvoice
      
      const html = `
        <!DOCTYPE html>
        <html>
          <head>
            <title>电子发票</title>
            <style>
              body { font-family: Arial, sans-serif; margin: 20px; }
              .invoice-header { text-align: center; margin-bottom: 30px; }
              .invoice-header h2 { margin-bottom: 5px; }
              .invoice-info { margin-bottom: 30px; }
              .invoice-row { display: flex; margin-bottom: 10px; }
              .invoice-label { width: 150px; font-weight: bold; }
              .invoice-value { flex: 1; }
              .invoice-table { margin-bottom: 30px; }
              .invoice-table table { width: 100%; border-collapse: collapse; }
              .invoice-table th, .invoice-table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
              .invoice-table th { background-color: #f2f2f2; }
              .invoice-table tfoot td { font-weight: bold; }
              .text-right { text-align: right; }
              .invoice-footer { margin-top: 50px; }
              .invoice-stamp { margin-top: 30px; text-align: right; border: 2px dashed #f00; display: inline-block; padding: 10px 20px; color: #f00; font-weight: bold; float: right; }
            </style>
          </head>
          <body>
            <div class="invoice-header">
              <h2>电子发票</h2>
              <p>发票编号：INV${invoice.id}${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}</p>
            </div>
            
            <div class="invoice-info">
              <div class="invoice-row">
                <div class="invoice-label">患者姓名：</div>
                <div class="invoice-value">${this.currentUser.realName}</div>
              </div>
              <div class="invoice-row">
                <div class="invoice-label">就诊项目：</div>
                <div class="invoice-value">${invoice.treatmentItem}</div>
              </div>
              <div class="invoice-row">
                <div class="invoice-label">就诊医生：</div>
                <div class="invoice-value">${invoice.doctorName}</div>
              </div>
              <div class="invoice-row">
                <div class="invoice-label">预约时间：</div>
                <div class="invoice-value">${invoice.appointmentDate} ${invoice.appointmentTime}</div>
              </div>
              <div class="invoice-row">
                <div class="invoice-label">开票日期：</div>
                <div class="invoice-value">${this.formatDate(invoice.paymentTime)}</div>
              </div>
            </div>
            
            <div class="invoice-table">
              <table>
                <thead>
                  <tr>
                    <th>项目</th>
                    <th>数量</th>
                    <th>单价(元)</th>
                    <th>金额(元)</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>${invoice.treatmentItem}</td>
                    <td>1</td>
                    <td>${invoice.paymentAmount}</td>
                    <td>${invoice.paymentAmount}</td>
                  </tr>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="3" class="text-right">合计金额：</td>
                    <td>${invoice.paymentAmount}</td>
                  </tr>
                  <tr>
                    <td colspan="3" class="text-right">实收金额：</td>
                    <td>${invoice.paymentAmount}</td>
                  </tr>
                </tfoot>
              </table>
            </div>
            
            <div class="invoice-footer">
              <p>备注：此电子发票可作为报销凭证，与纸质发票具有同等法律效力。</p>
              <div class="invoice-stamp">
                <span>智能齿科医院电子印章</span>
              </div>
              <div style="clear: both;"></div>
            </div>
          </body>
        </html>
      `
      printWindow.document.write(html)
      printWindow.document.close()
      printWindow.onload = function() {
        printWindow.print()
        // printWindow.close()
      }
    },
    
    // 刷新支付记录
    refreshPayments() {
      this.fetchAppointments()
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 获取状态文本
    getStatusText(status) {
      switch (status) {
        case 0: return '待确认'
        case 1: return '已确认'
        case 2: return '已完成'
        case 3: return '已取消'
        default: return '未知状态'
      }
    },
    
    // 获取状态类型（用于标签颜色）
    getStatusType(status) {
      switch (status) {
        case 0: return 'warning'
        case 1: return 'primary'
        case 2: return 'success'
        case 3: return 'danger'
        default: return 'info'
      }
    },
    
    // 获取支付方式文本
    getPaymentMethodText() {
      switch (this.paymentMethod) {
        case 'wechat': return '微信'
        case 'alipay': return '支付宝'
        case 'unionpay': return '银联云闪付'
        default: return ''
      }
    }
  }
}
</script>

<style scoped>
.payment-container {
  height: 100%;
}

.payment-card {
  margin-bottom: 20px;
}

.payment-section {
  margin-bottom: 30px;
}

.payment-section h3 {
  margin-top: 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.empty-data {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.empty-data i {
  font-size: 40px;
  margin-bottom: 10px;
}

.payment-dialog-content {
  padding: 0 20px;
}

.payment-info {
  margin-bottom: 20px;
}

.payment-item {
  margin-bottom: 10px;
  display: flex;
}

.payment-item .label {
  width: 100px;
  color: #606266;
}

.payment-item .value {
  flex: 1;
}

.payment-amount {
  font-size: 18px;
}

.payment-amount .amount {
  color: #f56c6c;
  font-weight: bold;
}

.payment-method {
  margin-bottom: 20px;
}

.payment-method .label {
  margin-bottom: 10px;
  color: #606266;
}

.payment-qrcode {
  text-align: center;
  margin: 20px 0;
}

.payment-qrcode img {
  width: 200px;
  height: 200px;
  border: 1px solid #dcdfe6;
}

.text-center {
  text-align: center;
}

/* 发票样式 */
.invoice-content {
  padding: 20px;
}

.invoice-header {
  text-align: center;
  margin-bottom: 30px;
}

.invoice-header h2 {
  margin-bottom: 5px;
}

.invoice-info {
  margin-bottom: 30px;
}

.invoice-row {
  display: flex;
  margin-bottom: 10px;
}

.invoice-label {
  width: 150px;
  font-weight: bold;
}

.invoice-value {
  flex: 1;
}

.invoice-table {
  margin-bottom: 30px;
}

.invoice-table table {
  width: 100%;
  border-collapse: collapse;
}

.invoice-table th,
.invoice-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.invoice-table th {
  background-color: #f2f2f2;
}

.invoice-table tfoot td {
  font-weight: bold;
}

.text-right {
  text-align: right;
}

.invoice-footer {
  margin-top: 50px;
}

.invoice-stamp {
  margin-top: 30px;
  text-align: right;
  border: 2px dashed #f00;
  padding: 10px 20px;
  color: #f00;
  font-weight: bold;
  float: right;
}
</style>
