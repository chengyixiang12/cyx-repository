package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/5 14:28
 **/
@Schema(description = "编辑字典类型")
@Data
@Alias(value = "EditDictTypeRequest")
public class EditDictTypeRequest {

    @Schema(description = "主键")
    @NotNull(message = "主键不能为空")
    private Long id;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "字典类型名称")

    private String dictName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
