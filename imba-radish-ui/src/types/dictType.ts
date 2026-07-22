export interface GetDictTypesRequest {
    pageNum: number;
    pageSize: number;
    keyword: string;
    status: number | null;
}

export interface DictTypesVo {
    id: string;
    dictName: string;
    dictType: string;
    status: number;
    sortOrder: number;
}

export interface DictTypeVo {
    id: string;
    dictName: string;
    dictType: string;
    status: number;
    remark: string;
    sort: number | null;
}

export interface SaveDictTypeRequest {
    dictName: string;
    dictType: string;
    sort: number | null;
    status: number;
    remark: string;
}

export interface EditDictTypeRequest {
    id: string | null;
    sort: number | null;
    dictName: string;
    dictType: string;
    status: number;
    remark: string;
}