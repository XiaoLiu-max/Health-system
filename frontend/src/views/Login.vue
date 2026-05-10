<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="header">
        <img src="@/assets/logo.png" alt="系统Logo" class="logo" />
        <h1 class="health-title">健康管理系统</h1>
        <p class="subtitle">守护你的健康，从这里开始</p>
      </div>

      <el-tabs v-model="activeTab">
        <!-- 1. 账号密码登录 -->
        <el-tab-pane label="账号登录" name="account">
          <el-form :model="loginForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="handleLogin"> 登录 </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 2. 手机号登录 -->
        <el-tab-pane label="手机号登录" name="phone">
          <el-form :model="phoneForm" label-width="80px">
            <el-form-item label="手机号">
              <el-input v-model="phoneForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="验证码">
              <div style="display: flex; gap: 10px">
                <el-input v-model="phoneForm.code" placeholder="123456" />
                <el-button @click="sendPhoneCode">{{ codeText }}</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="handlePhoneLogin">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 3. 注册 -->
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" label-width="80px">
            <!-- 🔥 这里我修好了！去掉了导致重复提示的代码 -->
            <el-form-item label="用户名">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                @blur="checkUsernameExist"
              />
              <div v-if="usernameTip" class="tip" :class="usernameValid ? 'success' : 'error'">
                {{ usernameTip }}
              </div>
            </el-form-item>

            <el-form-item label="手机号">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="验证码">
              <div style="display: flex; gap: 10px">
                <el-input v-model="registerForm.code" placeholder="123456" />
                <el-button @click="sendRegisterCode">{{ codeText }}</el-button>
              </div>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="registerForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="registerForm.age" :min="1" :max="120" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="handleRegister">
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 4. 忘记密码 -->
        <el-tab-pane label="忘记密码" name="forget">
          <el-form :model="forgetForm" label-width="80px">
            <el-form-item label="手机号">
              <el-input v-model="forgetForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="验证码">
              <div style="display: flex; gap: 10px">
                <el-input v-model="forgetForm.code" placeholder="123456" />
                <el-button @click="sendForgetCode">{{ codeText }}</el-button>
              </div>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input
                v-model="forgetForm.newPassword"
                type="password"
                placeholder="请输入新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="handleForgetPwd">
                重置密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const activeTab = ref('account')

// 1. 账号密码登录
const loginForm = reactive({ username: '', password: '' })
const handleLogin = async () => {
  try {
    const res = (await request.post('/user/login/password', loginForm)) as any
    if (res.code === 200) {
      localStorage.setItem('token', res.data)
      ElMessage.success('登录成功')
      router.push('/home')
    }
  } catch (e) {
    ElMessage.error('登录失败：用户名或密码错误')
  }
}

// 2. 手机号登录
const phoneForm = reactive({ phone: '', code: '' })
const handlePhoneLogin = async () => {
  try {
    const res = (await request.post('/user/login/phone', phoneForm)) as any
    if (res.code === 200) {
      localStorage.setItem('token', res.data)
      ElMessage.success('登录成功')
      router.push('/home')
    }
  } catch {
    ElMessage.error('登录失败：手机号或验证码错误')
  }
}

// 3. 注册
const registerForm = reactive({
  username: '',
  phone: '',
  code: '',
  gender: 1,
  age: 18,
  password: '',
})

// 实时校验用户名
const usernameTip = ref('')
const usernameValid = ref(false)

// 离开输入框自动检查
const checkUsernameExist = async () => {
  const username = registerForm.username
  if (!username) {
    usernameTip.value = ''
    return
  }

  try {
    const res = await request.get('/user/checkUsername?username=' + username)
    const exist = res.data

    if (exist) {
      usernameTip.value = '❌ 用户名已存在'
      usernameValid.value = false
    } else {
      usernameTip.value = '✅ 用户名可用'
      usernameValid.value = true
    }
  } catch (err) {
    usernameTip.value = '⚠️ 检查失败'
  }
}

// 注册
const handleRegister = async () => {
  await checkUsernameExist()
  if (!usernameValid.value) {
    ElMessage.error('用户名不可用，请更换')
    return
  }

  try {
    const res = (await request.post('/user/register', registerForm)) as any
    if (res.code === 200) {
      ElMessage.success('注册成功！')
      activeTab.value = 'account'
    }
  } catch (err: any) {
    const msg = err.response?.data?.msg || '注册失败'
    ElMessage.error(msg)
  }
}

// 4. 忘记密码
const forgetForm = reactive({ phone: '', code: '', newPassword: '' })
const handleForgetPwd = async () => {
  try {
    const res = (await request.post('/user/forget', forgetForm)) as any
    if (res.code === 200) {
      ElMessage.success('密码重置成功')
      activeTab.value = 'account'
    }
  } catch (err: any) {
    const msg = err.response?.data?.msg || '重置失败'
    ElMessage.error(msg)
  }
}

// 验证码
const codeText = ref('获取验证码')
const sendPhoneCode = () => ElMessage.success('验证码：123456')
const sendRegisterCode = () => ElMessage.success('验证码：123456')
const sendForgetCode = () => ElMessage.success('验证码：123456')
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.login-card {
  width: 500px;
  padding: 36px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 96px;
  height: 96px;
  margin-bottom: 16px;
  filter: drop-shadow(0 2px 8px rgba(0, 128, 0, 0.1));
}

.health-title {
  margin: 0;
  font-size: 26px;
  color: #166534;
  font-weight: 600;
}

.subtitle {
  margin: 8px 0 0;
  color: #65a30d;
  font-size: 14px;
}

.tip {
  font-size: 12px;
  margin-top: 4px;
}
.success {
  color: #00b42a;
}
.error {
  color: #f53f3f;
}
</style>
