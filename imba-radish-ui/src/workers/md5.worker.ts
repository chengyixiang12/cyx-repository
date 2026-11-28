import SparkMD5 from 'spark-md5'

/**
 * 全量 MD5 在 Web Worker 里算，主线程不再被 SparkMD5.append 阻塞。
 * 仍然逐片读完整文件（不做抽样 hash），秒传键、断点续传键、后端 32 位 hex 正则都不用改。
 */

interface HashRequest {
  type: 'hash'
  file: File
  chunkSize: number
}

export type HashResponse =
  | { type: 'progress'; percent: number }
  | { type: 'done'; md5: string }
  | { type: 'error'; message: string }

// 不引入 webworker lib（与 DOM lib 冲突），只声明这里用到的两个成员
const ctx = self as unknown as {
  onmessage: ((event: MessageEvent<HashRequest>) => void) | null
  postMessage: (message: HashResponse) => void
}

ctx.onmessage = async (event: MessageEvent<HashRequest>) => {
  const { file, chunkSize } = event.data
  try {
    const spark = new SparkMD5.ArrayBuffer()
    const total = Math.max(1, Math.ceil(file.size / chunkSize))

    for (let index = 0; index < total; index++) {
      const start = index * chunkSize
      const end = Math.min(start + chunkSize, file.size)
      // Blob.arrayBuffer() 在 Worker 中可用，比 FileReader 更直接，且不会占用主线程
      const buffer = await file.slice(start, end).arrayBuffer()
      spark.append(buffer)
      ctx.postMessage({ type: 'progress', percent: Math.round(((index + 1) / total) * 100) })
    }

    ctx.postMessage({ type: 'done', md5: spark.end() })
  } catch (error) {
    ctx.postMessage({
      type: 'error',
      message: error instanceof Error ? error.message : '指纹计算失败'
    })
  }
}
