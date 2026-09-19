<template>
  <PublicLayout>
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="font-serif text-3xl text-textMain mb-8">展览</h1>

      <!-- Filter chips -->
      <div class="flex space-x-4 mb-8">
        <button
          @click="selectedKind = null"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedKind === null
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          全部
        </button>
        <button
          @click="selectedKind = 'PERMANENT'"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedKind === 'PERMANENT'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          常设展览
        </button>
        <button
          @click="selectedKind = 'TEMPORARY'"
          :class="[
            'px-4 py-2 rounded-lg transition-colors',
            selectedKind === 'TEMPORARY'
              ? 'bg-spring text-ink'
              : 'bg-panel border border-line text-textSub hover:text-textMain'
          ]"
        >
          临时展览
        </button>
      </div>

      <!-- Exhibition grid -->
      <div v-if="filteredExhibitions.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="exhibition in filteredExhibitions"
          :key="exhibition.id"
          @click="router.push(`/exhibitions/${exhibition.id}`)"
          class="bg-panel border border-line rounded-lg overflow-hidden cursor-pointer hover:border-spring transition-all transform hover:-translate-y-1"
        >
          <img
            :src="exhibition.coverImage"
            :alt="exhibition.title"
            class="w-full h-48 object-cover"
          />
          <div class="p-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs px-2 py-1 rounded" :class="getKindClass(exhibition.kind)">
                {{ getKindLabel(exhibition.kind) }}
              </span>
              <span class="text-xs px-2 py-1 rounded" :class="getStatusClass(exhibition.displayStatus)">
                {{ getStatusLabel(exhibition.displayStatus) }}
              </span>
            </div>
            <h3 class="font-medium text-textMain mb-2">{{ exhibition.title }}</h3>
            <p class="text-textSub text-sm line-clamp-2">{{ exhibition.summary }}</p>
            <p class="text-textSub text-xs mt-2">{{ exhibition.hall }}</p>
          </div>
        </div>
      </div>
      <div v-else class="text-center text-textSub py-12">
        {{ loading ? '加载中...' : '暂无展览' }}
      </div>
    </div>
  </PublicLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { exhibitionApi } from '@/api/public'
import type { Exhibition, ExhibitionKind, DisplayStatus } from '@/types'
import PublicLayout from '@/layouts/PublicLayout.vue'

const router = useRouter()
const exhibitions = ref<Exhibition[]>([])
const selectedKind = ref<ExhibitionKind | null>(null)
const loading = ref(true)

const filteredExhibitions = computed(() => {
  if (selectedKind.value) {
    return exhibitions.value.filter(ex => ex.kind === selectedKind.value)
  }
  return exhibitions.value
})

onMounted(async () => {
  try {
    exhibitions.value = await exhibitionApi.getList()
  } catch (error) {
    console.error('Failed to load exhibitions:', error)
  } finally {
    loading.value = false
  }
})

const getKindLabel = (kind: ExhibitionKind): string => {
  return kind === 'PERMANENT' ? '常设' : '临展'
}

const getKindClass = (kind: ExhibitionKind): string => {
  return kind === 'PERMANENT'
    ? 'bg-clay text-ink'
    : 'bg-gold text-ink'
}

const getStatusLabel = (status: DisplayStatus): string => {
  const labels = {
    ONGOING: '正在展出',
    UPCOMING: '即将开幕',
    ENDED: '已闭展'
  }
  return labels[status]
}

const getStatusClass = (status: DisplayStatus): string => {
  const classes = {
    ONGOING: 'bg-spring text-ink',
    UPCOMING: 'bg-gold text-ink',
    ENDED: 'bg-line text-textSub'
  }
  return classes[status]
}
</script>