package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取定时任务执行记录列表的请求参数")
@Alias(value = "GetQuartzRecordListRequest")
public class GetQuartzRecordListRequest extends PageRequest {

    @Schema(description = "定时任务id")
    @NotBlank(message = "定时任务id不能为空")
    private String jobId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
