export interface GetDialogueHistoriesRequest {
    pageNum: number;
    pageSize: number;
    keyword: string | null;
}

export interface GetDialogueHistoriesVo {
    id: string;
    createBy: string;
    createTime: string;
    content: string;
    title: string;
}

export interface SaveDialogueRequest {
    title: string | null
}

export interface RenameRequest {
    id: string | null;
    title: string;
}

export interface GetTitleVo {
    id: string | null;
    title: string | null;
}