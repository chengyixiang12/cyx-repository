package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/22 17:57
 **/

@Data
@Schema(description = "获取已分配的菜单响应参数")
@Alias(value = "GetAssignedMenuVo")
public class GetAssignedMenuVo {

    @Schema(description = "主键")
    private Long id;
}
