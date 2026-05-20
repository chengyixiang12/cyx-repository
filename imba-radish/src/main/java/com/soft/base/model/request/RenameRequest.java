package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Schema(description = "重命名请求参数")
@Alias(value = "RenameRequest")
public class RenameRequest {

    @NotNull(message = "主键不能为空")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "对话名称")
    private String title;
}
