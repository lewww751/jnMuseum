<template>
  <div class="guide">
    <h1>参观指南</h1>
    <div class="guide-content" v-html="content"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getGuideContent } from '../api/guide';

const content = ref<string>('');

onMounted(async () => {
  try {
    const response = await getGuideContent('visit');
    content.value = response.data.content;
  } catch (error) {
    console.error('Failed to fetch guide content:', error);
    content.value = '<p>无法加载参观指南内容</p>';
  }
});
</script>

<style scoped>
.guide {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.guide-content {
  line-height: 1.6;
  color: #333;
}
</style>