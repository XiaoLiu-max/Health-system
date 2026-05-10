<!-- <template>
  <div class="weather-card">
    <div class="bg-image"></div>

    <div class="content">
      <div class="left">
        <div class="location">
          <span class="city">{{ weather.city || "加载中..." }}</span>
          <span class="date">{{ currentDate }}</span>
        </div>
        <div class="temp-section">
          <span class="num">{{ weather.temperature }}°</span>
          <span class="desc">{{ weather.weather }}</span>
        </div>
      </div>

      <div class="center">
        <img :src="weatherIcon" alt="天气图标" class="weather-icon" />
      </div>

      <div class="right">
        <div class="info-item">
          <span class="label">风向</span>
          <span class="value">{{ weather.windDirection }}风 {{ weather.windPower }}级</span>
        </div>
        <div class="info-item">
          <span class="label">湿度</span>
          <span class="value">{{ weather.humidity }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '../utils/request'


import sun from '@/assets/weather/sun.png'
import cloudy from '@/assets/weather/partly-cloudy-day.png'
import overcast from '@/assets/weather/cloud.png'
import rain from '@/assets/weather/rain.png'
import snow from '@/assets/weather/snow.png'

const currentDate = ref('')
const weather = ref({
  city: '',
  temperature: '--',
  weather: '加载中',
  windDirection: '',
  windPower: '',
  humidity: ''
})


const weatherIcon = computed(() => {
  const w = weather.value.weather
  if (w.includes('晴')) return sun
  if (w.includes('多云')) return cloudy
  if (w.includes('阴')) return overcast
  if (w.includes('雨')) return rain
  if (w.includes('雪')) return snow
  return overcast
})


const formatDate = () => {
  const now = new Date()
  const weekArr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const week = weekArr[now.getDay()]
  currentDate.value = `${now.getMonth() + 1}月${now.getDate()}日 ${week}`
}


const getWeather = () => {
  if (!navigator.geolocation) {
    alert("您的浏览器不支持定位")
    return
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const longitude = position.coords.longitude
      const latitude = position.coords.latitude

      try {
        const res = (await request.get('/weather/app', {
          params: { longitude, latitude }
        })) as any

        if (res.code === 200) {
          weather.value = res.data
        }
      } catch (e) {
        console.error("天气请求失败", e)
      }
    },
    (err) => {
      console.error("定位失败", err)
    }
  )
}

onMounted(() => {
  formatDate()
  getWeather()
})
</script>

<style scoped>
.weather-card {
  position: relative;
  width: 100%;
  height: 220px;
  border-radius: 20px;
  overflow: hidden;
  color: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}


.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/grain-rain-bg.png') center / cover no-repeat;
  filter: brightness(0.85);
}

.content {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  box-sizing: border-box;
}


.left {
  width: 200px;
  flex-shrink: 0;
}

.location .city {
  display: block;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 4px;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
}

.location .date {
  font-size: 14px;
  opacity: 0.9;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
  margin-bottom: 20px;
}

.temp-section .num {
  font-size: 72px;
  font-weight: 700;
  line-height: 1;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.temp-section .desc {
  display: block;
  font-size: 18px;
  margin-top: 8px;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
}


.center {
  flex-shrink: 0;
}

.center .weather-icon {
  width: 100px;
  height: 100px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}


.right {
  width: 120px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: right;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  font-size: 14px;
  opacity: 0.9;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
}

.value {
  font-size: 16px;
  font-weight: 500;
  text-shadow: 0 1px 4px rgba(0,0,0,0.3);
}
</style> -->

<template>
  <div class="weather-card">
    <!-- 背景图 -->
    <div class="bg-image"></div>

    <!-- 内容层 -->
    <div class="content">
      <!-- 左侧：位置、日期、温度 -->
      <div class="left">
        <div class="location">
          <span class="city">{{ weather.city || '加载中...' }}</span>
          <span class="date">{{ currentDate }}</span>
        </div>
        <div class="temp-section">
          <span class="num">{{ weather.temperature }}°</span>
          <span class="desc">{{ weather.weather }}</span>
        </div>
      </div>

      <!-- 右侧：天气图标 + 风向湿度 -->
      <div class="right">
        <!-- 图标放在这里：城市日期右侧 -->
        <img :src="weatherIcon" alt="天气图标" class="weather-icon" />

        <div class="info-item">
          <span class="label">风向</span>
          <span class="value">{{ weather.windDirection }}风 {{ weather.windPower }}级</span>
        </div>
        <div class="info-item">
          <span class="label">湿度</span>
          <span class="value">{{ weather.humidity }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import request from '../utils/request'

import sun from '@/assets/weather/sun.png'
import cloudy from '@/assets/weather/partly-cloudy-day.png'
import overcast from '@/assets/weather/cloud.png'
import rain from '@/assets/weather/rain.png'
import snow from '@/assets/weather/snow.png'

const currentDate = ref('')
const weather = ref({
  city: '',
  temperature: '--',
  weather: '加载中',
  windDirection: '',
  windPower: '',
  humidity: '',
})

const weatherIcon = computed(() => {
  const w = weather.value.weather
  if (w.includes('晴')) return sun
  if (w.includes('多云')) return cloudy
  if (w.includes('阴')) return overcast
  if (w.includes('雨')) return rain
  if (w.includes('雪')) return snow
  return overcast
})

const formatDate = () => {
  const now = new Date()
  const weekArr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const week = weekArr[now.getDay()]
  currentDate.value = `${now.getMonth() + 1}月${now.getDate()}日 ${week}`
}

const getWeather = () => {
  if (!navigator.geolocation) {
    alert('您的浏览器不支持定位')
    return
  }

  navigator.geolocation.getCurrentPosition(async (position) => {
    const longitude = position.coords.longitude
    const latitude = position.coords.latitude

    try {
      const res = (await request.get('/weather/app', {
        params: { longitude, latitude },
      })) as any
      if (res.code === 200) {
        weather.value = res.data
      }
    } catch (e) {}
  })
}

onMounted(() => {
  formatDate()
  getWeather()
})
</script>

<style scoped>
.weather-card {
  position: relative;
  width: 100%;
  height: 220px;
  border-radius: 20px;
  overflow: hidden;
  color: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('@/assets/grain-rain-bg.png') center / cover no-repeat;
  filter: brightness(0.85);
}

.content {
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  box-sizing: border-box;
}

.left {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.location .city {
  display: block;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 4px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.location .date {
  font-size: 14px;
  opacity: 0.9;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  margin-bottom: 20px;
}

.temp-section .num {
  font-size: 72px;
  font-weight: 700;
  line-height: 1;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.temp-section .desc {
  display: block;
  font-size: 18px;
  margin-top: 8px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

/* ========== 核心：右侧布局（图标 + 信息） ========== */
.right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.weather-icon {
  width: 60px;
  height: 60px;
  margin-bottom: 8px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 14px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}
</style>
