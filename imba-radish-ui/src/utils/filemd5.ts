import type { HashResponse } from '@/workers/md5.worker'

/** 分片大小：前后端、进度校验都用同一个值 */
export const chunkSize = 5 * 1024 * 1024

export interface HashOptions {
  onProgress?: (percent: number) => void
  /** 取消时直接 terminate Worker，指纹计算会立刻停掉 */
  signal?: AbortSignal
}

export class HashAbortedError extends Error {
  constructor() {
    super('指纹计算已取消')
    this.name = 'AbortError'
  }
}

/**
 * 计算文件全量 MD5（32 位 hex）。
 * 计算过程在 Worker 中进行，主线程只收进度消息；选中大文件后可以立即渲染任务面板。
 */
export const calculateFileMd5 = (file: File, options: HashOptions = {}): Promise<string> =>
  new Promise<string>((resolve, reject) => {
    const worker = new Worker(new URL('../workers/md5.worker.ts', import.meta.url), {
      type: 'module'
    })
    let settled = false
    let onAbort: () => void = () => {}

    const settle = (finish: () => void) => {
      if (settled) return
      settled = true
      options.signal?.removeEventListener('abort', onAbort)
      worker.terminate()
      finish()
    }

    onAbort = () => settle(() => reject(new HashAbortedError()))

    if (options.signal) {
      if (options.signal.aborted) {
        onAbort()
        return
      }
      options.signal.addEventListener('abort', onAbort, { once: true })
    }

    worker.onmessage = (event: MessageEvent<HashResponse>) => {
      const message = event.data
      if (message.type === 'progress') {
        options.onProgress?.(message.percent)
      } else if (message.type === 'done') {
        settle(() => resolve(message.md5))
      } else {
        settle(() => reject(new Error(message.message)))
      }
    }

    worker.onerror = event => {
      settle(() => reject(new Error(event.message || '指纹计算失败')))
    }

    worker.postMessage({ type: 'hash', file, chunkSize })
  })

export const isAbortError = (error: unknown): boolean =>
  error instanceof HashAbortedError ||
  (typeof error === 'object' &&
    error !== null &&
    'name' in error &&
    (error as { name: unknown }).name === 'AbortError')
