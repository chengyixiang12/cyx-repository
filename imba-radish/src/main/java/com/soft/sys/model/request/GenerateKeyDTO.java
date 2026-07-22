package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 *
 * @author cyx
 * @date 2026-07-20
 */
@Data
@Schema(description = "生成密钥")
public class GenerateKeyDTO {

    @NotBlank(message = "类型不能为空")
    @Schema(description = "类型")
    private Integer type;

    @NotBlank(message = "密钥类型不能为空")
    @Schema(description = "密钥类型")
    private String secretType;

    @Schema(description = "描述")
    private String description;
}
