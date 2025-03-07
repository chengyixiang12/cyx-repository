package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String originalPass;

    @Schema(description = "新密码")
    private String targetPass;
}
