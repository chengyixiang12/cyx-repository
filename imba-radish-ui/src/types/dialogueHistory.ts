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

/** 分页获取对话明细(倒序分页: 第1页为最新消息) */
export interface GetDialogueDetailsRequest {
    dialogueId: string;
    pageNum: number;
    pageSize: number;
}

export interface DialogueDetailsVo {
    id: string;
    /** 1: 用户; 0: AI */
    tag: number;
    content: string;
    createTime: string;
}