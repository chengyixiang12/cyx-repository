<template>
  <div class="file-upload">
    <el-button type="primary" @click="openPicker">上传文件</el-button>
  </div>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { useUploadTasks } from '@/composables/useUploadTasks'

const emit = defineEmits<{ (e: 'upload-success'): void }>()

// 任务队列与面板已提升到全局单例，这里只负责「唤起选择器」+ 通知文件列表刷新
const { openPicker, successVersion } = useUploadTasks()

watch(successVersion, () => emit('upload-success'))
</script>

<style scoped>
.file-upload {
  display: inline-flex;
}
</style>
