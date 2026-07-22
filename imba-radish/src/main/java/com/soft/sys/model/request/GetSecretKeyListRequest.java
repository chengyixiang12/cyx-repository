package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: 获取密钥列表请求参数
 * @DateTime: 2025/7/15
 **/

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取密钥列表请求参数")
public class GetSecretKeyListRequest extends PageRequest {

    @Schema(description = "密钥类型")
    private Integer type;
}
