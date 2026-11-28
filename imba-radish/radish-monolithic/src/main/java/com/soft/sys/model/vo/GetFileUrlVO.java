package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author cyx
 * @date 2026-11-28
 */
@Data
@Schema(description = "文件url")
public class GetFileUrlVO {

    @Schema(description = "url")
    private String url;
}
