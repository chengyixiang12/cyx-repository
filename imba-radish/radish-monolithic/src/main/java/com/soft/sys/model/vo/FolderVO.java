package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author cyx
 * @description 文件夹信息
 * @date 2026-11-28
 */
@Data
@Schema(description = "文件夹信息")
public class FolderVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "文件夹名称")
    private String name;

    @Schema(description = "父文件夹主键；NULL：根目录")
    private String parentId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "直属文件数量")
    private Long fileCount;
}
