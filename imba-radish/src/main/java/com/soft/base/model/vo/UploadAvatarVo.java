package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/29 18:23
 **/

@Data
@Schema(description = "上传用户头像响应参数")
@Alias(value = "UploadAvatarVo")
public class UploadAvatarVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "上传头像uri")
    private String uri;
}
