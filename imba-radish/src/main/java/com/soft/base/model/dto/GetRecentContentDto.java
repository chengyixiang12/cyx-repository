package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/12/1
 * @description
 */

@Data
@Schema(description = "获取最新上下文")
@Alias(value = "GetRecentContentDto")
public class GetRecentContentDto {

    @Schema(description = "标识")
    private Integer tag;

    @Schema(description = "上下文")
    private String content;
}
