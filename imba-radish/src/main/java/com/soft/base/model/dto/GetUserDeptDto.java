package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/1 19:01
 **/

@Data
@Schema(description = "获取用户部门传输参数")
public class GetUserDeptDto {

    @Schema(description = "主键")
    private Long value;

    @Schema(description = "名称")
    private String label;
}
