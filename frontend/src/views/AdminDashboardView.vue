<template>
  <div class="dashboard">
    <h1>管理员 dashboard</h1>
    <div class="stats">
      <div class="stat-card">
        <h3>今日预约</h3>
        <p>{{ stats.today.AM + stats.today.PM }}</p>
      </div>
      <div class="stat-card">
        <h3>明日预约</h3>
        <p>{{ stats.tomorrow.AM + stats.tomorrow.PM }}</p>
      </div>
      <div class="stat-card">
        <h3>活跃预约</h3>
        <p>{{ stats.stats.activeBookings }}</p>
      </div>
      <div class="stat-card">
        <h3>今日核销</h3>
        <p>{{ stats.stats.todayCheckIns }}</p>
      </div>
    </div>
    
    <div class="chart">
      <h3>预约趋势</h3>
      <div ref="chartRef" style="width: 100%; height: 300px;"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getDashboard } from '../api/admin';
import * as echarts from 'echarts';

const chartRef = ref();
const stats = ref<any>(null);

onMounted(async () => {
  try {
    const response = await getDashboard();
    stats.value = response.data;
    
    // 初始化图表
    const chart = echarts.init(chartRef.value);
    const option = {
      xAxis: {
        type: 'category',
        data: stats.value.last7Days.map(item => item.date)
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: stats.value.last7Days.map(item => item.count),
        type: 'line'
      }]
    };
    chart.setOption(option);
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error);
  }
});
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.stat-card h3 {
  margin: 0 0 0.5rem 0;
  color: #666;
}

.stat-card p {
  font-size: 1.5rem;
  font-weight: bold;
  color: #667eea;
}

.chart {
  margin-top: 2rem;
}
</style>