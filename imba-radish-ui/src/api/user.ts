import {get, post} from '@/utils/methods';
import { PageVo } from '@/types/user';

export function getUserList(data: any) {
   return post<PageVo>('/user/getAllUsers', true, data);
}