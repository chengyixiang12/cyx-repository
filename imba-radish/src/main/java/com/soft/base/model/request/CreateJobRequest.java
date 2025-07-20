package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/7/14 13:47
 **/
@Schema(description = "创建定时任务请求参数")
@Data
public class CreateJobRequest {

    @Schema(description = "任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "cron表达式")
    private String cron;

    @Schema(description = "任务类型")
    @NotBlank(message = "任务类型不能为空")
    private String jobType;

    @Schema(description = "任务所需参数")
    private String jobParam;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "间隔")
    private Integer jobInterval;

    @Schema(description = "间隔类型；0：毫秒；1：秒；2：分钟；3：小时")
    private String intervalType;

    @Schema(description = "调度类型；0：简单调度；1：cron表达式调度")
    @NotBlank(message = "调度类型不能为空")
    private String scheduleType;
}
