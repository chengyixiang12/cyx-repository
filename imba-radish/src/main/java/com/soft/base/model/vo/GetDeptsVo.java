package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/3 14:33
 **/

@Data
@Schema(description = "获取部门（复）响应参数")
@Alias(value = "GetDeptsVo")
public class GetDeptsVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "父级编码")
    private String parentCode;

    @Schema(description = "父级名称")
    private String parentName;

    @Schema(description = "层级")
    private String level;

    @Schema(description = "排序")
    private Integer sortOrder;
}
