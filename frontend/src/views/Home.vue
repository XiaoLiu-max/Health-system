<!-- <template>
  <div class="home-page">
    <div class="weather-container">
      <WeatherCard />
    </div>

    <div class="cards-container">
      <div class="card-item" @click="$router.push('/data/record')">
        <div class="icon">📝</div>
        <div class="title">数据录入</div>
      </div>

      <div class="card-item" @click="$router.push('/data/analysis')">
        <div class="icon">📊</div>
        <div class="title">数据统计</div>
      </div>

      <div class="card-item" @click="$router.push('/data/week')">
        <div class="icon">📈</div>
        <div class="title">健康周报</div>
      </div>

      
      <div class="card-item" @click="$router.push('/data/month')">
        <div class="icon">📊</div>
        <div class="title">健康月报</div>
      </div>
      
      <div class="card-item" @click="$router.push('/data/my')">
        <div class="icon">📅</div>
        <div class="title">我的健康数据</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import WeatherCard from '@/components/WeatherCard.vue'
</script>

<style scoped>
.home-page {
  padding: 20px;
  min-height: 100vh;
  background: #edf7f0;
}

.weather-container {
  margin-bottom: 20px;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.card-item {
  background: white;
  height: 160px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}
</style> -->

<template>
  <div class="weather-container">
    <WeatherCard />
  </div>
  <div class="home-page">
    <div class="cards-container">
      <div class="card-item" @click="$router.push('/data/record')">
        <div class="icon">📝</div>
        <div class="title">数据录入</div>
      </div>

      <!-- 数据统计 → 替换成 健康提醒 -->
      <div class="card-item" @click="handleOpenRemind">
        <div class="icon">⏰</div>
        <div class="title">健康提醒</div>
      </div>

      <div class="card-item" @click="$router.push('/data/week')">
        <div class="icon">📈</div>
        <div class="title">健康周报</div>
      </div>

      <div class="card-item" @click="$router.push('/data/month')">
        <div class="icon">📊</div>
        <div class="title">健康月报</div>
      </div>

      <div class="card-item" @click="$router.push('/data/my')">
        <div class="icon">📅</div>
        <div class="title">我的健康数据</div>
      </div>
    </div>

    <!-- 引入独立组件 -->
    <RemindCard ref="remindCardRef" />
  </div>
</template>

<script setup lang="ts">
import WeatherCard from '@/components/WeatherCard.vue'
import RemindCard from '@/components/RemindCard.vue'
import { ref } from 'vue'

const remindCardRef = ref<InstanceType<typeof RemindCard> | null>(null)

const handleOpenRemind = () => {
  remindCardRef.value?.openRemindDialog()
}
</script>

<style scoped>
.home-page {
  padding: 20px;
  min-height: 100vh;
  background: #edf7f0;
}

.weather-container {
  margin-bottom: 20px;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.card-item {
  background: white;
  height: 160px;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}
</style>
