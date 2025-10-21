export interface UploadFileVo {
    fileId: number;
    fileName: string;
}

export interface FilesVo {
    id: number;
    location: string;
    fileSize: number;
    originalName: string;
    createTime: string;
    createBy: string;
}

export interface FilesRequest {
    pageNum: number;
    pageSize: number;
    keyword: string;
}