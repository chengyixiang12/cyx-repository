package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/21 22:25
 **/

@Data
@Schema(description = "获取未被赋予的权限响应参数")
@Alias(value = "GetAllPermissionVo")
public class GetAllPermissionVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "权限名称")
    private String name;
}
