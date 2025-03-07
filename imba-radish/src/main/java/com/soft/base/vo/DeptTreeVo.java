package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 10:10
 **/

@Data
@Schema(description = "组织架构")
@Alias(value = "DeptTreeVo")
public class DeptTreeVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "父级id", hidden = true)
    private Long parentId;

    @Schema(description = "子节点")
    private List<DeptTreeVo> children = new ArrayList<>();

    @Schema(description = "用户", hidden = true)
    private List<DeptUserVo> users = new ArrayList<>();
}
