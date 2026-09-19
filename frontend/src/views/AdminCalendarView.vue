<template>
  <div class="calendar">
    <h1>日历管理</h1>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="开放日设置" name="open">
        <el-form :model="form" label-width="100px">
          <el-form-item label="日期">
            <el-date-picker v-model="form.date" type="date" />
          </el-form-item>
          <el-form-item label="是否开放">
            <el-switch v-model="form.isOpen" />
          </el-form-item>
          <el-form-item label="原因">
            <el-input v-model="form.reason" />
          </el-form-item>
          <el-button type="primary" @click="saveDaySetting">保存</el-button>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="时段容量" name="capacity">
        <el-form :model="form" label-width="100px">
          <el-form-item label="日期">
            <el-date-picker v-model="form.date" type="date" />
          </el-form-item>
          <el-form-item label="时段">
            <el-select v-model="form.slot">
              <el-option label="上午 (AM)" value="AM" />
              <el-option label="下午 (PM)" value="PM" />
            </el-select>
          </el-form-item>
          <el-form-item label="容量">
            <el-input-number v-model="form.capacity" :min="1" :max="500" />
          </el-form-item>
          <el-button type="primary" @click="saveSlotCapacity">保存</el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getCalendar, updateDaySetting, updateSlotCapacity } from '../api/admin';

const activeTab = ref('open');
const form = ref<any>({});

onMounted(async () => {
  try {
    const response = await getCalendar('2026-09');
    console.log('Calendar data:', response.data);
  } catch (error) {
    console.error('Failed to fetch calendar data:', error);
  }
});

const saveDaySetting = async () => {
  try {
    await updateDaySetting(form.value);
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};

const saveSlotCapacity = async () => {
  try {
    await updateSlotCapacity(form.value);
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};
</script>