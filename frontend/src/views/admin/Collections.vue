<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">藏品管理</h1>
      <el-button type="primary" @click="openCreate">新增藏品</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="图片" width="90">
        <template #default="{ row }">
          <img :src="row.image" class="h-12 w-12 object-cover rounded" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="era" label="年代" width="120" />
      <el-table-column prop="category" label="类别" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column label="上架" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.published" @change="save(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑藏品' : '新增藏品'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称" required>
          <el-input v-model="form.name" maxlength="100" />
        </el-form-item>
        <el-form-item label="年代" required>
          <el-input v-model="form.era" />
        </el-form-item>
        <el-form-item label="类别" required>
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload :show-file-list="false" :http-request="doUpload" accept="image/jpeg,image/png,image/webp">
            <img v-if="form.image" :src="form.image" class="h-20 rounded border" />
            <el-button v-else>上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.published" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save()">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { adminCollectionApi, adminUploadApi } from '@/api/admin'
import type { CollectionItem } from '@/types'

const list = ref<CollectionItem[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)

const emptyForm = (): CollectionItem => ({
  id: 0,
  name: '',
  era: '',
  category: '',
  description: '',
  image: '',
  sortOrder: 0,
  published: true
})
const form = ref<CollectionItem>(emptyForm())

const load = async () => {
  loading.value = true
  try {
    list.value = await adminCollectionApi.getList()
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.value = emptyForm()
  dialogVisible.value = true
}

const openEdit = (row: CollectionItem) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const doUpload = async (options: UploadRequestOptions) => {
  const res = await adminUploadApi.upload(options.file)
  form.value.image = res.url
}

const save = async (row?: CollectionItem) => {
  const data = row ?? form.value
  if (!data.name || !data.era || !data.category) {
    ElMessage.warning('请填写名称、年代与类别')
    return
  }
  saving.value = true
  try {
    if (data.id) {
      await adminCollectionApi.update(data.id, data)
    } else {
      await adminCollectionApi.create(data)
    }
    dialogVisible.value = false
    ElMessage.success('已保存')
    await load()
  } finally {
    saving.value = false
  }
}

const remove = async (row: CollectionItem) => {
  await ElMessageBox.confirm(`确认删除「${row.name}」？`, '删除确认', { type: 'warning' })
  await adminCollectionApi.delete(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
