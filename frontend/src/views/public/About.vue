<template>
  <PublicLayout>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">关于我们</h1>

      <!-- Guide content -->
      <div v-if="guideContent" class="prose prose-invert max-w-none">
        <div class="text-textMain leading-relaxed whitespace-pre-line">
          {{ guideContent.content }}
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '暂无信息' }}
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { guideApi } from '@/api/public'
import type { GuideContent } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const guideContent = ref<GuideContent | null>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    guideContent.value = await guideApi.getContent('about')
  } catch (error) {
    console.error('Failed to load about content:', error)
  } finally {
    loading.value = false
  }
})
</script>