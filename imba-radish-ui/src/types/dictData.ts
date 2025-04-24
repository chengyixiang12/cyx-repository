export interface DictDatasRequest {
    pageNum: number;
    pageSize: number;
    dictType: string;
    keyword: string;
    status: number | null;
}

export interface DictDatasVo {
    id: number;
    code: string;
    label: string;
    value: number;
    isDefault: number;
    status: number;
    sortOrder: number;
}

export interface SaveDictDataRequest {
    sortOrder: number | null;
    code: string;
    label: string;
    value: string;
    dictType: string;
    cssClass: string;
    listClass: string;
    isDefault: number;
    status: number;
    remark: string;
}

export interface EditDictDataRequest {
    id: number | null;
    sortOrder: number | null;
    code: string;
    label: string;
    value: string;
    dictType: string;
    cssClass: string;
    listClass: string;
    isDefault: number;
    status: number;
    remark: string;
}

export interface DictDataVo {
    id: number;
    code: string;
    label: string;
    value: string;
    isDefault: number;
    status: number;
    cssClass: string;
    dictType: string;
    listClass: string;
    remark: string;
    sortOrder: number;
}