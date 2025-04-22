export interface GetRolesRequest {
    keyword?: string | null;
    status?: number | null;
    pageNum: number;
    pageSize: number;
}

export interface SysRoleVo {
    id: number;
    sortOrder: number | null;
    code: string | null;
    name: string | null;
    description: string | null;
    status: number | null;
    isDefault: number | null;
    fixRole: number | null;
}

export interface SaveRoleRequest {
    sortOrder: number | null;
    code: string | null;
    name: string | null;
    description: string | null;
    status: number | null;
}

export interface EditRoleRequest {
    id: number | null;
    sortOrder: number | null;
    name: string | null;
    description: string | null;
    status: number | null;
}

export interface SysRolesVo {
    id: number | null;
    sortOrder: number | null;
    code: string | null;
    name: string | null;
    description: string | null;
    status: number | null;
    isDefault: number | null;
    fixRole: number | null;
}

export interface SetMenusRequest {
    roleId: number;
    menuIds: number[];
}

export interface GetRoleSelectVo {
    id: number;
    name: string;
}