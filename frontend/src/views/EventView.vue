<template>
  <div class="events">
    <h1>活动列表</h1>
    <div class="event-grid">
      <div v-for="event in events" :key="event.id" class="event-card">
        <img :src="event.coverImage" :alt="event.title" class="event-image">
        <div class="event-info">
          <h3>{{ event.title }}</h3>
          <p class="category">{{ event.category }}</p>
          <p class="location">{{ event.location }}</p>
          <p class="time">{{ event.startTime }} - {{ event.endTime }}</p>
          <p class="summary">{{ event.summary }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getEvents } from '../api/event';

const events = ref<any[]>([]);

onMounted(async () => {
  try {
    const response = await getEvents();
    events.value = response.data;
  } catch (error) {
    console.error('Failed to fetch events:', error);
  }
});
</script>

<style scoped>
.events {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.event-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.event-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.event-info {
  padding: 1.5rem;
}

.event-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.category, .location, .time {
  margin: 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.summary {
  margin: 1rem 0;
  color: #666;
  line-height: 1.5;
}
</style>