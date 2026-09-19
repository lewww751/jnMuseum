<template>
  <div class="events">
    <h1>活动管理</h1>
    <el-button type="primary" @click="showAddDialog = true">添加活动</el-button>
    
    <el-table :data="events" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="category" label="类型" />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editEvent(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteEvent(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showAddDialog" title="添加活动">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.category">
            <el-option label="特别活动" value="特别活动" />
            <el-option label="讲座" value="讲座" />
            <el-option label="互动体验" value="互动体验" />
            <el-option label="展览" value="展览" />
            <el-option label="科普" value="科普" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.startTime" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="form.endTime" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            action="/api/admin/upload"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            list-type="picture">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介">
          <el-input type="textarea" v-model="form.summary" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getEvents, saveEvent, updateEvent, deleteEvent as deleteEventApi } from '../api/admin';

const events = ref<any[]>([]);
const showAddDialog = ref(false);
const form = ref<any>({});
const editingId = ref<number | null>(null);

onMounted(async () => {
  try {
    const response = await getEvents();
    events.value = response.data;
  } catch (error) {
    console.error('Failed to fetch events:', error);
  }
});

const editEvent = (event: any) => {
  form.value = { ...event };
  editingId.value = event.id;
  showAddDialog.value = true;
};

const deleteEvent = async (id: number) => {
  try {
    await deleteEventApi(id);
    events.value = events.value.filter(item => item.id !== id);
    ElMessage.success('删除成功');
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败');
  }
};

const handleUploadSuccess = (response: any) => {
  form.value.coverImage = response;
};

const beforeUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传 JPG/PNG 格式图片');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB');
    return false;
  }
  return true;
};

const submitForm = async () => {
  try {
    if (editingId.value) {
      await updateEvent(form.value);
      events.value = events.value.map(item => 
        item.id === editingId.value ? form.value : item
      );
    } else {
      await saveEvent(form.value);
      events.value.push(form.value);
    }
    showAddDialog.value = false;
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};
</script>