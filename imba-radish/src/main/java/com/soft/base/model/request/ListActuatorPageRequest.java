package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description:
 * @date 2026-05-07
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "获取监控记录")
@Data
public class ListActuatorPageRequest extends PageRequest {

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
