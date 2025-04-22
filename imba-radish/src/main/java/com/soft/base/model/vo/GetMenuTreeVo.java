package com.soft.base.model.vo;

import com.soft.base.model.ctf.MenuTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/22 10:07
 **/

@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取菜单树响应参数")
@Alias(value = "GetMenuTreeVo")
public class GetMenuTreeVo implements MenuTree<GetMenuTreeVo> {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "上级菜单")
    private Long parentId;

    @Schema(description = "子集")
    private List<GetMenuTreeVo> children = new ArrayList<>();
}
