package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 14:04
 **/

@Data
@Schema(description = "添加字典数据请求参数")
public class SaveDictDataRequest {

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "编码")
    @NotBlank(message = "字典编码不能为空")
    private String code;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "值")
    @NotBlank(message = "字典值不能为空")
    private String value;

    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认；1：是 0：否")
    private Integer isDefault;

    @Schema(description = "状态；1：启用；0：停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
