<template>
  <PublicLayout>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">参观指南</h1>

      <!-- Notice banner -->
      <div class="bg-clay/20 border border-clay rounded-lg p-6 mb-8">
        <h2 class="font-medium text-clay mb-2">免费不免票 · 实名预约</h2>
        <p class="text-textSub text-sm">
          本馆免费向公众开放，但需要提前预约门票。预约成功后，凭预约码和有效证件入馆。
        </p>
      </div>

      <!-- Guide content -->
      <div v-if="guideContent" class="prose prose-invert max-w-none">
        <div class="text-textMain leading-relaxed whitespace-pre-line">
          {{ guideContent.content }}
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '暂无指南信息' }}
      </div>

      <!-- CTA -->
      <div class="mt-12 text-center">
        <router-link
          to="/reserve"
          class="inline-block px-8 py-3 bg-spring text-ink rounded-lg font-medium hover:bg-springHover transition-colors"
        >
          立即预约
        </router-link>
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
    guideContent.value = await guideApi.getContent('visit')
  } catch (error) {
    console.error('Failed to load guide content:', error)
  } finally {
    loading.value = false
  }
})
</script>