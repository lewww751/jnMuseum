<template>
  <div class="bookings">
    <h1>预约管理</h1>
    
    <el-form :model="searchForm" inline>
      <el-form-item label="预约码">
        <el-input v-model="searchForm.code" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="searchForm.phone" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status">
          <el-option label="全部" value="" />
          <el-option label="活跃" value="ACTIVE" />
          <el-option label="已取消" value="CANCELLED" />
          <el-option label="已核销" value="CHECKED_IN" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchBookings">搜索</el-button>
      </el-form-item>
    </el-form>
    
    <el-table :data="bookings" style="width: 100%">
      <el-table-column prop="code" label="预约码" />
      <el-table-column prop="visitDate" label="参观日期" />
      <el-table-column prop="slot" label="时段" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : scope.row.status === 'CANCELLED' ? 'info' : 'warning'">
            {{ scope.row.status === 'ACTIVE' ? '活跃' : scope.row.status === 'CANCELLED' ? '已取消' : '已核销' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入馆人">
        <template #default="scope">
          <div v-for="guest in scope.row.guests" :key="guest.id">
            {{ guest.name }} ({{ guest.idCard }})
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'ACTIVE'" type="primary" @click="checkIn(scope.row.code)">核销</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { getBookings, checkIn } from '../api/admin';

const bookings = ref<any[]>([]);
const searchForm = ref({
  code: '',
  phone: '',
  status: ''
});

const searchBookings = async () => {
  try {
    const params: any = {};
    if (searchForm.value.code) params.code = searchForm.value.code;
    if (searchForm.value.phone) params.phone = searchForm.value.phone;
    if (searchForm.value.status) params.status = searchForm.value.status;
    
    const response = await getBookings(params);
    bookings.value = response.data;
  } catch (error) {
    console.error('Failed to fetch bookings:', error);
  }
};

const checkIn = async (code: string) => {
  try {
    await checkIn(code);
    ElMessage.success('核销成功');
    searchBookings();
  } catch (error: any) {
    ElMessage.error(error.message || '核销失败');
  }
};
</script>