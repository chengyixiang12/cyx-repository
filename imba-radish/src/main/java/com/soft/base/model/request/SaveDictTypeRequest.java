package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/4 21:57
 **/

@Schema(description = "添加字典类型")
@Data
@Alias(value = "SaveDictTypeRequest")
public class SaveDictTypeRequest {

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "字典类型名称")
    @NotBlank(message = "字典类型名称不能为空")
    private String dictName;

    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
