<template>
  <PublicLayout>
    <!-- Hero Section -->
    <div class="relative h-[600px] overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-b from-transparent via-ink/50 to-ink"></div>
      <img
        src="/images/hero.jpg"
        alt="济南市博物馆"
        class="absolute inset-0 w-full h-full object-cover"
      />
      <div class="absolute inset-0 flex items-center justify-center">
        <div class="text-center px-4">
          <h2 class="font-serif text-4xl sm:text-5xl lg:text-6xl text-textMain mb-4">
            汉画像石上的齐鲁生活
          </h2>
          <p class="text-textSub text-lg mb-8">探索齐鲁文明的历史印记</p>
          <router-link
            to="/reserve"
            class="inline-block px-8 py-3 bg-spring text-ink rounded-lg font-medium hover:bg-springHover transition-all transform hover:scale-105"
          >
            立即预约参观
          </router-link>
        </div>
      </div>
    </div>

    <!-- Current Exhibitions -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="flex items-center justify-between mb-8">
        <h3 class="font-serif text-2xl text-textMain">正在展出</h3>
        <router-link to="/exhibitions" class="text-spring hover:text-springHover transition-colors">
          查看更多 →
        </router-link>
      </div>
      <div v-if="exhibitions.length > 0" class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div
          v-for="exhibition in exhibitions.slice(0, 3)"
          :key="exhibition.id"
          class="bg-panel border border-line rounded-lg overflow-hidden hover:border-spring transition-all transform hover:-translate-y-1"
        >
          <img
            :src="exhibition.coverImage"
            :alt="exhibition.title"
            class="w-full h-48 object-cover"
          />
          <div class="p-4">
            <h4 class="font-medium text-textMain mb-2">{{ exhibition.title }}</h4>
            <p class="text-textSub text-sm line-clamp-2">{{ exhibition.summary }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">加载中...</div>
    </section>

    <!-- Recent Events -->
    <section class="bg-panel">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div class="flex items-center justify-between mb-8">
          <h3 class="font-serif text-2xl text-textMain">近期活动</h3>
          <router-link to="/events" class="text-spring hover:text-springHover transition-colors">
            查看更多 →
          </router-link>
        </div>
        <div v-if="events.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div
            v-for="event in events.slice(0, 4)"
            :key="event.id"
            class="bg-panel2 border border-line rounded-lg p-4 hover:border-spring transition-all"
          >
            <span class="inline-block px-2 py-1 text-xs rounded-full mb-3" :class="getEventStatusClass(event.eventStatus)">
              {{ getEventStatusLabel(event.eventStatus) }}
            </span>
            <h4 class="font-medium text-textMain mb-2">{{ event.title }}</h4>
            <p class="text-textSub text-sm line-clamp-2">{{ event.summary }}</p>
            <p class="text-textSub text-xs mt-2">{{ formatDate(event.startTime) }}</p>
          </div>
        </div>
        <div v-else class="text-center text-textSub py-12">加载中...</div>
      </div>
    </section>

    <!-- Featured Collections -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div class="flex items-center justify-between mb-8">
        <h3 class="font-serif text-2xl text-textMain">馆藏精选</h3>
        <router-link to="/collections" class="text-spring hover:text-springHover transition-colors">
          查看更多 →
        </router-link>
      </div>
      <div v-if="collections.length > 0" class="flex space-x-6 overflow-x-auto pb-4 scrollbar-thin">
        <div
          v-for="collection in collections.slice(0, 6)"
          :key="collection.id"
          class="flex-shrink-0 w-64 bg-panel border border-line rounded-lg overflow-hidden hover:border-spring transition-all"
        >
          <div class="relative">
            <img
              :src="collection.image"
              :alt="collection.name"
              class="w-full h-48 object-cover"
            />
            <span
              v-if="collection.isHighlight"
              class="absolute top-2 right-2 px-2 py-1 bg-gold text-ink text-xs rounded-full font-medium"
            >
              镇馆之宝
            </span>
          </div>
          <div class="p-4">
            <h4 class="font-medium text-textMain mb-1">{{ collection.name }}</h4>
            <p class="text-textSub text-sm">{{ collection.era }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">加载中...</div>
    </section>

    <!-- Opening Hours -->
    <section class="bg-panel2 border-t border-line">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div class="text-center mb-8">
          <h3 class="font-serif text-2xl text-textMain mb-2">开放时间</h3>
          <p class="text-textSub">免费不免票 · 实名预约</p>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 text-center">
          <div class="bg-panel border border-line rounded-lg p-6">
            <p class="text-textMain font-medium mb-2">周二至周日</p>
            <p class="text-textSub">9:00 - 17:00</p>
            <p class="text-textSub/70 text-sm mt-1">（16:00停止入馆）</p>
          </div>
          <div class="bg-panel border border-line rounded-lg p-6">
            <p class="text-textMain font-medium mb-2">周一</p>
            <p class="text-textSub">闭馆</p>
            <p class="text-textSub/70 text-sm mt-1">（法定节假日除外）</p>
          </div>
          <div class="bg-panel border border-line rounded-lg p-6">
            <p class="text-textMain font-medium mb-2">预约方式</p>
            <p class="text-textSub">网上实名预约</p>
            <router-link to="/reserve" class="text-spring hover:text-springHover text-sm">
              立即预约 →
            </router-link>
          </div>
        </div>
      </div>
    </section>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { exhibitionApi, eventApi, collectionApi } from '@/api/public'
import type { Exhibition, MuseumEvent, CollectionItem, EventStatus } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const exhibitions = ref<Exhibition[]>([])
const events = ref<MuseumEvent[]>([])
const collections = ref<CollectionItem[]>([])

onMounted(async () => {
  try {
    exhibitions.value = await exhibitionApi.getList()
    events.value = await eventApi.getList()
    collections.value = await collectionApi.getList()
  } catch (error) {
    console.error('Failed to load homepage data:', error)
  }
})

const getEventStatusLabel = (status: EventStatus): string => {
  const labels = {
    UPCOMING: '即将开始',
    ONGOING: '进行中',
    ENDED: '已结束'
  }
  return labels[status]
}

const getEventStatusClass = (status: EventStatus): string => {
  const classes = {
    UPCOMING: 'bg-gold text-ink',
    ONGOING: 'bg-spring text-ink',
    ENDED: 'bg-line text-textSub'
  }
  return classes[status]
}

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>