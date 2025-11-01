package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/18 16:41
 **/

@Data
@Schema(description = "获取用户（复）传输参数")
@Alias(value = "GetUsersDto")
public class GetUsersDto {

    @Schema(description = "部门ids")
    private List<Long> deptIds;

    @Schema(description = "关键字检索")
    private String nameLikeQry;

    @Schema(description = "用户是否被启用；1：启用；0：禁用")
    private Integer enabled;

    @Schema(description = "账户是否被锁定；1：正常；0：锁定")
    private Integer accountNonLocked;
}
