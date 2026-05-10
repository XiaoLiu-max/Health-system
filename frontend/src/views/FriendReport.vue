<template>
  <div class="friend-report-page">
    <div class="page-header">
      <h3>📊 {{ friendName || '好友' }} 的健康周报</h3>
      <div class="header-right">
        <el-date-picker
          v-model="queryDate"
          type="date"
          placeholder="选择日期查看周报"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="loadReport"
        />
      </div>
    </div>

    <div class="card-wrapper">
      <div class="report-card">
        <div class="card-header">
          <div class="title">📋 报告概览</div>
          <div class="period">{{ report.startDate || '-' }} ~ {{ report.endDate || '-' }}</div>
        </div>

        <div class="analysis-section">
          <h4>💡 健康分析</h4>
          <p class="analysis-text">
            {{ report.analysisText || '暂无周报数据' }}
          </p>
        </div>

        <div class="status-section">
          <h4>📊 核心指标</h4>
          <div class="status-grid">
            <div class="status-item">
              <div class="label">睡眠监测</div>
              <div class="value">{{ getStatusIcon('sleep') }} {{ getStatusText('sleep') }}</div>
            </div>
            <div class="status-item">
              <div class="label">血压监测</div>
              <div class="value">{{ getStatusIcon('bp') }} {{ getStatusText('bp') }}</div>
            </div>
            <div class="status-item">
              <div class="label">血糖监测</div>
              <div class="value">{{ getStatusIcon('sugar') }} {{ getStatusText('sugar') }}</div>
            </div>
            <div class="status-item">
              <div class="label">体重/BMI</div>
              <div class="value">{{ getStatusIcon('bmi') }} {{ getStatusText('bmi') }}</div>
            </div>
          </div>
        </div>

        <div class="chart-section" v-if="chartData">
          <h4>📈 健康数据趋势</h4>
          <div class="chart-box">
            <v-chart :option="chartOption" style="width: 100%; height: 360px" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
} from 'echarts/components'

// ✅ 和 Message.vue 一样用封装好的 request
import request from '@/utils/request'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent])

const route = useRoute()
const friendUserId = ref(Number(route.query.userId))
const friendName = ref('')

const queryDate = ref(new Date().toISOString().split('T')[0])
const report = ref({
  startDate: '',
  endDate: '',
  analysisText: '',
  chartData: '',
})

const chartData = ref(null)
const sleepAbnormal = ref(false)
const bpAbnormal = ref(false)
const sugarAbnormal = ref(false)
const bmiAbnormal = ref(false)

// 获取本周一
const getMondayOfWeek = (dateStr) => {
  const date = new Date(dateStr)
  const day = date.getDay()
  const diff = date.getDate() - day + (day === 0 ? -6 : 1)
  const monday = new Date(date.setDate(diff))
  return monday.toISOString().split('T')[0]
}

// 解析图表
const parseChartData = (jsonStr) => {
  if (!jsonStr) return null
  try {
    return JSON.parse(jsonStr)
  } catch (e) {
    return null
  }
}

// 图表配置
const chartOption = computed(() => {
  if (!chartData.value || !chartData.value.list || chartData.value.list.length === 0) {
    return {}
  }
  const list = chartData.value.list
  const dates = list.map((i) => i.recordDate)
  const sleep = list.map((i) => i.sleepHour)
  const sbp = list.map((i) => i.sbp)
  const sugar = list.map((i) => i.bloodSugar)
  const bmi = list.map((i) => i.bmi)

  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['睡眠时长', '收缩压', '血糖', 'BMI'] },
    xAxis: { data: dates },
    yAxis: {},
    series: [
      { name: '睡眠时长', type: 'line', data: sleep },
      { name: '收缩压', type: 'line', data: sbp },
      { name: '血糖', type: 'line', data: sugar },
      { name: 'BMI', type: 'line', data: bmi },
    ],
  }
})

// ✅ 加载好友周报 —— 和 Message 写法完全一样！！！
const loadReport = async () => {
  if (!friendUserId.value) return
  try {
    const monday = getMondayOfWeek(queryDate.value)

    // ✅ 这里 100% 和 Message.vue 风格一致
    const res = await request.get('/health/report/friend', {
      params: {
        userId: friendUserId.value,
        type: 1,
        date: monday,
      },
    })

    report.value = res.data || {}
    chartData.value = parseChartData(report.value.chartData)

    const text = report.value.analysisText || ''
    sleepAbnormal.value = text.includes('睡眠') && (text.includes('不足') || text.includes('长期'))
    bpAbnormal.value = text.includes('血压') && (text.includes('偏高') || text.includes('频繁'))
    sugarAbnormal.value = text.includes('血糖') && (text.includes('偏高') || text.includes('多次'))
    bmiAbnormal.value = text.includes('体重') || text.includes('BMI')
  } catch (err) {
    ElMessage.error('加载好友周报失败')
    console.error(err)
  }
}

// ✅ 获取好友名称 —— 同样用封装 request
const fetchFriendName = async () => {
  try {
    const res = await request.get('/user/search', {
      params: { keyword: friendUserId.value },
    })
    friendName.value = res.data?.username || '好友'
  } catch (e) {
    friendName.value = '好友'
  }
}

const getStatusIcon = (type) => {
  const map = { sleep: sleepAbnormal, bp: bpAbnormal, sugar: sugarAbnormal, bmi: bmiAbnormal }
  return map[type].value ? '⚠️' : '✅'
}
const getStatusText = (type) => {
  const map = { sleep: sleepAbnormal, bp: bpAbnormal, sugar: sugarAbnormal, bmi: bmiAbnormal }
  return map[type].value ? '需关注' : '正常'
}

onMounted(() => {
  fetchFriendName()
  loadReport()
})
</script>

<style scoped>
.friend-report-page {
  padding: 20px;
  background-color: #edf7f0;
  min-height: calc(100vh - 120px);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-wrapper {
  max-width: 960px;
  margin: 0 auto;
}
.report-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}
.title {
  font-size: 18px;
  font-weight: bold;
}
.period {
  color: #666;
  font-size: 14px;
}
.analysis-section {
  margin-bottom: 24px;
}
.analysis-text {
  line-height: 1.7;
  color: #333;
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}
.status-section {
  margin-bottom: 24px;
}
.status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.status-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}
.label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}
.value {
  font-size: 16px;
  font-weight: bold;
}
.chart-box {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}
</style>
