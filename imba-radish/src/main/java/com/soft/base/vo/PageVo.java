package com.soft.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Schema(description = "分页")
@Data
@Alias(value = "PageVo")
public class PageVo<T> {

    @Schema(description = "响应结果")
    private List<T> result;

    @Schema(description = "总数", example = "10")
    private Long total;
}
