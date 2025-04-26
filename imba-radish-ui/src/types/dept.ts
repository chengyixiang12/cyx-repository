export interface DeptTreeVo {
    id: number,
    code: string,
    name: string,
    parentId: number,
    children: DeptTreeVo[]
}

export interface GetDeptsRequest {
    keyword: string;
    parent: string;
    pageNum: number;
    pageSize: number;
}

export interface GetDeptsVo {
    id: number;
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
    parentId: number;
}

export interface EditDeptRequest {
    id: number;
    sortOrder: number;
    code: string;
    name: string;
    parentId: number;
}

export interface DeptVo {
    id: number;
    sortOrder: number | null;
    code: string;
    name: string;
    parentId: number | null;
}