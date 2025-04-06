import { post, get } from '@/utils/http';

export async function fetchMenuList(): Promise<MenuItem[]> {
  const menus = await get<Array<MenuItem>>('/menu/getMenuTree',{ flag: true });
  return menus.data
 }