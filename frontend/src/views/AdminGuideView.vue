<template>
  <div class="guide">
    <h1>指南管理</h1>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="关于我们" name="about">
        <el-form :model="form" label-width="100px">
          <el-form-item label="内容">
            <el-input type="textarea" v-model="form.content" />
          </el-form-item>
          <el-button type="primary" @click="saveContent('about')">保存</el-button>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="联系我们" name="contact">
        <el-form :model="form" label-width="100px">
          <el-form-item label="内容">
            <el-input type="textarea" v-model="form.content" />
          </el-form-item>
          <el-button type="primary" @click="saveContent('contact')">保存</el-button>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="参观指南" name="visit">
        <el-form :model="form" label-width="100px">
          <el-form-item label="内容">
            <el-input type="textarea" v-model="form.content" />
          </el-form-item>
          <el-button type="primary" @click="saveContent('visit')">保存</el-button>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="展览信息" name="exhibitions">
        <el-form :model="form" label-width="100px">
          <el-form-item label="内容">
            <el-input type="textarea" v-model="form.content" />
          </el-form-item>
          <el-button type="primary" @click="saveContent('exhibitions')">保存</el-button>
        </el-form>
      </el-tab-pane>
      
      <el-tab-pane label="活动信息" name="events">
        <el-form :model="form" label-width="100px">
          <el-form-item label="内容">
            <el-input type="textarea" v-model="form.content" />
          </el-form-item>
          <el-button type="primary" @click="saveContent('events')">保存</el-button>
        </form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getGuideContent, saveGuideContent } from '../api/admin';

const activeTab = ref('about');
const form = ref<any>({});

onMounted(async () => {
  try {
    const response = await getGuideContent(activeTab.value);
    form.value = response.data;
  } catch (error) {
    console.error('Failed to fetch guide content:', error);
  }
});

const saveContent = async (key: string) => {
  try {
    await saveGuideContent(key, form.value);
    ElMessage.success('保存成功');
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败');
  }
};
</script>