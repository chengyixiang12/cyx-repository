package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/5 14:01
 **/
@Schema(description = "部门")
@Data
@Alias(value = "DeptVo")
public class DeptVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "父级主键")
    private Long parentId;

    @Schema(description = "父级名称")
    private String parentName;
}
