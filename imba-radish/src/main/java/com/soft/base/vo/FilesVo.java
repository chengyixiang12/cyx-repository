package com.soft.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 16:46
 **/
@Data
@Schema(description = "获取文件（复）响应参数")
public class FilesVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "存储地址；1：minio；2：磁盘")
    private String location;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "源文件名")
    private String originalName;

    @Schema(description = "上传时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "上传人")
    private String createBy;
}
