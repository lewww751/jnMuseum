<template>
  <div class="exhibitions">
    <h1>展览管理</h1>
    <el-button type="primary" @click="showAddDialog = true">添加展览</el-button>
    
    <el-table :data="exhibitions" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="kind" label="类型" />
      <el-table-column prop="hall" label="展厅" />
      <el-table-column prop="startDate" label="开始日期" />
      <el-table-column prop="endDate" label="结束日期" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editExhibition(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteExhibition(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showAddDialog" title="添加展览">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.kind">
            <el-option label="历史" value="历史" />
            <el-option label="艺术" value="艺术" />
            <el-option label="科学" value="科学" />
            <el-option label="民俗" value="民俗" />
            <el-option label="考古" value="考古" />
          </el-select>
        </el-form-item>
        <el-form-item label="展厅">
          <el-input v-model="form.hall" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" />
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
        <el-form-item label="详细内容">
          <el-input type="textarea" v-model="form.content" />
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
import { getExhibitions, saveExhibition, updateExhibition, deleteExhibition as deleteExhibitionApi } from '../api/admin';

const exhibitions = ref<any[]>([]);
const showAddDialog = ref(false);
const form = ref<any>({});
const editingId = ref<number | null>(null);

onMounted(async () => {
  try {
    const response = await getExhibitions();
    exhibitions.value = response.data;
  } catch (error) {
    console.error('Failed to fetch exhibitions:', error);
  }
});

const editExhibition = (exhibition: any) => {
  form.value = { ...exhibition };
  editingId.value = exhibition.id;
  showAddDialog.value = true;
};

const deleteExhibition = async (id: number) => {
  try {
    await deleteExhibitionApi(id);
    exhibitions.value = exhibitions.value.filter(item => item.id !== id);
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
      await updateExhibition(form.value);
      exhibitions.value = exhibitions.value.map(item => 
        item.id === editingId.value ? form.value : item
      );
    } else {
      await saveExhibition(form.value);
      exhibitions.value.push(form.value);
    }
    showAddDialog.value = false;
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};
</script>