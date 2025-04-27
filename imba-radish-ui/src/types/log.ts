export interface LogsVo {
    id: number;
    nickname: string;
    moduleName: string;
    requestMethod: string;
    statusCode: number;
    executionTime: number;
    operationDesc: string;
    logLevel: string;
    createTime: string;
}

export interface LogsRequest {
    requestMethod: string;
    keyword: string;
    statusCode: number | null;
    startTime: string;
    endTime: string;
    pageNum: number;
    pageSize: number;
    logLevel: string;
}

export interface GetLogVo {
    
}