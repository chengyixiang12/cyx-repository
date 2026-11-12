<template>
  <div class="file-upload">
    <el-button type="primary" @click="openPicker">上传文件</el-button>
    <input ref="inputRef" class="file-upload__input" type="file" multiple @change="handleInputChange" />

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
            <el-button link @click="panelVisible = false">关闭</el-button>
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
          <p v-else class="upload-panel__empty">可以把文件拖到这里批量上传</p>
        </div>
      </section>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, markRaw, onBeforeUnmount, onMounted, ref } from 'vue'
import {
  uploadChunkApi, uploadFileApi, mergeChunkApi, getFileByMd5Api,
  getUploadProgressApi, cancelChunkApi
} from '@/api/file'
import { showMessage } from '@/utils/message'
import { calculateFileMd5, chunkSize, isAbortError } from '@/utils/filemd5'

const emit = defineEmits<{ (e: 'upload-success'): void }>()

/** 与后端 radish.upload.max-total-file-size、nginx client_max_body_size 保持一致 */
const MAX_FILE_SIZE = 2 * 1024 * 1024 * 1024
const MAX_SELECTION = 100
/** 两级并发：同时处理的文件数 / 单个文件内并行的分片数 */
const MAX_FILE_CONCURRENCY = 3
const MAX_CHUNK_CONCURRENCY = 4
const CHUNK_RETRY = 3

type TaskStatus =
  | 'queued' | 'hashing' | 'checking' | 'uploading' | 'merging'
  | 'done' | 'failed' | 'cancelled'

interface UploadTask {
  id: string
  file: File
  name: string
  size: number
  status: TaskStatus
  /** 总进度：指纹计算占前 20%，分片上传占后 80% */
  percent: number
  hashPercent: number
  uploadedBytes: number
  md5?: string
  error?: string
}

const inputRef = ref<HTMLInputElement>()
const tasks = ref<UploadTask[]>([])
const panelVisible = ref(false)
const collapsed = ref(false)
const dragOver = ref(false)

/** AbortController / 并发计数放在响应式对象之外，避免被 Vue 代理 */
const runtimes = new Map<string, AbortController>()
let activeCount = 0
let successSinceFlush = false
let taskSeq = 0

const isTerminal = (status: TaskStatus) => status === 'done' || status === 'failed' || status === 'cancelled'
const canRetry = (status: TaskStatus) => status === 'failed' || status === 'cancelled'

const summaryText = computed(() => {
  const running = tasks.value.filter(t => !isTerminal(t.status)).length
  const done = tasks.value.filter(t => t.status === 'done').length
  return running ? `${running} 个进行中 · ${done} 个完成` : `共 ${tasks.value.length} 个`
})

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

const openPicker = () => inputRef.value?.click()

const handleInputChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = Array.from(target.files ?? [])
  // 清空 value，否则再次选择同一个文件不会触发 change
  target.value = ''
  enqueue(files)
}

const handleDrop = (event: DragEvent) => {
  dragOver.value = false
  enqueue(Array.from(event.dataTransfer?.files ?? []))
}

const enqueue = (files: File[]) => {
  if (!files.length) return
  if (files.length > MAX_SELECTION) {
    showMessage(`单次最多选择 ${MAX_SELECTION} 个文件`, 'warning')
    files = files.slice(0, MAX_SELECTION)
  }

  const seen = new Set(tasks.value.map(t => `${t.name}|${t.size}|${t.file.lastModified}`))
  const fresh: UploadTask[] = []
  let skipped = 0

  for (const file of files) {
    const key = `${file.name}|${file.size}|${file.lastModified}`
    if (seen.has(key)) continue
    seen.add(key)
    const task: UploadTask = {
      id: `upload-${Date.now()}-${++taskSeq}`,
      file: markRaw(file),
      name: file.name,
      size: file.size,
      status: 'queued',
      percent: 0,
      hashPercent: 0,
      uploadedBytes: 0
    }
    if (file.size > MAX_FILE_SIZE || file.size === 0) {
      skipped += 1
      task.status = 'failed'
      task.error = file.size === 0 ? '空文件不支持上传' : '文件超过 2GB 上限'
    }
    fresh.push(task)
  }

  if (!fresh.length) {
    showMessage('这些文件已在上传队列中', 'info')
    return
  }
  tasks.value = [...fresh, ...tasks.value]
  panelVisible.value = true
  collapsed.value = false
  if (skipped > 0) showMessage(`${skipped} 个文件超出限制，已跳过`, 'warning')
  pump()
}

const markDone = (task: UploadTask) => {
  task.status = 'done'
  task.percent = 100
  task.uploadedBytes = task.size
  task.error = undefined
  successSinceFlush = true
}

const backoff = (attempt: number, signal: AbortSignal): Promise<void> =>
  new Promise(resolve => {
    const timer = setTimeout(() => {
      signal.removeEventListener('abort', onAbort)
      resolve()
    }, 400 * 2 ** attempt)
    const onAbort = () => {
      clearTimeout(timer)
      signal.removeEventListener('abort', onAbort)
      resolve()
    }
    signal.addEventListener('abort', onAbort, { once: true })
  })

const uploadChunkWithRetry = async (
  task: UploadTask,
  md5: string,
  index: number,
  size: number,
  signal: AbortSignal,
  onProgress: (loaded: number) => void
) => {
  let lastError: unknown
  for (let attempt = 0; attempt < CHUNK_RETRY; attempt++) {
    if (signal.aborted) throw new Error('AbortError')
    try {
      const formData = new FormData()
      formData.append('chunk', task.file.slice(index * chunkSize, index * chunkSize + size))
      formData.append('fileMd5', md5)
      formData.append('chunkIndex', String(index))
      await uploadChunkApi(formData, { signal, onUploadProgress: onProgress })
      return
    } catch (error) {
      if (signal.aborted || isAbortError(error)) throw error
      lastError = error
      if (attempt < CHUNK_RETRY - 1) {
        await backoff(attempt, signal)
      }
    }
  }
  throw lastError instanceof Error ? lastError : new Error('分片上传失败')
}

const runTask = async (task: UploadTask): Promise<void> => {
  const controller = new AbortController()
  runtimes.set(task.id, controller)

  try {
    // 1. 全量 MD5 在 Worker 里算，主线程不阻塞；重试时复用已算好的值
    task.status = 'hashing'
    task.hashPercent = 0
    task.percent = 0
    const md5 = task.md5 ?? await calculateFileMd5(task.file, {
      onProgress: percent => {
        task.hashPercent = percent
        task.percent = Math.round(percent * 0.2)
      },
      signal: controller.signal
    })
    task.md5 = md5

    // 2. 秒传检查（命中时后端会为当前用户/文件名建立记录，返回 id）
    task.status = 'checking'
    const hitId = await getFileByMd5Api(md5, task.name, controller.signal)
    if (hitId) {
      markDone(task)
      return
    }

    // 3. 小文件直传
    if (task.size <= chunkSize) {
      task.status = 'uploading'
      const formData = new FormData()
      formData.append('multipartFile', task.file)
      formData.append('fileMd5', md5)
      await uploadFileApi(formData, {
        signal: controller.signal,
        onUploadProgress: loaded => {
          task.uploadedBytes = Math.min(task.size, loaded)
          task.percent = 20 + Math.round((task.uploadedBytes / task.size) * 80)
        }
      })
      markDone(task)
      return
    }

    // 4. 断点续传：先问服务端哪些分片已落盘且长度正确
    const progress = await getUploadProgressApi(md5, task.size, chunkSize, controller.signal)
    const uploaded = new Set(progress.uploadedIndices ?? [])
    const totalChunks = Math.ceil(task.size / chunkSize)
    const expectedSize = (index: number) =>
      index === totalChunks - 1 ? task.size - index * chunkSize : chunkSize

    // 进度只按"已确认字节"累加，不再像旧实现那样在分片开始前就加到 100%
    const loadedMap = new Map<number, number>()
    uploaded.forEach(index => {
      if (index >= 0 && index < totalChunks) loadedMap.set(index, expectedSize(index))
    })
    const refreshProgress = () => {
      let sum = 0
      loadedMap.forEach(value => { sum += value })
      task.uploadedBytes = Math.min(task.size, sum)
      task.percent = Math.min(99, 20 + Math.round((task.uploadedBytes / task.size) * 80))
    }
    refreshProgress()

    const pending: number[] = []
    for (let index = 0; index < totalChunks; index++) {
      if (!uploaded.has(index)) pending.push(index)
    }

    task.status = 'uploading'
    let cursor = 0
    const chunkWorker = async () => {
      while (cursor < pending.length && !controller.signal.aborted) {
        const index = pending[cursor++]
        const size = expectedSize(index)
        await uploadChunkWithRetry(task, md5, index, size, controller.signal, loaded => {
          // 重试时进度不倒退
          const current = loadedMap.get(index) ?? 0
          loadedMap.set(index, Math.min(size, Math.max(current, loaded)))
          refreshProgress()
        })
        loadedMap.set(index, size)
        refreshProgress()
      }
    }
    await Promise.all(
      Array.from({ length: Math.min(MAX_CHUNK_CONCURRENCY, pending.length) }, chunkWorker)
    )
    if (controller.signal.aborted) return

    // 5. 合并（后端同步写 MinIO 成功才返回，100% 即代表可下载）
    task.status = 'merging'
    task.percent = 99
    await mergeChunkApi({
      fileMd5: md5,
      fileName: task.name,
      total: totalChunks,
      fileSize: task.size,
      chunkSize,
      signal: controller.signal
    })
    markDone(task)
  } catch (error) {
    if (controller.signal.aborted || isAbortError(error)) {
      task.status = 'cancelled'
      return
    }
    task.status = 'failed'
    task.error = error instanceof Error ? error.message : '上传失败'
  }
}

const pump = () => {
  while (activeCount < MAX_FILE_CONCURRENCY) {
    const next = tasks.value.find(task => task.status === 'queued')
    if (!next) break
    activeCount += 1
    next.status = 'hashing'
    void runTask(next)
      .catch(error => {
        if (!isAbortError(error)) {
          next.status = 'failed'
          next.error = error instanceof Error ? error.message : '上传失败'
        }
      })
      .finally(() => {
        activeCount -= 1
        runtimes.delete(next.id)
        pump()
      })
  }

  // 所有任务都结束后只刷新一次列表，不再每个文件刷新一遍
  if (activeCount === 0 && !tasks.value.some(task => task.status === 'queued') && successSinceFlush) {
    successSinceFlush = false
    emit('upload-success')
  }
}

const cancelTask = async (task: UploadTask) => {
  if (isTerminal(task.status)) return
  runtimes.get(task.id)?.abort()
  task.status = 'cancelled'
  task.error = undefined

  // 取消时主动清掉服务端分片；清不掉也没关系，服务端 TTL 会兜底
  if (task.md5) {
    const inUse = tasks.value.some(
      other => other.id !== task.id && other.md5 === task.md5 && !isTerminal(other.status)
    )
    if (!inUse) {
      try {
        await cancelChunkApi(task.md5)
      } catch {
        // 静默：清理失败由服务端定时回收
      }
    }
  }
}

const retryTask = (task: UploadTask) => {
  if (!canRetry(task.status)) return
  task.status = 'queued'
  task.error = undefined
  task.percent = 0
  task.uploadedBytes = 0
  task.hashPercent = 0
  pump()
}

const removeTask = (task: UploadTask) => {
  runtimes.get(task.id)?.abort()
  tasks.value = tasks.value.filter(item => item.id !== task.id)
}

const clearFinished = () => {
  tasks.value = tasks.value.filter(task => !isTerminal(task.status))
}

// 只做提示，不在页面卸载时删除分片：刷新/误关后还能断点续传，孤儿分片由服务端 TTL 回收
const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (tasks.value.some(task => !isTerminal(task.status))) {
    event.preventDefault()
    event.returnValue = ''
  }
}

onMounted(() => window.addEventListener('beforeunload', handleBeforeUnload))
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  runtimes.forEach(controller => controller.abort())
  runtimes.clear()
})
</script>

<style scoped>
.file-upload {
  display: inline-flex;
}

.file-upload__input {
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
.upload-panel__empty {
  margin: 0;
  padding: var(--radish-space-4) 0;
  font-size: var(--radish-font-sm);
  color: var(--radish-text-secondary);
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
