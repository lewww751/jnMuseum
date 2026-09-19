<template>
  <div class="collections">
    <h1>藏品管理</h1>
    <el-button type="primary" @click="showAddDialog = true">添加藏品</el-button>
    
    <el-table :data="collections" style="width: 100%">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="era" label="年代" />
      <el-table-column prop="category" label="类别" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button @click="editCollection(scope.row)">编辑</el-button>
          <el-button type="danger" @click="deleteCollection(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showAddDialog" title="添加藏品">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="年代">
          <el-input v-model="form.era" />
        </el-form-item>
        <el-form-item label="类别">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="/api/admin/upload"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            list-type="picture">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
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
import { getCollections, saveCollection, updateCollection, deleteCollection as deleteCollectionApi } from '../api/admin';

const collections = ref<any[]>([]);
const showAddDialog = ref(false);
const form = ref<any>({});
const editingId = ref<number | null>(null);

onMounted(async () => {
  try {
    const response = await getCollections();
    collections.value = response.data;
  } catch (error) {
    console.error('Failed to fetch collections:', error);
  }
});

const editCollection = (collection: any) => {
  form.value = { ...collection };
  editingId.value = collection.id;
  showAddDialog.value = true;
};

const deleteCollection = async (id: number) => {
  try {
    await deleteCollectionApi(id);
    collections.value = collections.value.filter(item => item.id !== id);
    ElMessage.success('删除成功');
  } catch (error: any) {
    ElMessage.error(error.message || '删除失败');
  }
};

const handleUploadSuccess = (response: any) => {
  form.value.image = response;
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
      await updateCollection(form.value);
      collections.value = collections.value.map(item => 
        item.id === editingId.value ? form.value : item
      );
    } else {
      await saveCollection(form.value);
      collections.value.push(form.value);
    }
    showAddDialog.value = false;
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};
</script>