export interface GetAllPermissionVo {
    id: string,
    name: string
}

export interface GetAssignPerVo {
    id: string,
    name: string
}

export interface SetPermissionsRequest {
    roleId: string | null,
    permissionIds: string[]
}

export interface PermissionsVo {
    id: string,
    code: string | null,
    name: string | null,
    type: string | null,
    status: string | null,
    description: string | null
}

export interface PermissionsRequest {
    keyword: string | null,
    pageNum: number,
    pageSize: number,
    status: string | null,
    type: string | null
}

export interface SavePermissionRequest {
    name: string,
    code: string,
    type: string,
    status: string,
    description: string | null
}

export interface EditPermissionRequest {
    id: string | null,
    name: string,
    code: string,
    type: string,
    status: number,
    description: string | null

}

export interface GetPermissionVo {
    id: string,
    name: string,
    code: string,
    type: string,
    status: string,
    description: string | null
}