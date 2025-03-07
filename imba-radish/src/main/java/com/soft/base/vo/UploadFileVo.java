package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/2/8 11:18
 **/

@Data
@Schema(description = "上传文件响应参数")
public class UploadFileVo {

    @Schema(description = "文件id")
    private Long fileId;

    @Schema(description = "文件名")
    private String fileName;
}
