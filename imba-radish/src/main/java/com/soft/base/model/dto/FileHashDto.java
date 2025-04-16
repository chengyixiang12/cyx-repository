package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/4/15 17:34
 **/

@Data
@Schema(description = "文件hash传输参数")
@Alias(value = "FileHashDto")
public class FileHashDto {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "源文件名")
    private String originalName;

    @Schema(description = "存储文件名")
    private String fileKey;

    @Schema(description = "存储地址；1：minio；2：磁盘")
    private String location;

    @Schema(description = "文件路径")
    private String objectKey;

    @Schema(description = "文件后缀；示例：.txt、.jpg")
    private String fileSuffix;

    @Schema(description = "文件大小；单位：B")
    private String fileSize;
}
