import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token && config.headers) {
      config.headers.token = token
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器 ✅ 正确写法
service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(res.msg)
    }
    // 直接返回数据，不要转 Promise！
    return res
  },
  (error) => {
    ElMessage.error('网络异常，请检查后端是否启动')
    return Promise.reject(error)
  },
)

export default service
