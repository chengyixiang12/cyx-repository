import { websocket_url } from '@/common/global-config'
import { WebsocketManager, ConnectionStatus, type WebsocketConfig } from './websocketManager'

let wsInstance: WebsocketManager | null = null

export function getWebSocketInstance(config?: WebsocketConfig) {
  if (!wsInstance) {
    wsInstance = new WebsocketManager(websocket_url, config)
  }
  return wsInstance
}

export { ConnectionStatus }
export type { WebsocketConfig, WebsocketMessage } from './websocketManager'