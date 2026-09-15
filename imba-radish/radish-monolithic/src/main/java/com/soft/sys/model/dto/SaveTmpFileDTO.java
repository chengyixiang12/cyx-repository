package com.soft.sys.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/2/10 9:34
 **/

@Data
@Schema(description = "保存临时文件请求参数")
public class SaveTmpFileDTO {

    @Schema(description = "字节数组")
    private Byte[] buffer;

    @Schema(description = "文件hash")
    private String hashCode;

    @Schema(description = "文件索引")
    private Integer index;
}
