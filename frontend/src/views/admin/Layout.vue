<template>
  <div class="min-h-screen bg-gray-100">
    <el-container>
      <!-- Sidebar -->
      <el-aside width="200px" class="bg-gray-800 text-white">
        <div class="h-16 flex items-center justify-center border-b border-gray-700">
          <span class="font-bold text-lg">管理后台</span>
        </div>
        <el-menu
          :default-active="currentRoute"
          class="el-menu-vertical border-none"
          background-color="#1f2937"
          text-color="#9ca3af"
          active-text-color="#ffffff"
          router
        >
          <el-menu-item index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/exhibitions">
            <el-icon><Picture /></el-icon>
            <span>展览管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/events">
            <el-icon><Calendar /></el-icon>
            <span>活动管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/collections">
            <el-icon><Collection /></el-icon>
            <span>藏品管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/guide">
            <el-icon><Document /></el-icon>
            <span>指南编辑</span>
          </el-menu-item>
          <el-menu-item index="/admin/calendar">
            <el-icon><Calendar /></el-icon>
            <span>容量与闭馆日</span>
          </el-menu-item>
          <el-menu-item index="/admin/bookings">
            <el-icon><Tickets /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- Main content -->
      <el-container>
        <!-- Header -->
        <el-header class="bg-white shadow-sm flex items-center justify-between px-6">
          <div class="text-lg font-medium text-gray-800">济南市博物馆管理后台</div>
          <div class="flex items-center space-x-4">
            <span class="text-gray-600">{{ adminStore.username }}</span>
            <el-button
              type="danger"
              size="small"
              @click="handleLogout"
            >
              退出登录
            </el-button>
          </div>
        </el-header>

        <!-- Page content -->
        <el-main class="p-6">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

const currentRoute = computed(() => route.path)

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    adminStore.logout()
    router.push('/admin/login')
  } catch (error) {
    // User cancelled
  }
}
</script>