package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author cyx
 * @description 单条指标的时间序列
 * @date 2026-11-05
 */
@Data
@Schema(description = "指标时间序列")
public class TrendSeriesVO {

    @Schema(description = "指标标识")
    private String key;

    @Schema(description = "指标名称")
    private String name;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数据点")
    private List<TrendPointVO> points;
}
