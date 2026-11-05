package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cyx
 * @description 组件状态 + 最近的状态变更
 * @date 2026-11-05
 */
@Data
@Schema(description = "组件状态总览")
public class ComponentOverviewVO {

    @Schema(description = "各组件当前状态")
    private List<ComponentStatusVO> components;

    @Schema(description = "最近的状态变更记录")
    private List<HealthChangeVO> changes;

    @Data
    @Schema(description = "组件当前状态")
    public static class ComponentStatusVO {

        @Schema(description = "组件标识")
        private String component;

        @Schema(description = "组件名称")
        private String componentName;

        @Schema(description = "状态；UP/DOWN/DISABLED/UNKNOWN")
        private String status;

        @Schema(description = "状态说明")
        private String detail;

        @Schema(description = "进入当前状态的时间")
        private LocalDateTime sinceTime;

        @Schema(description = "最后探测时间")
        private LocalDateTime lastCheckTime;
    }

    @Data
    @Schema(description = "状态变更记录")
    public static class HealthChangeVO {

        @Schema(description = "组件名称")
        private String componentName;

        @Schema(description = "变化后的状态")
        private String status;

        @Schema(description = "状态说明")
        private String detail;

        @Schema(description = "变化时间")
        private LocalDateTime changeTime;
    }
}
