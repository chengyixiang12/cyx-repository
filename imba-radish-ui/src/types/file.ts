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

export interface ChunkProgressVo {
    /** 校验过长度、可安全续传的分片索引 */
    uploadedIndices: number[];
    uploadedCount?: number;
}