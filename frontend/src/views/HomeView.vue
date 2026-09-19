<template>
  <div class="home">
    <div class="hero">
      <h1>欢迎来到济南市博物馆</h1>
      <p>探索济南的历史文化，感受城市的魅力</p>
      <router-link to="/booking" class="btn-primary">立即预约</router-link>
    </div>
    
    <div class="features">
      <div class="feature">
        <h3>精彩展览</h3>
        <p>丰富的展览内容，满足您的文化需求</p>
      </div>
      <div class="feature">
        <h3>文化活动</h3>
        <p>定期举办各类文化活动，丰富您的文化生活</p>
      </div>
      <div class="feature">
        <h3>预约参观</h3>
        <p>便捷的预约系统，轻松安排您的参观时间</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { getExhibitions } from '../api/exhibition';

onMounted(async () => {
  try {
    const response = await getExhibitions();
    console.log('Exhibitions:', response.data);
  } catch (error) {
    console.error('Failed to fetch exhibitions:', error);
  }
});
</script>

<style scoped>
.home {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.hero {
  text-align: center;
  padding: 4rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.btn-primary {
  display: inline-block;
  padding: 0.8rem 2rem;
  background-color: #fff;
  color: #667eea;
  text-decoration: none;
  border-radius: 4px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #f0f0f0;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature {
  text-align: center;
  padding: 2rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.feature h3 {
  color: #667eea;
  margin-bottom: 1rem;
}
</style>