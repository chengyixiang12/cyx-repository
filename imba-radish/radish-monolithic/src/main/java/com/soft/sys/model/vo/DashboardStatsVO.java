package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description 首页统计数据
 * @date 2026-11-05
 */
@Data
@Schema(description = "首页统计数据")
public class DashboardStatsVO {

    @Schema(description = "用户总数")
    private Long userCount;

    @Schema(description = "文件总数")
    private Long fileCount;

    @Schema(description = "定时任务总数")
    private Long jobCount;

    @Schema(description = "当前 QPS（最近30秒窗口，来自内存快照）")
    private Double qps;
}
