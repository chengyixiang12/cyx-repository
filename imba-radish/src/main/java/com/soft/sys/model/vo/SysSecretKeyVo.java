package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 密钥列表响应参数
 * @DateTime: 2025/7/15
 **/

@Data
@Schema(description = "密钥列表响应参数")
public class SysSecretKeyVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "密钥类型")
    private String secretType;

    @Schema(description = "公钥（脱敏显示）")
    private String publicKey;

    @Schema(description = "私钥（脱敏显示）")
    private String privateKey;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
