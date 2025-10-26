package com.soft.base.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author cyq
 * @date 2025/10/26
 * @description
 */
@Data
@Schema(description = "修改job")
@Alias(value = "EditJobRequest")
public class EditJobRequest {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    @NotBlank(message = "任务组不能为空")
    private String jobGroup;

    @Schema(description = "cron表达式")
    private String cron;

    @Schema(description = "任务类型")
    @NotBlank(message = "任务类型不能为空")
    private String jobType;

    @Schema(description = "任务状态；1：启用；0：暂停")
    private Integer status;

    @Schema(description = "任务参数")
    private String jobParam;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "间隔")
    private Integer jobInterval;

    @Schema(description = "间隔类型；0：毫秒；1：秒；2：分钟；3：小时")
    private String intervalType;

    @Schema(description = "调度类型；0：简单调度；1：cron表达式调度")
    private String scheduleType;

    @Schema(description = "备注")
    private String remark;
}
