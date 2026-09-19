<template>
  <PublicLayout>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">馆藏精选</h1>

      <!-- Collections grid -->
      <div v-if="collections.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
        <div
          v-for="collection in collections"
          :key="collection.id"
          class="bg-panel border border-line rounded-lg overflow-hidden hover:border-spring transition-all transform hover:-translate-y-1"
        >
          <div class="relative">
            <img
              :src="collection.imageUrl"
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
            <h3 class="font-medium text-textMain mb-1">{{ collection.name }}</h3>
            <p class="text-textSub text-sm mb-2">{{ collection.period }}</p>
            <p class="text-textSub text-xs">{{ collection.category }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '暂无藏品' }}
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { collectionApi } from '@/api/public'
import type { CollectionItem } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const collections = ref<CollectionItem[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    collections.value = await collectionApi.getList()
  } catch (error) {
    console.error('Failed to load collections:', error)
  } finally {
    loading.value = false
  }
})
</script>