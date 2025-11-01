/**
 * 保存用户的请求参数
 */
export interface SaveUserRequest {
    username: string;
    password: string;
    nickname: string;
    deptId: string;
    email: string;
    phone: string;
    roleIds: string[];
}

export interface EditUserRequest {
    id: string | null;
    nickname: string;
    deptId: string;
    email: string;
    phone: string;
    roleIds: string[];
}

export interface GetUserVo {
    id: string;
    username: string;
    nickname: string;
    email: string;
    phone: string;
    deptId: string | null;
    roleIds: string[];
}

export interface AllUserVo {
    id: string;
    username: string;
    phone: string;
    email: string;
    nickname: string;
    enabled: number;
    accountNonLocked: number;
}