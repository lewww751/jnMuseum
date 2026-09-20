<template>
  <div class="p-6">
    <h1 class="text-xl font-bold mb-4">指南编辑</h1>

    <el-tabs v-model="activeTab" @tab-change="load">
      <el-tab-pane label="参观指南" name="visit" />
      <el-tab-pane label="关于本馆" name="about" />
    </el-tabs>

    <el-alert
      class="mb-4"
      type="info"
      :closable="false"
      title="纯文本内容，段落之间用空行分隔"
    />

    <el-input
      v-model="content"
      type="textarea"
      :rows="18"
      :loading="loading"
      placeholder="正在加载…"
    />

    <div class="mt-4">
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminGuideApi } from '@/api/admin'
import { guideApi } from '@/api/public'

const activeTab = ref<'visit' | 'about'>('visit')
const content = ref('')
const loading = ref(false)
const saving = ref(false)

const load = async () => {
  loading.value = true
  try {
    const data = await guideApi.getContent(activeTab.value)
    content.value = data.content
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  try {
    await adminGuideApi.update(activeTab.value, content.value)
    ElMessage.success('已保存')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
