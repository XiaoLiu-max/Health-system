<template>
  <div class="report-page">
    <div class="page-header">
      <h3>📊 我的本周健康周报</h3>
      <div class="header-right">
        <el-date-picker
          v-model="queryDate"
          type="date"
          placeholder="选择日期查看过往周报"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="loadReport"
        />
        <el-button type="primary" class="gen-btn" @click="generateWeekReport" :loading="generating">
          生成本周报
        </el-button>
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
            {{ report.analysisText || '暂无周报数据，请先录入本周健康数据。' }}
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

        <!-- 这里是图表：后端已经有数据，前端现在能显示了 -->
        <div class="chart-section" v-if="chartData">
          <h4>📈 健康数据趋势图</h4>
          <div class="chart-box">
            <v-chart :option="chartOption" ref="chart" style="width: 100%; height: 360px"></v-chart>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from 'axios'
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

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent])

const queryDate = ref(new Date().toISOString().split('T')[0])
const report = ref({
  startDate: '',
  endDate: '',
  analysisText: '',
  chartData: '',
})
const generating = ref(false)
const chartData = ref(null)

const sleepAbnormal = ref(false)
const bpAbnormal = ref(false)
const sugarAbnormal = ref(false)
const bmiAbnormal = ref(false)

const getMondayOfWeek = (dateStr) => {
  const date = new Date(dateStr)
  const day = date.getDay()
  const diff = date.getDate() - day + (day === 0 ? -6 : 1)
  const monday = new Date(date.setDate(diff))
  return monday.toISOString().split('T')[0]
}

const parseChartData = (jsonStr) => {
  if (!jsonStr) return null
  try {
    return JSON.parse(jsonStr)
  } catch (e) {
    return null
  }
}

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
    legend: { data: ['睡眠', '收缩压', '血糖', 'BMI'] },
    xAxis: { data: dates },
    yAxis: {},
    series: [
      { name: '睡眠', type: 'line', data: sleep },
      { name: '收缩压', type: 'line', data: sbp },
      { name: '血糖', type: 'line', data: sugar },
      { name: 'BMI', type: 'line', data: bmi },
    ],
  }
})

const loadReport = async () => {
  if (!queryDate.value) return
  try {
    const monday = getMondayOfWeek(queryDate.value)
    const res = await axios.get('/health/report/by-date', {
      params: { type: 1, date: monday },
    })
    report.value = res.data.data || {}
    chartData.value = parseChartData(report.value.chartData)

    const text = report.value.analysisText || ''
    sleepAbnormal.value = text.includes('睡眠') && (text.includes('不足') || text.includes('长期'))
    bpAbnormal.value = text.includes('血压') && (text.includes('偏高') || text.includes('频繁'))
    sugarAbnormal.value = text.includes('血糖') && (text.includes('偏高') || text.includes('多次'))
    bmiAbnormal.value = text.includes('体重') || text.includes('BMI')
  } catch (err) {
    ElMessage.error('加载失败')
  }
}

const generateWeekReport = async () => {
  if (!queryDate.value) {
    ElMessage.warning('请选择日期')
    return
  }
  generating.value = true
  try {
    await axios.get('/health/genMyWeekReport', { params: { date: queryDate.value } })
    ElMessage.success('✅ 周报生成成功！')
    loadReport()
  } catch (e) {
    ElMessage.error('生成失败')
  } finally {
    generating.value = false
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
  loadReport()
})
</script>

<style scoped>
.report-page {
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

.header-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.page-header h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.card-wrapper {
  max-width: 800px;
}

.report-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
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

.analysis-section,
.status-section,
.chart-section {
  margin-bottom: 25px;
}

h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.analysis-text {
  line-height: 1.8;
  color: #555;
  background: #f9fafb;
  padding: 15px;
  border-radius: 8px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.status-item {
  text-align: center;
  padding: 15px;
  background: #f9fafb;
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
  background: #f9fafb;
  padding: 15px;
  border-radius: 8px;
}
</style>
