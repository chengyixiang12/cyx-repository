package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "获取标题响应参数")
@Alias(value = "GetTitleVO")
public class GetTitleVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "标题")
    private String title;
}
