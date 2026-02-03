package com.soft.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/11/1
 * @description
 */
@Data
@Alias(value = "SelectDeletedFileDto")
@Schema(description = "查询已删除的文件")
public class SelectDeletedFileDto {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "桶名")
    private String bucket;

    @Schema(description = "文件存储路径")
    private String objectKey;

    @Schema(description = "是否保留")
    private Boolean retain;
}
