package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/8/17
 * @description
 */
@Data
@Schema(description = "获取定时任务（复）响应参数")
@Alias(value = "GetQuartzTasksVo")
public class GetQuartzTasksVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "cron表达式")
    private String cron;

    @Schema(description = "任务类型")
    private String jobType;

    @Schema(description = "任务状态；1：启用；0：暂停")
    private String status;
}
