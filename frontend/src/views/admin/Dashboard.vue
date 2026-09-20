<template>
  <div class="p-6">
    <h1 class="text-xl font-bold mb-6">数据看板</h1>

    <el-row :gutter="16" class="mb-6">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover">
          <div class="text-gray-500 text-sm">{{ card.label }}</div>
          <div class="text-3xl font-bold mt-2">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="10">
        <el-card shadow="never" header="今日 / 明日时段容量">
          <el-table :data="slotRows" size="small">
            <el-table-column prop="day" label="日期" width="70" />
            <el-table-column prop="label" label="时段" />
            <el-table-column prop="booked" label="已约" width="70" />
            <el-table-column prop="capacity" label="容量" width="70" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card shadow="never" header="近 7 天预约趋势（入馆人数）">
          <div ref="chartRef" class="h-72"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import * as echarts from 'echarts'
import { adminDashboardApi } from '@/api/admin'
import type { DashboardStats } from '@/types'

const stats = ref<DashboardStats | null>(null)
const chartRef = ref<HTMLDivElement>()

const statCards = computed(() => [
  { label: '有效预约单', value: stats.value?.stats.activeBookings ?? '-' },
  { label: '今日已核销', value: stats.value?.stats.todayCheckIns ?? '-' },
  { label: '展览数', value: stats.value?.stats.exhibitionCount ?? '-' },
  { label: '活动数', value: stats.value?.stats.eventCount ?? '-' }
])

const slotRows = computed(() => {
  if (!stats.value) return []
  return [
    ...stats.value.today.map((s) => ({ day: '今天', ...s })),
    ...stats.value.tomorrow.map((s) => ({ day: '明天', ...s }))
  ]
})

onMounted(async () => {
  const data = await adminDashboardApi.getStats()
  stats.value = data
  const chart = echarts.init(chartRef.value!)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 16, top: 24, bottom: 28 },
    xAxis: { type: 'category', data: data.last7Days.map((d) => d.date.slice(5)) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: data.last7Days.map((d) => d.booked), barMaxWidth: 36 }]
  })
})
</script>
