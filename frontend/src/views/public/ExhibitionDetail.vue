<template>
  <PublicLayout>
    <div v-if="exhibition" class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Back button -->
      <button
        @click="router.back()"
        class="mb-6 text-spring hover:text-springHover transition-colors flex items-center"
      >
        ← 返回展览列表
      </button>

      <!-- Cover image -->
      <img
        :src="exhibition.coverImage"
        :alt="exhibition.title"
        class="w-full h-64 md:h-96 object-cover rounded-lg mb-8"
      />

      <!-- Exhibition info -->
      <h1 class="font-serif text-3xl text-textMain mb-4">{{ exhibition.title }}</h1>

      <div class="flex flex-wrap gap-4 mb-6 text-sm text-textSub">
        <div class="flex items-center space-x-2">
          <span class="px-2 py-1 rounded" :class="getKindClass(exhibition.kind)">
            {{ getKindLabel(exhibition.kind) }}
          </span>
        </div>
        <div class="flex items-center space-x-2">
          <span>📍 {{ exhibition.hall }}</span>
        </div>
        <div v-if="exhibition.startDate && exhibition.endDate" class="flex items-center space-x-2">
          <span>📅 {{ formatDate(exhibition.startDate) }} - {{ formatDate(exhibition.endDate) }}</span>
        </div>
      </div>

      <!-- Content -->
      <div v-if="exhibition.content" class="prose prose-invert max-w-none">
        <div class="text-textMain leading-relaxed whitespace-pre-line">
          {{ exhibition.content }}
        </div>
      </div>

      <!-- CTA -->
      <div class="mt-12 bg-panel border border-line rounded-lg p-6">
        <h3 class="font-medium text-textMain mb-2">参观展览</h3>
        <p class="text-textSub text-sm mb-4">免费参观，需提前预约门票</p>
        <router-link
          to="/reserve"
          class="inline-block px-6 py-2 bg-spring text-ink rounded-lg font-medium hover:bg-springHover transition-colors"
        >
          预约参观
        </router-link>
      </div>
    </div>
    <div v-else class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '展览不存在' }}
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { exhibitionApi } from '@/api/public'
import type { Exhibition, ExhibitionKind } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const router = useRouter()
const route = useRoute()
const exhibition = ref<Exhibition | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const id = parseInt(route.params.id as string)
    exhibition.value = await exhibitionApi.getDetail(id)
  } catch (error) {
    console.error('Failed to load exhibition:', error)
  } finally {
    loading.value = false
  }
})

const getKindLabel = (kind: ExhibitionKind): string => {
  return kind === 'PERMANENT' ? '常设展览' : '临时展览'
}

const getKindClass = (kind: ExhibitionKind): string => {
  return kind === 'PERMANENT'
    ? 'bg-clay text-ink'
    : 'bg-gold text-ink'
}

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}
</script>