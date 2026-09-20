<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">活动管理</h1>
      <el-button type="primary" @click="openCreate">新增活动</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="category" label="类别" width="100" />
      <el-table-column prop="location" label="地点" width="120" />
      <el-table-column prop="startTime" label="开始时间" width="170" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑活动' : '新增活动'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" maxlength="100" />
        </el-form-item>
        <el-form-item label="类别" required>
          <el-select v-model="form.category">
            <el-option v-for="c in CATEGORIES" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="开始时间" required>
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload :show-file-list="false" :http-request="doUpload" accept="image/jpeg,image/png,image/webp">
            <img v-if="form.coverImage" :src="form.coverImage" class="h-20 rounded border" />
            <el-button v-else>上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
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
import { adminEventApi, adminUploadApi } from '@/api/admin'
import type { MuseumEvent } from '@/types'

const CATEGORIES = ['讲座', '工作坊', '亲子活动', '其他']

const list = ref<MuseumEvent[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)

const emptyForm = (): MuseumEvent => ({
  id: 0,
  title: '',
  category: '讲座',
  location: '',
  summary: '',
  coverImage: '',
  startTime: '',
  endTime: undefined,
  eventStatus: 'UPCOMING',
  published: true
})
const form = ref<MuseumEvent>(emptyForm())

const load = async () => {
  loading.value = true
  try {
    list.value = await adminEventApi.getList()
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.value = emptyForm()
  dialogVisible.value = true
}

const openEdit = (row: MuseumEvent) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const doUpload = async (options: UploadRequestOptions) => {
  const res = await adminUploadApi.upload(options.file)
  form.value.coverImage = res.url
}

const save = async (row?: MuseumEvent) => {
  const data = row ?? form.value
  if (!data.title || !data.startTime) {
    ElMessage.warning('请填写标题与开始时间')
    return
  }
  saving.value = true
  try {
    if (data.id) {
      await adminEventApi.update(data.id, data)
    } else {
      await adminEventApi.create(data)
    }
    dialogVisible.value = false
    ElMessage.success('已保存')
    await load()
  } finally {
    saving.value = false
  }
}

const remove = async (row: MuseumEvent) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」？`, '删除确认', { type: 'warning' })
  await adminEventApi.delete(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
