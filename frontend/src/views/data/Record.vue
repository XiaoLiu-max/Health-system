<template>
  <div class="data-record-page">
    <div class="page-header">
      <h3>📊 今日健康数据录入</h3>
    </div>

    <div class="form-wrapper">
      <el-form :model="form" label-width="120px" class="data-form">
        <el-form-item label="身高(m)">
          <el-input v-model.number="form.height" placeholder="请输入身高" />
        </el-form-item>

        <el-form-item label="体重(kg)">
          <el-input v-model.number="form.weight" placeholder="请输入体重" />
        </el-form-item>

        <!-- 🔥 全部改成后端真实字段名 -->
        <el-form-item label="收缩压(mmHg)">
          <el-input v-model.number="form.sbp" placeholder="收缩压" />
        </el-form-item>

        <el-form-item label="舒张压(mmHg)">
          <el-input v-model.number="form.dbp" placeholder="舒张压" />
        </el-form-item>

        <el-form-item label="体温(℃)">
          <el-input v-model.number="form.bodyTemp" placeholder="体温" />
        </el-form-item>

        <el-form-item label="血糖(mmol/L)">
          <el-input v-model.number="form.bloodSugar" placeholder="血糖" />
        </el-form-item>

        <el-form-item label="睡眠时长(h)">
          <el-input v-model.number="form.sleepHour" placeholder="睡眠时长" />
        </el-form-item>

        <el-form-item>
          <div class="btn-group">
            <el-button @click="loadYesterdayData" type="info"> 🔁 一键复用昨日数据 </el-button>
            <el-button type="primary" @click="submitData" :loading="loading">
              ✅ 保存今日数据
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const form = reactive({
  height: null,
  weight: null,
  sbp: null,
  dbp: null,
  bodyTemp: null,
  bloodSugar: null,
  sleepHour: null,
})

// 提交保存（关键修复：保存前清空id）
const submitData = async () => {
  loading.value = true
  try {
    // 🔥 关键：保存前强制清空id，让后端知道这是新增数据
    delete form.id
    await axios.post('/health/save', form)
    ElMessage.success('✅ 今日健康数据保存成功')
  } catch (err) {
    ElMessage.error('❌ 保存失败')
  } finally {
    loading.value = false
  }
}

// 加载昨日数据（关键修复：只复制数据，不复制id）
const loadYesterdayData = async () => {
  try {
    const res = await axios.get('/health/yesterday')
    const data = res.data.data || {}
    const hasData = Object.values(data).some((item) => item != null)
    if (hasData) {
      // 🔥 关键：手动复制字段，不复制id
      form.height = data.height
      form.weight = data.weight
      form.sbp = data.sbp
      form.dbp = data.dbp
      form.bodyTemp = data.bodyTemp
      form.bloodSugar = data.bloodSugar
      form.sleepHour = data.sleepHour
      ElMessage.success('✅ 已加载昨日数据')
    } else {
      ElMessage.info('ℹ️ 暂无昨日数据')
    }
  } catch (err) {
    ElMessage.info('ℹ️ 暂无昨日数据')
  }
}
</script>

<style scoped>
.data-record-page {
  padding: 20px;
  background-color: #edf7f0;
  min-height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.page-header {
  margin-bottom: 20px;
  width: 100%;
  max-width: 800px;
}

.page-header h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.form-wrapper {
  width: 100%;
  max-width: 800px;
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.data-form {
  width: 100%;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}
</style>
