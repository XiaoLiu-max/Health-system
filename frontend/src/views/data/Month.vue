<template>
  <div class="report-page">
    <div class="page-header">
      <h3>📊 健康月报</h3>
      <div class="header-right">
        <el-date-picker
          v-model="date"
          type="date"
          placeholder="选择日期查看过往月报"
          value-format="YYYY-MM-DD"
          @change="loadReport"
        />
        <el-button type="primary" @click="generateMonthReport" :loading="generating">
          生成本月报
        </el-button>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <div class="period">{{ report.startDate || '-' }} ~ {{ report.endDate || '-' }}</div>
      </div>

      <div class="analysis-section">
        <h4>💡 健康分析</h4>
        <p class="text">
          {{ report.analysisText || '暂无月报数据，请先录入本月健康数据。' }}
        </p>
      </div>

      <!-- 月报核心指标（和周报保持一致） -->
      <div class="status-section" v-if="report.analysisText">
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

      <!-- 月报折线图 -->
      <div class="chart-section" v-if="chartData && chartData.list && chartData.list.length">
        <h4>📈 本月健康数据趋势图</h4>
        <div class="chart-box">
          <v-chart :option="chartOption" style="width: 100%; height: 360px"></v-chart>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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

// 注册ECharts组件
use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, GridComponent, LegendComponent])

const date = ref(new Date().toISOString().split('T')[0])
const report = ref({
  startDate: '',
  endDate: '',
  analysisText: '',
  chartData: '',
})
const generating = ref(false)
const chartData = ref(null)

// 指标状态
const sleepAbnormal = ref(false)
const bpAbnormal = ref(false)
const sugarAbnormal = ref(false)
const bmiAbnormal = ref(false)

// 解析后端返回的图表数据
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
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#fff',
      textStyle: { color: '#333' },
    },
    legend: {
      data: ['睡眠', '收缩压', '血糖', 'BMI'],
      top: 0,
      left: 'center',
      height: 30,
    },
    grid: {
      left: '5%',
      right: '5%',
      top: '12%',
      bottom: '18%',
      containLabel: true,
    },

    // ======================
    // 🔥 核心：数据过多时自动开启左右滑动
    // ======================
    dataZoom: [
      {
        type: 'slider', // 底部滑动条
        show: true,
        xAxisIndex: [0],
        start: 0, // 默认从第0个数据开始
        end: 100, // 默认显示100%宽度
        height: 18, // 滑动条高度
      },
      {
        type: 'inside', // 支持鼠标滚轮/手指滑动
        xAxisIndex: [0],
        start: 0,
        end: 100,
      },
    ],

    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 30,
        interval: 0, // 显示所有日期（滑动查看）
        fontSize: 11,
      },
    },
    yAxis: {
      type: 'value',
    },
    series: [
      { name: '睡眠', type: 'line', data: sleep, smooth: true },
      { name: '收缩压', type: 'line', data: sbp, smooth: true },
      { name: '血糖', type: 'line', data: sugar, smooth: true },
      { name: 'BMI', type: 'line', data: bmi, smooth: true },
    ],
  }
})

const loadReport = async () => {
  if (!date.value) return
  try {
    const res = await axios.get('/health/report/by-date', {
      params: { type: 2, date: date.value },
    })
    report.value = res.data.data || {}
    chartData.value = parseChartData(report.value.chartData)

    // 解析分析文本，判断指标状态
    const text = report.value.analysisText || ''
    sleepAbnormal.value = text.includes('睡眠') && (text.includes('不足') || text.includes('长期'))
    bpAbnormal.value = text.includes('血压') && (text.includes('偏高') || text.includes('频繁'))
    sugarAbnormal.value = text.includes('血糖') && (text.includes('偏高') || text.includes('多次'))
    bmiAbnormal.value = text.includes('体重') || text.includes('BMI')
  } catch (err) {
    ElMessage.error('加载月报失败')
  }
}

// 生成月报
const generateMonthReport = async () => {
  if (!date.value) {
    ElMessage.warning('请选择日期')
    return
  }
  generating.value = true
  try {
    await axios.get('/health/genMyMonthReport', { params: { date: date.value } })
    ElMessage.success('✅ 月报生成成功！')
    await loadReport()
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
  background: #edf7f0;
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
.card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 800px;
}
.card-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.period {
  color: #666;
  font-size: 14px;
}
.text {
  line-height: 1.7;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  color: #555;
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
