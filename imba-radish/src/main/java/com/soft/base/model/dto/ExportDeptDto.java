package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/11 11:05
 **/

@Data
@Schema(description = "导出部门传输参数")
@Alias(value = "ExportDeptDto")
public class ExportDeptDto {

    @Schema(description = "部门id")
    private Long id;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级部门名称")
    private String parentName;
}
