package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/3 14:33
 **/

@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取部门（复）请求参数")
@Alias(value = "GetDeptsRequest")
public class GetDeptsRequest extends PageRequest {

    @Schema(description = "关键字")
    private String keyword;
}
