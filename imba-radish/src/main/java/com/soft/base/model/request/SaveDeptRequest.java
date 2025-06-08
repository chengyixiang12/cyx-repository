package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 10:22
 **/

@Data
@Schema(description = "添加部门")
@Alias(value = "SaveDeptRequest")
public class SaveDeptRequest {

    @Schema(description = "部门名称")
    @NotBlank(message = "部门名称不能为空")
    private String name;

    @Schema(description = "部门编码")
    @NotBlank(message = "部门编码不能为空")
    private String code;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "父级id")
    private Long parentId;
}
