import { post, get, put, del } from '@/utils/http';
import { DeptTreeVo, DeptVo, EditDeptRequest, GetDeptsRequest, GetDeptsVo, SaveDeptRequest } from '@/types/dept';
import { PaginatedData } from '@/types/api';

/**
 * 
 * @returns 获取部门树
 */
export async function getDeptTree(): Promise<DeptTreeVo[]> {
  const res = await get('/dept/getDeptTree', { flag: true });
  return res.data;
}

/**
 * 获取部门（复）
 * @param data 
 * @returns 
 */
export async function getDeptsApi(data: GetDeptsRequest): Promise<PaginatedData<GetDeptsVo>> {
  const res = await post('/dept/getDepts', data, { flag: true });
  return res.data;
}

/**
 * 删除部门
 * @param id 
 */
export async function deleteDeptApi(id: number) {
  await del(`/dept/${id}`, { flag: true })
}

/**
 * 添加部门
 * @param data 
 */
export async function saveDeptApi(data: SaveDeptRequest) {
  await post('/dept', data, { flag: true })
}

/**
 * 编辑部门
 * @param data 
 */
export async function updateDeptApi(data: EditDeptRequest) {
  await put('/dept', data, { flag: true })
}

/**
 * 获取部门（单）
 * @param id 
 */
export async function getDeptApi(id: number): Promise<DeptVo> {
  const res = await get(`/dept/${id}`, { flag: true })
  return res.data;
}