export interface GetRolesRequest {
    keyword: string;
    status: number | null;
    pageNum: number;
    pageSize: number;
}

export interface SysRoleVo {
    id: string;
    sortOrder: number | null;
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
    id: string | null;
    sortOrder: number;
    name: string;
    description: string;
    status: number;
}

export interface SysRolesVo {
    id: string;
    sortOrder: number;
    code: string;
    name: string;
    description: string;
    status: number;
    isDefault: number;
    fixRole: number;
}

export interface SetMenusRequest {
    roleId: string | null;
    menuIds: string[];
}

export interface GetRoleSelectVo {
    id: string;
    name: string;
}