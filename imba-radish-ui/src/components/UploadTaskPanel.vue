<template>
  <!-- 文件选择器常驻布局层，任何页面都能通过 openPicker() 唤起 -->
  <input ref="inputRef" class="upload-task-panel__input" type="file" multiple @change="handleInputChange" />

  <transition name="el-fade-in-linear">
    <section v-if="panelVisible" class="upload-panel" :class="{ 'upload-panel--collapsed': collapsed }"
      @dragover.prevent="dragOver = true" @dragleave.prevent="dragOver = false" @drop.prevent="handleDrop">
      <header class="upload-panel__header">
        <div class="upload-panel__title">
          <span>上传任务</span>
          <span class="upload-panel__badge">{{ summaryText }}</span>
        </div>
        <div class="upload-panel__tools">
          <el-button link type="primary" @click="clearFinished">清除已完成</el-button>
          <el-button link @click="collapsed = !collapsed">{{ collapsed ? '展开' : '收起' }}</el-button>
          <el-button link @click="closePanel">关闭</el-button>
        </div>
      </header>

      <div v-if="!collapsed" class="upload-panel__body" :class="{ 'upload-panel__body--dragging': dragOver }">
        <p v-if="dragOver" class="upload-panel__drop-tip">松开即可加入上传队列</p>
        <ul v-if="tasks.length" class="upload-task-list">
          <li v-for="task in tasks" :key="task.id" class="upload-task">
            <div class="upload-task__main">
              <span class="upload-task__name" :title="task.name">{{ task.name }}</span>
              <span class="upload-task__size">{{ formatSize(task.size) }}</span>
            </div>
            <div class="upload-task__status">
              <span :class="['upload-task__label', `upload-task__label--${task.status}`]">{{ statusText(task) }}</span>
              <div class="upload-task__actions">
                <el-button v-if="!isTerminal(task.status)" link type="danger" @click="cancelTask(task)">取消</el-button>
                <el-button v-if="canRetry(task.status)" link type="primary" @click="retryTask(task)">重试</el-button>
                <el-button v-if="isTerminal(task.status)" link @click="removeTask(task)">移除</el-button>
              </div>
            </div>
            <el-progress :percentage="task.percent" :stroke-width="4" :show-text="false"
              :status="progressStatus(task.status)" />
          </li>
        </ul>
        <div v-else class="upload-panel__empty">
          <p>可以把文件拖到这里批量上传</p>
          <el-button link type="primary" @click="openPicker">选择文件</el-button>
        </div>
      </div>
    </section>
  </transition>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import {
  useUploadTasks, isTerminal, canRetry,
  type UploadTask, type TaskStatus
} from '@/composables/useUploadTasks'

const inputRef = ref<HTMLInputElement>()
const {
  tasks, panelVisible, collapsed, dragOver, summaryText,
  setPicker, openPicker, handleInputChange, handleDrop,
  closePanel, clearFinished, cancelTask, retryTask, removeTask
} = useUploadTasks()

onMounted(() => setPicker(inputRef.value ?? null))
onBeforeUnmount(() => setPicker(null))

const formatSize = (size: number): string => {
  if (size <= 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const index = Math.min(units.length - 1, Math.floor(Math.log(size) / Math.log(1024)))
  return `${(size / 1024 ** index).toFixed(1)} ${units[index]}`
}

const statusText = (task: UploadTask): string => {
  switch (task.status) {
    case 'queued': return '排队中'
    case 'hashing': return `计算指纹 ${task.hashPercent}%`
    case 'checking': return '检查秒传'
    case 'uploading': return `上传中 ${Math.round((task.uploadedBytes / Math.max(task.size, 1)) * 100)}%`
    case 'merging': return '合并并写入存储'
    case 'done': return '已完成'
    case 'cancelled': return '已取消'
    case 'failed': return task.error ? `失败：${task.error}` : '上传失败'
  }
}

const progressStatus = (status: TaskStatus): 'success' | 'exception' | undefined => {
  if (status === 'done') return 'success'
  if (status === 'failed' || status === 'cancelled') return 'exception'
  return undefined
}
</script>

<style scoped>
.upload-task-panel__input {
  display: none;
}

.upload-panel {
  position: fixed;
  right: var(--radish-space-6);
  bottom: var(--radish-space-6);
  z-index: 2000;
  width: 380px;
  overflow: hidden;
  background: var(--radish-bg-card);
  border: 1px solid var(--radish-border);
  border-radius: var(--radish-radius-lg);
  box-shadow: var(--radish-shadow-pop);
}

.upload-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--radish-space-2);
  padding: var(--radish-space-3) var(--radish-space-4);
  background: var(--radish-bg-bar);
  border-bottom: 1px solid var(--radish-border-light);
}

.upload-panel--collapsed .upload-panel__header {
  border-bottom: 0;
}

.upload-panel__title {
  display: flex;
  align-items: center;
  gap: var(--radish-space-2);
  font-size: var(--radish-font-md);
  font-weight: var(--radish-weight-semibold);
  color: var(--radish-text-primary);
}

.upload-panel__badge {
  font-size: var(--radish-font-xs);
  font-weight: var(--radish-weight-normal);
  color: var(--radish-text-secondary);
}

.upload-panel__tools {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  gap: var(--radish-space-1);
}

.upload-panel__body {
  max-height: 320px;
  overflow-y: auto;
  padding: var(--radish-space-2) var(--radish-space-4) var(--radish-space-3);
  transition: var(--radish-transition);
}

.upload-panel__body--dragging {
  background: var(--radish-primary-light-9);
  outline: 1px dashed var(--radish-primary-light-5);
}

.upload-panel__drop-tip,
.upload-panel__empty p {
  margin: 0;
  padding: var(--radish-space-4) 0;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-secondary);
  text-align: center;
}

.upload-panel__empty p {
  padding-bottom: var(--radish-space-1);
}

.upload-panel__empty {
  text-align: center;
}

.upload-task-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.upload-task {
  padding: var(--radish-space-3) 0;
  border-bottom: 1px solid var(--radish-border-light);
}

.upload-task:last-child {
  border-bottom: 0;
}

.upload-task__main {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--radish-space-2);
}

.upload-task__name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  font-size: var(--radish-font-base);
  color: var(--radish-text-primary);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-task__size {
  flex-shrink: 0;
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.upload-task__status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--radish-space-2);
  margin: var(--radish-space-1) 0;
}

.upload-task__label {
  font-size: var(--radish-font-xs);
  color: var(--radish-text-secondary);
}

.upload-task__label--hashing,
.upload-task__label--checking,
.upload-task__label--uploading,
.upload-task__label--merging {
  color: var(--radish-primary);
}

.upload-task__label--done {
  color: var(--radish-success);
}

.upload-task__label--failed {
  color: var(--radish-danger);
}

.upload-task__label--cancelled {
  color: var(--radish-info);
}

.upload-task__actions {
  display: flex;
  flex-shrink: 0;
  gap: var(--radish-space-1);
}
</style>
