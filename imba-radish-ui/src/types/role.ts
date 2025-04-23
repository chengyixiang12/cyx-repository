export interface GetRolesRequest {
    keyword: string;
    status: number | null;
    pageNum: number;
    pageSize: number;
}

export interface SysRoleVo {
    id: number;
    sortOrder: number;
    code: string;
    name: string;
    description: string;
    status: number;
    isDefault: number;
    fixRole: number;
}

export interface SaveRoleRequest {
    sortOrder: number;
    code: string;
    name: string;
    description: string;
    status: number;
}

export interface EditRoleRequest {
    id: number | null;
    sortOrder: number;
    name: string;
    description: string;
    status: number;
}

export interface SysRolesVo {
    id: number;
    sortOrder: number;
    code: string;
    name: string;
    description: string;
    status: number;
    isDefault: number;
    fixRole: number;
}

export interface SetMenusRequest {
    roleId: number | null;
    menuIds: number[];
}

export interface GetRoleSelectVo {
    id: number;
    name: string;
}