/**
 * 首页数据类型
 */

export interface DashboardStats {
  /** 用户总数 */
  userCount: number
  /** 文件总数 */
  fileCount: number
  /** 定时任务总数 */
  jobCount: number
  /** 当前 QPS（次/秒） */
  qps: number
}

export interface RecentLogin {
  /** 登录时间 */
  loginTime: string
  /** 登录 IP */
  ipAddress: string
  /** 操作系统/浏览器信息 */
  osBrowserInfo: string
}
