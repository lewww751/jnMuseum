<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">容量与闭馆日</h1>
      <div class="flex items-center gap-2">
        <el-button @click="prevMonth">上一月</el-button>
        <span class="font-semibold">{{ month }}</span>
        <el-button @click="nextMonth">下一月</el-button>
      </div>
    </div>

    <div class="grid grid-cols-7 gap-2" v-loading="loading">
      <div
        v-for="day in days"
        :key="day.date"
        class="border rounded p-2 cursor-pointer hover:shadow-md min-h-24"
        :class="day.isOpen ? 'bg-white' : 'bg-gray-100'"
        @click="openDrawer(day)"
      >
        <div class="flex justify-between items-center mb-1">
          <span class="font-semibold text-sm">{{ day.date.slice(8) }}</span>
          <el-tag size="small" :type="day.isOpen ? 'success' : 'info'">
            {{ day.isOpen ? '开放' : '闭馆' }}
          </el-tag>
        </div>
        <div class="text-xs text-gray-500">
          <div v-if="!day.isOpen && day.override?.reason">{{ day.override.reason }}</div>
          <div>上午 {{ day.slots.AM.booked }}/{{ day.slots.AM.capacity }}</div>
          <div>下午 {{ day.slots.PM.booked }}/{{ day.slots.PM.capacity }}</div>
        </div>
      </div>
    </div>

    <el-drawer v-model="drawerVisible" :title="`设置 ${current?.date ?? ''}`" size="380px">
      <el-form label-width="90px">
        <el-form-item label="开放状态">
          <el-switch v-model="form.isOpen" active-text="开放" inactive-text="闭馆" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="form.reason" placeholder="如：节假日特别开放 / 临时布展" />
        </el-form-item>
        <el-form-item label="上午容量">
          <el-input-number v-model="form.amCapacity" :min="1" :max="5000" />
        </el-form-item>
        <el-form-item label="下午容量">
          <el-input-number v-model="form.pmCapacity" :min="1" :max="5000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="removeOverride">恢复默认</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminCalendarApi } from '@/api/admin'
import type { CalendarDay } from '@/types'

const month = ref(new Date().toISOString().slice(0, 7))
const days = ref<CalendarDay[]>([])
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)

const current = ref<CalendarDay | null>(null)
const form = ref({ isOpen: true, reason: '', amCapacity: 300, pmCapacity: 300 })

const load = async () => {
  loading.value = true
  try {
    days.value = await adminCalendarApi.getMonth(month.value)
  } finally {
    loading.value = false
  }
}

const prevMonth = () => {
  const [y, m] = month.value.split('-').map(Number)
  const d = new Date(y, m - 2, 1)
  month.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  load()
}

const nextMonth = () => {
  const [y, m] = month.value.split('-').map(Number)
  const d = new Date(y, m, 1)
  month.value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
  load()
}

const openDrawer = (day: CalendarDay) => {
  current.value = day
  form.value = {
    isOpen: day.isOpen,
    reason: day.override?.reason ?? '',
    amCapacity: day.slots.AM.capacity,
    pmCapacity: day.slots.PM.capacity
  }
  drawerVisible.value = true
}

const save = async () => {
  if (!current.value) return
  saving.value = true
  try {
    await adminCalendarApi.setDaySetting({
      date: current.value.date,
      isOpen: form.value.isOpen,
      reason: form.value.reason || undefined
    })
    await adminCalendarApi.setSlotCapacity({ date: current.value.date, slot: 'AM', capacity: form.value.amCapacity })
    await adminCalendarApi.setSlotCapacity({ date: current.value.date, slot: 'PM', capacity: form.value.pmCapacity })
    ElMessage.success('已保存')
    drawerVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

const removeOverride = async () => {
  if (!current.value) return
  await adminCalendarApi.removeDaySetting(current.value.date)
  ElMessage.success('已恢复默认开放规则')
  drawerVisible.value = false
  await load()
}

onMounted(load)
</script>
