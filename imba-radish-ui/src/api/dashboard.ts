import { post, get } from '@/utils/http';
import { MenuItem } from '@/types/menu';

export async function getLeftMenusApi(): Promise<MenuItem[]> {
  const res = await get<Array<MenuItem>>('/menu/getLeftMenus', { flag: true });
  return res.data
}