export interface GetQuartzRecordListVo {
    id: string | null
    jobName: string | null
    jobType: string | null
    jobGroup: string | null
    executionTime: number | null
    startTime: string | null
    endTime: string | null
    status: string | null
}

export interface GetQuartzRecordListRequest {
    jobId: string | null
    keyword: string | null
    startTime: string | null
    endTime: string | null
    pageNum: number | null
    pageSize: number | null
}