package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "获取标题响应参数")
@Alias(value = "GetTitleVo")
public class GetTitleVo {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "标题")
    private String title;
}
