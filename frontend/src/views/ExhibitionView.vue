<template>
  <div class="exhibitions">
    <h1>展览列表</h1>
    <div class="exhibition-grid">
      <div v-for="exhibition in exhibitions" :key="exhibition.id" class="exhibition-card">
        <img :src="exhibition.coverImage" :alt="exhibition.title" class="exhibition-image">
        <div class="exhibition-info">
          <h3>{{ exhibition.title }}</h3>
          <p class="kind">{{ exhibition.kind }}</p>
          <p class="hall">{{ exhibition.hall }}</p>
          <p class="summary">{{ exhibition.summary }}</p>
          <router-link :to="`/exhibitions/${exhibition.id}`" class="btn-details">查看详情</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getExhibitions } from '../api/exhibition';

const exhibitions = ref<any[]>([]);

onMounted(async () => {
  try {
    const response = await getExhibitions();
    exhibitions.value = response.data;
  } catch (error) {
    console.error('Failed to fetch exhibitions:', error);
  }
});
</script>

<style scoped>
.exhibitions {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.exhibition-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.exhibition-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.exhibition-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.exhibition-info {
  padding: 1.5rem;
}

.exhibition-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.kind, .hall {
  margin: 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.summary {
  margin: 1rem 0;
  color: #666;
  line-height: 1.5;
}

.btn-details {
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 0.9rem;
}

.btn-details:hover {
  background-color: #5a67d8;
}
</style>