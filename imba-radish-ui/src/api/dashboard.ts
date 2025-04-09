import { post, get } from '@/utils/http';
import { MenuItem } from '@/types/menu';

export async function fetchMenuList(): Promise<MenuItem[]> {
  const res = await get<Array<MenuItem>>('/menu/getMenuTree',{ flag: true });
  return res.data
 }