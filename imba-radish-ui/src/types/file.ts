export interface UploadFileVo {
    fileId: string;
    fileName: string;
}

export interface FilesVo {
    id: string;
    location: string;
    locationName?: string;
    fileSize: number;
    originalName: string;
    fileSuffix?: string;
    folderId?: string;
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

export interface FolderVo {
    id: string;
    name: string;
    parentId?: string | null;
    createTime?: string;
    /** 直属文件数量 */
    fileCount?: number;
}

export interface FolderTreeNode {
    id: string;
    name: string;
    children?: FolderTreeNode[];
}

export interface FileListRequest {
    /** 文件夹主键；不传表示根目录；有关键字时后端忽略此值做全局搜索 */
    folderId?: string | null;
    keyword?: string;
    pageNum: number;
    pageSize: number;
}

export interface FolderContentVo {
    folders: FolderVo[];
    files: { records: FilesVo[]; total: number };
    /** 面包屑路径，根目录 → 当前层 */
    breadcrumb: FolderVo[];
    currentFolder?: FolderVo | null;
}

export interface GetFileUrlVO {
    url: string;
}