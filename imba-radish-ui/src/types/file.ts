export interface UploadFileVo {
    fileId: string;
    fileName: string;
}

export interface FilesVo {
    id: string;
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

export interface UploadAvatarVo {
    id: string;
    uri: string | null;
}