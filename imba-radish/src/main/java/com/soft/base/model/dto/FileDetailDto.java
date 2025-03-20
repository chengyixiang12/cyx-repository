package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 18:41
 **/

@Data
@Schema(description = "文件详情")
@Alias(value = "FileDetailDto")
public class FileDetailDto {

    @Schema(description = "文件路径")
    private String objectKey;

    @Schema(description = "源文件名")
    private String originalName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "存储位置")
    private String location;
}
