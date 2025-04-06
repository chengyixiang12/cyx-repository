package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/1 16:34
 **/

@EqualsAndHashCode(callSuper = false)
@Schema(description = "获取所有用户请求参数")
@Data
public class GetAllUsersRequest extends PageRequest {

    @Schema(description = "部门id")
    private Long deptId;
}
