export interface GetQuartzTasksRequest  {
    pageNum: number;
    pageSize: number;
    keyword: string;
    status: string | null;
}

export interface GetQuartzTasksVo {
    id: string;
    jobName: string;
    jobGroup: string;
    cron: string;
    jobType: string;
    status: number;
}

export interface SaveJobRequest {
    jobName: string;
    jobGroup: string;
    cron: string;
    jobType: string;
    status: string;
    jobParam: string;
    startTime: string;
    endTime: string;
    jobInterval: number;
    intervalType: string;
    scheduleType: string;
    remark: string;
    jobClass: string;
}

export interface GetJobVo {
    id: string;
    jobName: string;
    jobGroup: string;
    cron: string;
    jobType: string;
    status: number;
    jobParam: string;
    startTime: string | null;
    endTime: string | null;
    jobInterval: number | null;
    intervalType: string;
    scheduleType: string;
    remark: string;
    jobClass: string;
}

export interface EditJobRequest {
    id: string | null;
    jobName: string;
    jobGroup: string;
    cron: string;
    jobType: string;
    status: string;
    jobParam: string;
    startTime: string | null;
    endTime: string | null;
    jobInterval: number;
    intervalType: string;
    scheduleType: string;
    remark: string;
    jobClass: string;
}