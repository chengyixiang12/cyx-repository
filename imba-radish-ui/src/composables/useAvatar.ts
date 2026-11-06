import { ref } from 'vue'
import { downloadFileApi } from '@/api/file'

/**
 * 全局头像状态（模块级单例）：
 * MainLayout 顶栏、Dashboard 欢迎卡、个人中心共用同一份 ref。
 * 更换头像后调用 refreshAvatar() 即可全站生效，无需重新登录。
 */
const avatarUrl = ref('')

export function useAvatar() {
  /**
   * 从 sessionStorage 的 userInfo 读取头像文件 id 并加载。
   * 静默失败：文件可能已被删除，回退为默认头像。
   */
  const refreshAvatar = async () => {
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
    if (!userInfo.avatar) {
      avatarUrl.value = ''
      return
    }
    try {
      const blob = await downloadFileApi(userInfo.avatar, true)
      avatarUrl.value = URL.createObjectURL(blob)
    } catch {
      avatarUrl.value = ''
    }
  }

  return { avatarUrl, refreshAvatar }
}
