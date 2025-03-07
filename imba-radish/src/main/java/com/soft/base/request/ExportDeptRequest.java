package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/12/11 10:54
 **/
@Schema(description = "导出部门请求参数")
@Data
@Alias(value = "ExportDeptRequest")
public class ExportDeptRequest {

    @Schema(description = "部门id数组")
    private List<Long> ids;

    @Schema(description = "文件名")
    private String fileName;
}
