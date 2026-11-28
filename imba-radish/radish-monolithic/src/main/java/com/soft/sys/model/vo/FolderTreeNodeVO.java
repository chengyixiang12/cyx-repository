package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文件夹树节点
 *
 * @author cyx
 * @date 2026-11-28
 */
@Data
@Schema(description = "文件夹树节点")
public class FolderTreeNodeVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "文件夹名称")
    private String name;

    @Schema(description = "子文件夹")
    private List<FolderTreeNodeVO> children;
}
