export interface DictDatasRequest {
    pageNum: number;
    pageSize: number;
    parentId: string | null;
    keyword: string;
    status: number | null;
}

export interface DictDatasVo {
    id: string;
    label: string;
    value: number;
    isDefault: number;
    status: number;
    sortOrder: number;
}

export interface SaveDictDataRequest {
    sortOrder: number | null;
    label: string;
    value: string;
    parentId: string | null;
    cssClass: string;
    listClass: string;
    isDefault: number;
    status: number;
    remark: string;
}

export interface EditDictDataRequest {
    id: string | null;
    sortOrder: number | null;
    label: string;
    value: string;
    parentId: string | null;
    cssClass: string;
    listClass: string;
    isDefault: number;
    status: number;
    remark: string;
}

export interface DictDataVo {
    id: string;
    label: string;
    value: string;
    isDefault: number;
    status: number;
    cssClass: string;
    parentId: string;
    listClass: string;
    remark: string;
    sortOrder: number | null;
}