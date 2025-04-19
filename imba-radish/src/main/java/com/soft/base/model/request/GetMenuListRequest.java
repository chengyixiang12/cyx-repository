package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/4 23:09
 **/

@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取菜单列表请求参数")
@Alias(value = "GetMenuListRequest")
public class GetMenuListRequest extends PageRequest{

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "菜单类型")
    private String type;
}
