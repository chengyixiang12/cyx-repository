export interface GetDictTypesRequest {
    pageNum: number;
    pageSize: number;
    keyword: string;
    status: number | null;
}

export interface DictTypesVo {
    id: number;
    dictName: string;
    dictType: string;
    status: number;
    sortOrder: number;
}

export interface DictTypeVo {
    id: number;
    dictName: string;
    dictType: string;
    status: number;
    remark: string;
    sortOrder: number | null;
}

export interface SaveDictTypeRequest {
    dictName: string;
    dictType: string;
    sortOrder: number | null;
    status: number;
    remark: string;
}

export interface EditDictTypeRequest {
    id: number | null;
    sortOrder: number | null;
    dictName: string;
    dictType: string;
    status: number;
    remark: string;
}