package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cyx
 * @description 文件夹层内容查询请求
 * @date 2026-11-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件夹层内容查询请求")
public class FileListDTO extends PageDTO {

    @Schema(description = "文件夹主键；NULL 或空表示根目录；有关键字时忽略，全局搜索")
    private Long folderId;

    @Schema(description = "关键字（全局搜索文件名）")
    private String keyword;
}
