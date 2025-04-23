export interface GetAllPermissionVo {
    id: number,
    name: string
}

export interface GetAssignPerVo {
    id: number,
    name: string
}

export interface SetPermissionsRequest {
    roleId: number | null,
    permissionIds: number[]
}