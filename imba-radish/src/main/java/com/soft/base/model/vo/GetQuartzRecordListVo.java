package com.soft.base.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Data
@Schema(description = "获取定时任务执行记录列表的相应参数")
@Alias(value = "GetQuartzRecordListVo")
public class GetQuartzRecordListVo {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务类型")
    private String jobType;

    @Schema(description = "任务组")
    private String jobGroup;

    @Schema(description = "耗时")
    private Long executionTime;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @Schema(description = "执行状态；0：未执行；1：执行中；2：执行完成；3：执行失败")
    private String status;
}
