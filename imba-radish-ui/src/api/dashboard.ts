import { post, get } from '@/utils/methods';

export async function fetchMenuList(): Promise<MenuItem[]> {
  return await get<Array<MenuItem>>(
    '/menu/getMenus',
    true,
    null  // 无查询参数
  );
 }