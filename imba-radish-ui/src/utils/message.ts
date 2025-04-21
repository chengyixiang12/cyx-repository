import { ElMessage } from 'element-plus'

export const showMessage = (message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info') => {
  ElMessage({
    message,
    type,
    duration: 2000,
    offset: 50
  })
}