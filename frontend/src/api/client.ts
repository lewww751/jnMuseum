import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type { Result } from '@/types'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor for admin authentication
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token && config.url?.startsWith('/admin/')) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    const result: Result = response.data
    if (result.code === 0) {
      return result.data
    } else {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const data = error.response.data

      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('admin_token')
        if (router.currentRoute.value.path.startsWith('/admin')) {
          router.push('/admin/login')
        }
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 400) {
        ElMessage.error(data?.message || '请求参数错误')
      } else {
        ElMessage.error(data?.message || '服务器错误')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络')
    } else {
      ElMessage.error('请求失败')
    }
    return Promise.reject(error)
  }
)

export default apiClient