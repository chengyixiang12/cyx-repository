/**
 * 保存用户的请求参数
 */
export interface SaveUserRequest {
    username: string;
    password: string;
    nickname: string;
    deptId: number;
    email: string;
    phone: string;
    roleIds: number[];
}

export interface EditUserRequest {
    id: number | null;
    nickname: string;
    deptId: number;
    email: string;
    phone: string;
    roleIds: number[];
}

export interface GetUserVo {
    id: number;
    username: string;
    nickname: string;
    email: string;
    phone: string;
    deptId: number;
    roleIds: number[];
}

export interface AllUserVo {
    id: number;
    username: string;
    phone: string;
    email: string;
    nickname: string;
    enabled: number;
    accountNonLocked: number;
}