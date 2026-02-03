import { websocket_url } from '@/common/global-config'
import { WebsocketManager } from './websocketManager'

let wsInstance: WebsocketManager | null = null

export function getWebSocketInstance() {
  if (!wsInstance) {
    wsInstance = new WebsocketManager(websocket_url)
  }
  return wsInstance
}