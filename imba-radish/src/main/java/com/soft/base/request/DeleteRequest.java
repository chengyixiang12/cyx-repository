package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Data
@Schema(description = "批量删除角色")
@Alias(value = "DeleteRequest")
public class DeleteRequest {

    @Schema(description = "主键")
    private List<Long> ids;
}
