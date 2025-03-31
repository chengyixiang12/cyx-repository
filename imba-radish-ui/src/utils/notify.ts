import { ElNotification } from 'element-plus'

type NotificationType = 'success' | 'warning' | 'error' | 'info'

export function showNotify(
  message: string,
  type: NotificationType = 'error',
  title?: string
) {
  ElNotification({
    title: title || type.toUpperCase(),
    message,
    type,
    duration: type === 'error' ? 5000 : 3000,
    position: 'top-right'
  })
}