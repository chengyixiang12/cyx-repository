package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/23 18:11
 **/

@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取字典类型（复）请求参数")
@Alias(value = "GetDictTypesRequest")
public class GetDictTypesRequest extends PageRequest {

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "状态；1：启用；0：禁用")
    private Integer status;
}
