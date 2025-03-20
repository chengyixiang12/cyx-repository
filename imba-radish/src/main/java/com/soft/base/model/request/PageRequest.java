package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "分页")
@Alias(value = "PageRequest")
public class PageRequest {

    @Schema(description = "当前页码", example = "1")
    private Long pageNum = 1L;

    @Schema(description = "页面大小", example = "10")
    private Long pageSize = 10L;
}
