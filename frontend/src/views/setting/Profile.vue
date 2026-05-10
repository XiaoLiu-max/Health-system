<template>
  <div class="profile-page">
    <h2>👤 个人资料</h2>

    <div class="form-wrapper">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model.number="form.age" placeholder="请输入年龄" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="save">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const form = ref({
  username: '',
  phone: '',
  gender: '',
  age: null,
})

const loadInfo = async () => {
  try {
    const res = await axios.get('/user/info')
    const user = res.data.data || {}

    // 后端性别 1=男 2=女
    if (user.gender === 1) {
      user.gender = '男'
    } else if (user.gender === 2) {
      user.gender = '女'
    }

    form.value = {
      username: user.username || '',
      phone: user.phone || '',
      gender: user.gender || '',
      age: user.age || null,
    }
  } catch (err) {
    ElMessage.error('加载信息失败')
  }
}

// 🔥 修复后的保存方法（能正常提交！）
const save = async () => {
  try {
    // 构造后端能识别的数据
    const submitData = {
      phone: form.value.phone,
      age: form.value.age || 0,
      // 关键：性别转回数字
      gender: form.value.gender === '男' ? 1 : 2,
    }

    await axios.post('/user/updateInfo', submitData)
    ElMessage.success('✅ 保存成功！')
    loadInfo() // 重新拉取最新信息
  } catch (err) {
    console.error(err)
    ElMessage.error('❌ 保存失败：' + (err.response?.data?.msg || '服务器异常'))
  }
}

onMounted(() => {
  loadInfo()
})
</script>

<style scoped>
.profile-page {
  padding: 20px 32px;
  background: #edf7f0;
  min-height: calc(100vh - 60px);
}

.form-wrapper {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
</style>
