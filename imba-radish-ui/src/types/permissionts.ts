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