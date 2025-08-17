package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/8/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "获取定时任务（复）请求参数")
@Alias(value = "GetQuartzTasksRequest")
public class GetQuartzTasksRequest extends PageRequest {

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "状态")
    private String status;
}
