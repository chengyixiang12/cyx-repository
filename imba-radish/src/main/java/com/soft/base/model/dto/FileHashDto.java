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
}
