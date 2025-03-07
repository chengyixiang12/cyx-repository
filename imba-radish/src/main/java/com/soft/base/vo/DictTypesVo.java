package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/4 16:01
 **/
@Schema(description = "字典类型")
@Data
@Alias(value = "DictTypesVo")
public class DictTypesVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;
}
