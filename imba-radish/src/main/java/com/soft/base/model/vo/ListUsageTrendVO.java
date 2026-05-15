package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description:
 * @date 2026-05-15
 */
@Data
@Schema(description = "获取使用率趋势")
public class ListUsageTrendVO {

    @Schema(description = "使用率")
    private Double usageRate;

    @Schema(description = "时间")
    private LocalDateTime createTime;
}
