import { computed, markRaw, ref } from 'vue'
import {
  uploadChunkApi, uploadFileApi, mergeChunkApi, getFileByMd5Api,
  getUploadProgressApi, cancelChunkApi
} from '@/api/file'
import { showMessage } from '@/utils/message'
import { calculateFileMd5, chunkSize, isAbortError } from '@/utils/filemd5'

/** 与后端 radish.upload.max-total-file-size、nginx client_max_body_size 保持一致 */
const MAX_FILE_SIZE = 2 * 1024 * 1024 * 1024
const MAX_SELECTION = 100
/** 两级并发：同时处理的文件数 / 单个文件内并行的分片数 */
const MAX_FILE_CONCURRENCY = 3
const MAX_CHUNK_CONCURRENCY = 4
const CHUNK_RETRY = 3

export type TaskStatus =
  | 'queued' | 'hashing' | 'checking' | 'uploading' | 'merging'
  | 'done' | 'failed' | 'cancelled'

export interface UploadTask {
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

/**
 * 全局上传任务状态（模块级单例）：
 * 顶栏入口、任务面板、文件管理页按钮共用同一份队列。
 * 面板关闭或切换页面都不会销毁任务，切账号时调用 resetUploadTasks 清空。
 */
const tasks = ref<UploadTask[]>([])
const panelVisible = ref(false)
const collapsed = ref(false)
const dragOver = ref(false)
/** 每次「一批任务全部结束且有成功」时自增，供文件列表页刷新 */
const successVersion = ref(0)

/** AbortController 放在响应式对象之外，避免被 Vue 代理 */
const runtimes = new Map<string, AbortController>()
let picker: HTMLInputElement | null = null
let activeCount = 0
let successSinceFlush = false
let taskSeq = 0
let listenersBound = false

export const isTerminal = (status: TaskStatus) => status === 'done' || status === 'failed' || status === 'cancelled'
export const canRetry = (status: TaskStatus) => status === 'failed' || status === 'cancelled'

/** Badge 展示任务列表总数；tooltip 用进行中数量补充语义 */
const taskCount = computed(() => tasks.value.length)
const activeTaskCount = computed(() => tasks.value.filter(task => !isTerminal(task.status)).length)
const summaryText = computed(() => {
  const running = activeTaskCount.value
  const done = tasks.value.filter(task => task.status === 'done').length
  return running ? `${running} 个进行中 · ${done} 个完成` : `共 ${tasks.value.length} 个`
})

const openPanel = () => {
  panelVisible.value = true
  collapsed.value = false
}

const closePanel = () => {
  panelVisible.value = false
}

const togglePanel = () => {
  if (panelVisible.value) closePanel()
  else openPanel()
}

/** 隐藏 input 由常驻布局的 UploadTaskPanel 注册，任何页面都能唤起文件选择器 */
const setPicker = (element: HTMLInputElement | null) => {
  picker = element
}

const openPicker = () => picker?.click()

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

  const seen = new Set(tasks.value.map(task => `${task.name}|${task.size}|${task.file.lastModified}`))
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
  openPanel()
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
        // resetUploadTasks 可能已把计数清零，避免扣成负数导致后续并发失控
        activeCount = Math.max(0, activeCount - 1)
        runtimes.delete(next.id)
        pump()
      })
  }

  // 所有任务都结束后只刷新一次列表，不再每个文件刷新一遍
  if (activeCount === 0 && !tasks.value.some(task => task.status === 'queued') && successSinceFlush) {
    successSinceFlush = false
    successVersion.value += 1
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

/** 退出登录 / 被强制下线时清空，避免下一个账号看到上一账号的文件名与任务 */
const resetUploadTasks = () => {
  runtimes.forEach(controller => controller.abort())
  runtimes.clear()
  activeCount = 0
  successSinceFlush = false
  tasks.value = []
  panelVisible.value = false
  collapsed.value = false
  dragOver.value = false
}

// 只做提示，不在页面卸载时删除分片：刷新/误关后还能断点续传，孤儿分片由服务端 TTL 回收
const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (tasks.value.some(task => !isTerminal(task.status))) {
    event.preventDefault()
    event.returnValue = ''
  }
}

const bindGlobalListeners = () => {
  if (listenersBound || typeof window === 'undefined') return
  listenersBound = true
  window.addEventListener('beforeunload', handleBeforeUnload)
}

export function useUploadTasks() {
  bindGlobalListeners()
  return {
    tasks,
    panelVisible,
    collapsed,
    dragOver,
    taskCount,
    activeTaskCount,
    summaryText,
    successVersion,
    setPicker,
    openPicker,
    handleInputChange,
    handleDrop,
    togglePanel,
    openPanel,
    closePanel,
    cancelTask,
    retryTask,
    removeTask,
    clearFinished,
    resetUploadTasks
  }
}
