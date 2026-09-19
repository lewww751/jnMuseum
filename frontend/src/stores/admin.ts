import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { adminAuthApi } from '@/api/admin'

export const useAdminStore = defineStore('admin', () => {
  const token = ref<string | null>(localStorage.getItem('admin_token'))
  const username = ref<string | null>(localStorage.getItem('admin_username'))

  const isAuthenticated = computed(() => !!token.value)

  const login = async (username: string, password: string) => {
    const response = await adminAuthApi.login({ username, password })
    token.value = response.token
    localStorage.setItem('admin_token', response.token)
    localStorage.setItem('admin_username', username)
    return response
  }

  const logout = () => {
    token.value = null
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
  }

  return {
    token,
    username,
    isAuthenticated,
    login,
    logout
  }
})