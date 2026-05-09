
export interface WebsocketMessage {
  status: boolean
  order: string
  msg: string
  [key: string]: any
}

export class WebsocketManager {
  private socket: WebSocket | null = null
  private heartbeatTimer: number | null = null
  private reconnectTimer: number | null = null

  private readonly HEARTBEAT_INTERVAL = 30000
  private readonly HEARTBEAT_TIMEOUT_LIMIT = 3
  private readonly RECONNECT_INTERVAL = 30000
  private readonly MAX_RECONNECT_ATTEMPTS = 5

  private missedHeartbeats = 0
  private reconnectAttempts = 0

  private url = ''
  private isActive = false;
  public onMessage: ((data: WebsocketMessage) => void) | null = null
  public onForceLogout: ((data: WebsocketMessage) => void) | null = null
  public aiAnwser: ((data: WebsocketMessage) => void) | null = null
  public heartbeat: ((data: WebsocketMessage) => void) | null = null
  public refreshToken: ((data: WebsocketMessage) => void) | null = null

  constructor(url: string) {
    this.url = url
  }

  /**
   * 
   * @param token 连接websocket
   */
  connect(token: string) {
    if (this.socket) {
      this.socket.close()
    }

    this.socket = new WebSocket(`${this.url}?Authorization=${encodeURIComponent(token)}`)

    this.socket.onopen = () => {
      console.log('[WebSocket] 连接成功')
      this.missedHeartbeats = 0
      this.reconnectAttempts = 0
      this.startHeartbeat()
    }

    this.socket.onmessage = (event: MessageEvent) => {
      const data: WebsocketMessage = JSON.parse(event.data)

      switch (data.order) {
        case 'HEART_BEAT': {
          this.heartbeat?.(data)
          this.missedHeartbeats = 0
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
          this.missedHeartbeats = 0
          break
        }
        default: {
          this.onMessage?.(data)
          break
        }
      }
    }

    this.socket.onclose = () => {
      this.stopHeartbeat()
      if (!this.isActive) {
        this.tryReconnect()
      }
    }

    this.socket.onerror = (err) => {
      console.error('[WebSocket] 连接错误', err)
    }
  }

  /**
   * 开始心跳
   */
  private startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatTimer = window.setInterval(() => {
      if (this.socket?.readyState !== WebSocket.OPEN) return

      try {
        this.socket.send(JSON.stringify({ order: 'HEART_BEAT' }))
        this.missedHeartbeats++
        if (this.missedHeartbeats >= this.HEARTBEAT_TIMEOUT_LIMIT) {
          console.error('[WebSocket] 心跳失败过多，关闭连接');
          this.socket?.close()
        }
      } catch (e) {
        console.error('[WebSocket] 心跳异常', e)
      }
    }, this.HEARTBEAT_INTERVAL)
  }

  private stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 重连websocket
   * @returns 
   */
  private tryReconnect() {
    if (this.reconnectAttempts >= this.MAX_RECONNECT_ATTEMPTS) {
      console.error('[WebSocket] 达到最大重连次数，放弃')
      return
    }

    if (this.reconnectTimer) return

    console.log(`[WebSocket] ${this.RECONNECT_INTERVAL / 1000}s 后尝试重连`)
    const token = sessionStorage.getItem('Authorization') || '';
    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectAttempts++
      this.connect(token)
      this.reconnectTimer = null
    }, this.RECONNECT_INTERVAL)
  }

  /**
   * 发送消息
   * @param data 
   */
  send(data: string | object) {
    if (this.socket?.readyState === WebSocket.OPEN) {
      const payload = typeof data === 'string' ? data : JSON.stringify(data)
      this.socket.send(payload)
    } else {
      console.warn('[WebSocket] 当前连接不可发送消息')
    }
  }

  /**
   * 关闭
   */
  close() {
    if (this.socket) {
      this.socket.close()
      this.socket = null
      this.isActive = true;
    }
    this.stopHeartbeat()
  }
}
