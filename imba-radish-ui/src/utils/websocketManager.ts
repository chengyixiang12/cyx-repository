export interface WebsocketMessage {
  status: boolean
  order: string
  msg: string
  [key: string]: any
}

export enum ConnectionStatus {
  CLOSED = 'closed',
  CONNECTING = 'connecting',
  OPEN = 'open',
  RECONNECTING = 'reconnecting'
}

export interface WebsocketConfig {
  reconnectInterval?: number
  maxReconnectAttempts?: number
}

export class WebsocketManager {
  private socket: WebSocket | null = null
  private reconnectTimer: number | null = null

  private readonly RECONNECT_INTERVAL: number
  private readonly MAX_RECONNECT_ATTEMPTS: number

  private reconnectAttempts = 0

  private url = ''
  private isActive = false
  private messageQueue: string[] = []

  public status = ConnectionStatus.CLOSED

  public onMessage: ((data: WebsocketMessage) => void) | null = null
  public onForceLogout: ((data: WebsocketMessage) => void) | null = null
  public aiAnwser: ((data: WebsocketMessage) => void) | null = null
  public refreshToken: ((data: WebsocketMessage) => void) | null = null

  public onOpen: (() => void) | null = null
  public onClose: ((code: number, reason: string) => void) | null = null
  public onError: ((error: Event) => void) | null = null
  public onStatusChange: ((status: ConnectionStatus) => void) | null = null

  constructor(url: string, config?: WebsocketConfig) {
    this.url = url
    this.RECONNECT_INTERVAL = config?.reconnectInterval ?? 30000
    this.MAX_RECONNECT_ATTEMPTS = config?.maxReconnectAttempts ?? 5
  }

  private setStatus(status: ConnectionStatus) {
    if (this.status !== status) {
      this.status = status
      this.onStatusChange?.(status)
    }
  }

  connect(token: string) {
    if (this.socket) {
      this.socket.close()
    }

    this.setStatus(ConnectionStatus.CONNECTING)

    this.socket = new WebSocket(`${this.url}?Authorization=${encodeURIComponent(token)}`)

    this.socket.onopen = () => {
      console.log('[WebSocket] 连接成功')
      this.reconnectAttempts = 0
      this.isActive = false
      this.setStatus(ConnectionStatus.OPEN)
      this.onOpen?.()
      this.flushMessageQueue()
    }

    this.socket.onmessage = (event: MessageEvent) => {
      try {
        const data: WebsocketMessage = JSON.parse(event.data)

        switch (data.order) {
          case 'PONG': {
            if (data.refreshFlag) {
              const fingerprint = sessionStorage.getItem('fingerprint');
              this.send({ order: 'REFRESH_TOKEN', fingerprint })
            }
            break
          }
          case 'FORCE_OFFLINE': {
            this.close()
            this.onForceLogout?.(data)
            break
          }
          case 'AI': {
            this.aiAnwser?.(data)
            break
          }
          case 'REFRESH_TOKEN': {
            this.refreshToken?.(data)
            break
          }
          case 'PING': {
            this.send({ order: 'PONG' })
            break
          }
          default: {
            this.onMessage?.(data)
            break
          }
        }
      } catch (e) {
        console.error('[WebSocket] 消息解析失败', e)
      }
    }

    this.socket.onclose = (event: CloseEvent) => {
      this.setStatus(ConnectionStatus.CLOSED)
      this.onClose?.(event.code, event.reason)

      if (!this.isActive) {
        this.tryReconnect(event.code)
      }
    }

    this.socket.onerror = (error: Event) => {
      console.error('[WebSocket] 连接错误', error)
      this.onError?.(error)
    }
  }

  private tryReconnect(closeCode?: number) {
    if (this.reconnectAttempts >= 1 && closeCode === 1006) {
      console.warn('[WebSocket] 重连被拒(1006)，token 可能已失效，停止重连')
      this.close()
      this.onForceLogout?.({ status: false, order: 'FORCE_OFFLINE', msg: '登录状态已过期，请重新登录' })
      return
    }

    if (this.reconnectAttempts >= this.MAX_RECONNECT_ATTEMPTS) {
      console.error('[WebSocket] 达到最大重连次数，放弃')
      return
    }

    if (this.reconnectTimer) return

    const token = sessionStorage.getItem('Authorization')

    if (!token) {
      console.warn('[WebSocket] 未获取到token，无法重连')
      return
    }

    this.setStatus(ConnectionStatus.RECONNECTING)
    this.reconnectAttempts++

    if (this.reconnectAttempts === 1) {
      console.log(`[WebSocket] 立即重连 (${this.reconnectAttempts}/${this.MAX_RECONNECT_ATTEMPTS})`)
      this.connect(token)
    } else {
      console.log(`[WebSocket] ${this.RECONNECT_INTERVAL / 1000}s 后尝试重连 (${this.reconnectAttempts}/${this.MAX_RECONNECT_ATTEMPTS})`)
      this.reconnectTimer = window.setTimeout(() => {
        this.connect(token)
        this.reconnectTimer = null
      }, this.RECONNECT_INTERVAL)
    }
  }

  private flushMessageQueue() {
    while (this.messageQueue.length > 0) {
      const message = this.messageQueue.shift()
      if (message) {
        this.sendRaw(message)
      }
    }
  }

  private sendRaw(data: string) {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(data)
    }
  }

  send(data: string | object) {
    const payload = typeof data === 'string' ? data : JSON.stringify(data)

    if (this.socket?.readyState === WebSocket.OPEN) {
      this.sendRaw(payload)
    } else if (this.status !== ConnectionStatus.CLOSED) {
      this.messageQueue.push(payload)
      if (this.messageQueue.length > 100) {
        this.messageQueue.shift()
      }
    } else {
      console.warn('[WebSocket] 当前连接已关闭，无法发送消息')
    }
  }

  close(code?: number, reason?: string) {
    this.isActive = true

    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }

    if (this.socket) {
      this.socket.close(code, reason)
      this.socket = null
    }

    this.messageQueue = []
  }

  isConnected(): boolean {
    return this.socket?.readyState === WebSocket.OPEN
  }

  getStatus(): ConnectionStatus {
    return this.status
  }
}