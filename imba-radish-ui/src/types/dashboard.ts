/**
 * 首页数据类型（全部为当前登录人的个人数据）
 */

export interface RecentLogin {
  /** 登录时间 */
  loginTime: string
  /** 登录 IP */
  ipAddress: string
  /** 操作系统/浏览器信息 */
  osBrowserInfo: string
}

export interface RecentDialogue {
  /** 对话 ID */
  id: number
  /** 对话标题 */
  title: string
  /** 创建时间 */
  createTime: string
}
