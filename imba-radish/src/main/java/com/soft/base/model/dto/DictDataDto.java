package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/10/29
 * @description
 */
@Data
@Alias(value = "DictDataDto")
@Schema(description = "字典数据")
public class DictDataDto {

    @Schema(description = "值")
    private String value;

    @Schema(description = "标签")
    private String label;
}
