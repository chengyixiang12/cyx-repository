export interface DeptTreeVo {
    id: string,
    code: string,
    name: string,
    parentId: string | null,
    children: DeptTreeVo[]
}

export interface GetDeptsRequest {
    keyword: string;
    parent: string | null;
    pageNum: number;
    pageSize: number;
}

export interface GetDeptsVo {
    id: string;
    code: number;
    name: number;
    parentCode: number;
    parentName: number;
    level: number;
}

export interface SaveDeptRequest {
    name: string;
    code: string;
    sortOrder: number;
    parentId: string | null;
}

export interface EditDeptRequest {
    id: string | null;
    sortOrder: number;
    code: string;
    name: string;
    parentId: string | null;
}

export interface DeptVo {
    id: string | null;
    sortOrder: number | null;
    code: string;
    name: string;
    parentId: string | null;
}

export interface ExportDeptVo {
    blob: Blob;
    filename: string;
}