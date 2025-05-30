export interface GetDialogueHistoriesRequest {
    pageNum: number;
    pageSize: number;
    keyword: string | null;
}

export interface GetDialogueHistoriesVo {
    id: number;
    createBy: string;
    createTime: string;
    content: string;
    title: string;
}

export interface SaveDialogueRequest {
    title: string | null
}