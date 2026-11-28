package com.soft.sys.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author cyx
 * @description 文件夹直属文件计数
 * @date 2026-11-28
 */
@Data
@Schema(description = "文件夹直属文件计数")
public class FolderFileCountDTO {

    @Schema(description = "文件夹主键")
    private Long folderId;

    @Schema(description = "直属文件数量")
    private Long cnt;
}
