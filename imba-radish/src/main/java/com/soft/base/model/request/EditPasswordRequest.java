package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/25 16:30
 **/
@Data
@Schema(description = "修改密码")
@Alias(value = "EditPasswordRequest")
public class EditPasswordRequest {

    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String originalPass;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String targetPass;
}
