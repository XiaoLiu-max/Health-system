// import { createApp } from 'vue'
// import { createPinia } from 'pinia'

// import App from './App.vue'
// import router from './router'

// const app = createApp(App)

// app.use(createPinia())
// app.use(router)

// app.mount('#app')

// src/main.ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import axios from 'axios'

const app = createApp(App)

axios.defaults.baseURL = 'http://localhost:8080'

// ===================== 我只加了这一段 =====================
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['token'] = token
  }
  return config
})
// =========================================================

app.config.globalProperties.$axios = axios

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
