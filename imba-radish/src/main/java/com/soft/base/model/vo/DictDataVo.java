package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 13:49
 **/

@Data
@Schema(description = "获取字典数据（单）响应参数")
@Alias(value = "DictDataVo")
public class DictDataVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "键值")
    private String value;

    @Schema(description = "是否默认；1：是 0：否")
    private Integer isDefault;

    @Schema(description = "状态；1：启用；0：停用")
    private Integer status;

    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer sortOrder;
}
