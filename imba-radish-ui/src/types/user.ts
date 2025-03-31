
// 用户列表
export interface User {
    id: number;
    username: string;
    phone: string;
    email: string;
    nickname: string;
    enabled: number;
    deptId: number;
}

// 分页
export interface PageVo {
    result: Array<User>;
    total: number;
}