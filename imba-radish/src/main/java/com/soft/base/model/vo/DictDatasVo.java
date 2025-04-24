package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 11:42
 **/
@Data
@Schema(description = "获取字典数据（复）响应参数")
public class DictDatasVo {

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

    @Schema(description = "排序")
    private Integer sortOrder;
}
