package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 13:46
 **/

@Data
@Schema(description = "编辑部门")
@Alias(value = "EditDeptRequest")
public class EditDeptRequest {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级id")
    private Long parentId;
}
