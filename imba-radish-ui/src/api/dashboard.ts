import { post, get } from '@/utils/http';
import { MenuItem } from '@/types/menu';
import { DashboardStats, RecentLogin } from '@/types/dashboard';

export async function getLeftMenusApi(): Promise<MenuItem[]> {
  const res = await get<Array<MenuItem>>('/menu/getLeftMenus', { flag: true });
  return res.data
}

/**
 * 首页统计数据：用户/文件/定时任务总数 + 当前QPS
 */
export async function getDashboardStatsApi(): Promise<DashboardStats> {
  const res = await get<DashboardStats>('/dashboard/stats', { flag: true });
  return res.data;
}

/**
 * 当前用户最近5次登录记录
 */
export async function getRecentLoginsApi(): Promise<RecentLogin[]> {
  const res = await get<RecentLogin[]>('/dashboard/recentLogins', { flag: true });
  return res.data || [];
}