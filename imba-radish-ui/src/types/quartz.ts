export interface GetQuartzTasksRequest  {
    pageNum: number;
    pageSize: number;
    keyword: string;
    status: string | null;
}

export interface GetQuartzTasksVo {
    id: number;
    jobName: string;
    jobGroup: string;
    cron: string;
    jobType: string;
    status: string;
}