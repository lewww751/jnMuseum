<template>
  <PublicLayout>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">活动</h1>

      <!-- Filter chips -->
      <div class="flex space-x-4 mb-8">
        <button
          @click="selectedStatus = null"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedStatus === null
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          全部
        </button>
        <button
          @click="selectedStatus = 'UPCOMING'"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedStatus === 'UPCOMING'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          即将开始
        </button>
        <button
          @click="selectedStatus = 'ONGOING'"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedStatus === 'ONGOING'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          进行中
        </button>
        <button
          @click="selectedStatus = 'ENDED'"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedStatus === 'ENDED'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          已结束
        </button>
      </div>

      <!-- Events grid -->
      <div v-if="filteredEvents.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="event in filteredEvents"
          :key="event.id"
          class="bg-panel border border-line rounded-lg p-6 hover:border-spring transition-all"
        >
          <div class="flex items-center justify-between mb-4">
            <span class="text-xs px-2 py-1 rounded-full" :class="getStatusClass(event.eventStatus)">
              {{ getStatusLabel(event.eventStatus) }}
            </span>
            <span class="text-xs text-textSub">{{ event.category }}</span>
          </div>
          <h3 class="font-medium text-textMain mb-2">{{ event.title }}</h3>
          <p class="text-textSub text-sm line-clamp-2 mb-4">{{ event.description }}</p>
          <div class="text-xs text-textSub">
            <p>📅 {{ formatDateTime(event.startTime) }}</p>
            <p v-if="event.endTime" class="mt-1">🕐 {{ formatEndTime(event.endTime) }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '暂无活动' }}
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { eventApi } from '@/api/public'
import type { MuseumEvent, EventStatus } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const events = ref<MuseumEvent[]>([])
const selectedStatus = ref<EventStatus | null>(null)
const loading = ref(true)

const filteredEvents = computed(() => {
  if (selectedStatus.value) {
    return events.value.filter(event => event.eventStatus === selectedStatus.value)
  }
  return events.value
})

onMounted(async () => {
  try {
    events.value = await eventApi.getList()
  } catch (error) {
    console.error('Failed to load events:', error)
  } finally {
    loading.value = false
  }
})

const getStatusLabel = (status: EventStatus): string => {
  const labels = {
    UPCOMING: '即将开始',
    ONGOING: '进行中',
    ENDED: '已结束'
  }
  return labels[status]
}

const getStatusClass = (status: EventStatus): string => {
  const classes = {
    UPCOMING: 'bg-gold text-ink',
    ONGOING: 'bg-spring text-ink',
    ENDED: 'bg-line text-textSub'
  }
  return classes[status]
}

const formatDateTime = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatEndTime = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}
</script>