import { post, get } from '@/utils/http';
import { DeptTreeVo } from '@/types/dept';

/**
 * 
 * @returns 获取部门树
 */
export async function getDeptTree(): Promise<DeptTreeVo[]> {
  // 调用method.ts封装的post方法
  // 第二个参数false表示不需要携带认证token
  const deptTreeVo = await get('/dept/getDeptTree', { flag: true });
  return deptTreeVo.data;
}