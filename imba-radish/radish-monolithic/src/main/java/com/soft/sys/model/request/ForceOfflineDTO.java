package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 强制下线请求参数
 * @DateTime: 2026/11/05
 **/
@Data
@Schema(description = "强制下线请求参数")
public class ForceOfflineDTO {

    @NotNull(message = "用户id不能为空")
    @Schema(description = "目标用户id")
    private Long id;

    @Schema(description = "设备类型：web / app / mini-program，为空表示所有设备下线")
    private String deviceType;
}
