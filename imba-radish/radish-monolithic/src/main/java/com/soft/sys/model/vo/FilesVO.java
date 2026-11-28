package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 16:46
 **/
@Data
@Schema(description = "获取文件列表响应参数")
public class FilesVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "存储地址；1：minio")
    private Integer location;

    @Schema(description = "存储位置中文名称")
    private String locationName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "源文件名")
    private String originalName;

    @Schema(description = "文件后缀；示例：.txt、.jpg")
    private String fileSuffix;

    @Schema(description = "所属文件夹主键；NULL：根目录")
    private String folderId;

    @Schema(description = "上传时间")
    private LocalDateTime createTime;

    @Schema(description = "上传人")
    private String createBy;
}
