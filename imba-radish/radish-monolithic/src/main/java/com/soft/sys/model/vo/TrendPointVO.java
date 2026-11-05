package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cyx
 * @description 趋势点
 * @date 2026-11-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "趋势点")
public class TrendPointVO {

    @Schema(description = "时间戳(毫秒)")
    private Long t;

    @Schema(description = "值")
    private Double v;
}
