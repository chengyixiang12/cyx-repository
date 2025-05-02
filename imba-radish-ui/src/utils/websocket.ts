import { WebsocketManager } from './websocketManager'

let wsInstance: WebsocketManager | null = null

export function getWebSocketInstance() {
  if (!wsInstance) {
    wsInstance = new WebsocketManager('ws://127.0.0.1:8081/ws')
  }
  return wsInstance
}