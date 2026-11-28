package com.soft.sys.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/12/11 10:54
 **/
@Schema(description = "导出部门请求参数")
@Data
@Alias(value = "ExportDeptDTO")
public class ExportDeptDTO {

    @Schema(description = "部门id数组，为空时导出全部部门")
    private List<Long> ids;

    @Schema(description = "文件名")
    private String fileName;
}
