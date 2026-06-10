import { WebsocketManager, ConnectionStatus, type WebsocketConfig } from './websocketManager'

let wsInstance: WebsocketManager | null = null

export function getWebSocketInstance(config?: WebsocketConfig) {
  if (!wsInstance) {
    wsInstance = new WebsocketManager('/ws', config)
  }
  return wsInstance
}

export { ConnectionStatus }
export type { WebsocketConfig, WebsocketMessage } from './websocketManager'