/**
 * 保存用户的请求参数
 */
export interface SaveUserRequest {
    username: string;
    password: string;
    nickname: string;
    deptId: string | null;
    email: string;
    phone: string;
    roleIds: string[];
}

export interface EditUserRequest {
    id: string | null;
    nickname: string | null;
    deptId: string | null;
    email: string | null;
    phone: string | null;
    roleIds: string[] | null;
}

export interface GetUserVo {
    id: string;
    username: string | null;
    nickname: string | null;
    email: string | null;
    phone: string | null;
    deptId: string | null;
    roleIds: string[] | null;
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