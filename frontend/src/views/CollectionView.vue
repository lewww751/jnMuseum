<template>
  <div class="collections">
    <h1>藏品展示</h1>
    <div class="collection-grid">
      <div v-for="item in collections" :key="item.id" class="collection-card">
        <img :src="item.image" :alt="item.name" class="collection-image">
        <div class="collection-info">
          <h3>{{ item.name }}</h3>
          <p class="era">{{ item.era }}</p>
          <p class="category">{{ item.category }}</p>
          <p class="description">{{ item.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCollectionItems } from '../api/collection';

const collections = ref<any[]>([]);

onMounted(async () => {
  try {
    const response = await getCollectionItems();
    collections.value = response.data;
  } catch (error) {
    console.error('Failed to fetch collections:', error);
  }
});
</script>

<style scoped>
.collections {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.collection-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.collection-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.collection-info {
  padding: 1.5rem;
}

.collection-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.era, .category {
  margin: 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.description {
  margin: 1rem 0;
  color: #666;
  line-height: 1.5;
}
</style>