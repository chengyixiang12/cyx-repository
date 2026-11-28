import { get } from '@/utils/http';
import { MenuItem } from '@/types/menu';
import { RecentDialogue, RecentLogin } from '@/types/dashboard';

export async function getLeftMenusApi(): Promise<MenuItem[]> {
  const res = await get<Array<MenuItem>>('/menu/getLeftMenus', { flag: true });
  return res.data
}

/**
 * 当前用户最近5次登录记录（个人维度）
 */
export async function getRecentLoginsApi(): Promise<RecentLogin[]> {
  const res = await get<RecentLogin[]>('/dashboard/recentLogins', { flag: true });
  return res.data || [];
}

/**
 * 当前用户最近5条对话（后端强制按登录人过滤）
 */
export async function getRecentDialoguesApi(): Promise<RecentDialogue[]> {
  const res = await get<RecentDialogue[]>('/dashboard/recentDialogues', { flag: true });
  return res.data || [];
}
