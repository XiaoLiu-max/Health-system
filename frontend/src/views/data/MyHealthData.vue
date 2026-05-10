<template>
  <div class="my-health-page">
    <div class="header">
      <h2>📅 我的健康数据</h2>
      <p>选择日期查看、修改或删除记录</p>
    </div>

    <!-- 日期选择器 -->
    <div class="date-box">
      <el-date-picker
        v-model="selectDate"
        type="date"
        placeholder="选择日期"
        value-format="YYYY-MM-DD"
        @change="loadData"
      />
    </div>

    <!-- 表单卡片（标签加宽，单位不换行） -->
    <div class="form-card" v-if="loaded">
      <el-form label-width="160px">
        <el-form-item label="身高(m)">
          <el-input
            v-model.number="form.height"
            type="number"
            step="0.01"
            placeholder="请输入身高"
          />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input
            v-model.number="form.weight"
            type="number"
            step="0.1"
            placeholder="请输入体重"
          />
        </el-form-item>
        <el-form-item label="收缩压(mmHg)">
          <el-input v-model.number="form.sbp" type="number" placeholder="收缩压" />
        </el-form-item>
        <el-form-item label="舒张压(mmHg)">
          <el-input v-model.number="form.dbp" type="number" placeholder="舒张压" />
        </el-form-item>
        <el-form-item label="体温(℃)">
          <el-input v-model.number="form.bodyTemp" type="number" step="0.1" placeholder="体温" />
        </el-form-item>
        <el-form-item label="血糖(mmol/L)">
          <el-input v-model.number="form.bloodSugar" type="number" placeholder="血糖" />
        </el-form-item>
        <el-form-item label="睡眠时长(h)">
          <el-input v-model.number="form.sleepHour" type="number" placeholder="睡眠时长" />
        </el-form-item>
        <el-form-item label="BMI">
          <el-input v-model.number="form.bmi" type="number" disabled placeholder="自动计算" />
        </el-form-item>

        <div class="btn-group">
          <el-button type="primary" @click="saveData" :loading="saving"> ✅ 保存 </el-button>
          <el-button type="danger" @click="deleteData" :loading="deleting">
            🗑️ 删除当天数据
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const selectDate = ref('')
const form = ref({})
const loaded = ref(false)
const saving = ref(false)
const deleting = ref(false)

const loadData = async () => {
  if (!selectDate.value) return
  try {
    const res = await axios.get('/health/data/get', {
      params: { date: selectDate.value },
    })
    form.value = res.data.data || {}
    loaded.value = true
  } catch (err) {
    ElMessage.error('加载失败')
  }
}

const saveData = async () => {
  if (!selectDate.value) return
  saving.value = true
  try {
    delete form.value.id
    // 🔥 带上选中的日期，而不是让后端自己拿今天的日期
    await axios.post('/health/save', {
      ...form.value,
      recordDate: selectDate.value,
    })
    ElMessage.success('✅ 健康数据保存成功')
    await loadData()
  } catch (err) {
    ElMessage.error('❌ 保存失败')
  } finally {
    saving.value = false
  }
}

const deleteData = async () => {
  if (!form.value.id) {
    ElMessage.warning('当天无数据可删除')
    return
  }
  deleting.value = true
  try {
    await axios.post(`/health/delete/${form.value.id}`)
    ElMessage.success('删除成功')
    form.value = {}
  } catch (err) {
    ElMessage.error('删除失败')
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  const today = new Date().toISOString().split('T')[0]
  selectDate.value = today
  loadData()
})
</script>

<style scoped>
.my-health-page {
  padding: 30px 20px;
  background: #edf7f0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.header {
  text-align: center;
  margin-bottom: 25px;
}
.header h2 {
  margin: 0;
  font-size: 30px;
  color: #333;
}
.header p {
  color: #666;
  margin-top: 8px;
  font-size: 18px;
}

.date-box {
  margin-bottom: 25px;
  width: 100%;
  max-width: 700px;
}
.date-box :deep(.el-date-editor) {
  width: 100%;
  height: 45px;
  font-size: 16px;
}

.form-card {
  background: #fff;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  width: 100%;
  max-width: 700px;
}
.form-card :deep(.el-form-item__label) {
  font-size: 16px;
}
.form-card :deep(.el-input__inner) {
  font-size: 16px;
  height: 45px;
}

.btn-group {
  margin-top: 25px;
  display: flex;
  gap: 15px;
  justify-content: center;
}
.btn-group .el-button {
  font-size: 16px;
  padding: 10px 25px;
}
</style>
