package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Data
@Schema(description = "批量删除角色")
@Alias(value = "DeleteRequest")
public class DeleteRequest {

    @Schema(description = "主键")
    @NotEmpty(message = "ids不能为空")
    private List<@NotNull(message = "ids不能为空") Long> ids;
}
