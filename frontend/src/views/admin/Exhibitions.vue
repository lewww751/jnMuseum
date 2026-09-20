<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-xl font-bold">展览管理</h1>
      <el-button type="primary" @click="openCreate">新增展览</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.kind === 'PERMANENT' ? 'success' : 'warning'">
            {{ row.kind === 'PERMANENT' ? '常设' : '临展' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hall" label="展厅" width="120" />
      <el-table-column label="展期" width="200">
        <template #default="{ row }">{{ row.startDate || '—' }} ~ {{ row.endDate || '—' }}</template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑展览' : '新增展览'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" maxlength="100" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-radio-group v-model="form.kind">
            <el-radio value="PERMANENT">常设</el-radio>
            <el-radio value="TEMPORARY">临展</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="展厅">
          <el-input v-model="form.hall" />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload :show-file-list="false" :http-request="doUpload" accept="image/jpeg,image/png,image/webp">
            <img v-if="form.coverImage" :src="form.coverImage" class="h-20 rounded border" />
            <el-button v-else>上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="form.kind === 'TEMPORARY'" label="展期" required>
          <el-date-picker
            v-model="range"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始"
            end-placeholder="结束"
          />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="form.content" type="textarea" :rows="5" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { adminExhibitionApi, adminUploadApi } from '@/api/admin'
import type { Exhibition } from '@/types'

const list = ref<Exhibition[]>([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)

const emptyForm = (): Exhibition => ({
  id: 0,
  title: '',
  kind: 'TEMPORARY',
  hall: '',
  summary: '',
  content: '',
  coverImage: '',
  displayStatus: 'ONGOING',
  sortOrder: 0,
  published: true
})
const form = ref<Exhibition>(emptyForm())
const range = computed({
  get: () =>
    form.value.startDate && form.value.endDate ? [form.value.startDate, form.value.endDate] : undefined,
  set: (val) => {
    if (val && val.length === 2) {
      form.value.startDate = val[0]
      form.value.endDate = val[1]
    }
  }
})

const load = async () => {
  loading.value = true
  try {
    list.value = await adminExhibitionApi.getList()
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.value = emptyForm()
  dialogVisible.value = true
}

const openEdit = (row: Exhibition) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const doUpload = async (options: UploadRequestOptions) => {
  const res = await adminUploadApi.upload(options.file)
  form.value.coverImage = res.url
}

const save = async (row?: Exhibition) => {
  const data = row ?? form.value
  if (!data.title) {
    ElMessage.warning('请填写标题')
    return
  }
  if (data.kind === 'TEMPORARY' && (!data.startDate || !data.endDate)) {
    ElMessage.warning('临展须填写展期')
    return
  }
  saving.value = true
  try {
    if (data.id) {
      await adminExhibitionApi.update(data.id, data)
    } else {
      await adminExhibitionApi.create(data)
    }
    dialogVisible.value = false
    ElMessage.success('已保存')
    await load()
  } finally {
    saving.value = false
  }
}

const remove = async (row: Exhibition) => {
  await ElMessageBox.confirm(`确认删除「${row.title}」？`, '删除确认', { type: 'warning' })
  await adminExhibitionApi.delete(row.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>
