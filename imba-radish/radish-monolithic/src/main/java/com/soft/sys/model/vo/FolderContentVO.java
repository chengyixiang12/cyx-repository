package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author cyx
 * @description 当前文件夹层内容：子文件夹 + 文件分页 + 面包屑
 * @date 2026-11-28
 */
@Data
@Schema(description = "文件夹层内容")
public class FolderContentVO {

    @Schema(description = "子文件夹列表（直属）")
    private List<FolderVO> folders;

    @Schema(description = "当前层文件分页")
    private PageVO<FilesVO> files;

    @Schema(description = "面包屑路径，根目录 → 当前层")
    private List<FolderVO> breadcrumb;

    @Schema(description = "当前文件夹信息；根目录时为 null")
    private FolderVO currentFolder;
}
