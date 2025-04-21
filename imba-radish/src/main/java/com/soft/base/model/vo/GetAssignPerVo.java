package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/21 22:31
 **/

@Data
@Schema(description = "获取被赋予的权限响应参数")
@Alias(value = "GetAssignPerVo")
public class GetAssignPerVo {

    @Schema(description = "权限id")
    private Long id;

    @Schema(description = "权限名称")
    private String name;
}
