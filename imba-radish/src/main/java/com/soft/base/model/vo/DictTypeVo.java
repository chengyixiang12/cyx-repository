package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/5 14:46
 **/
@Schema(description = "字典类型")
@Data
@Alias(value = "DictTypeVo")
public class DictTypeVo {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "字典类型名称")
    private String dictName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
