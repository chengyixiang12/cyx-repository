package com.soft.base.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取角色（复）")
@Alias(value = "GetRolesRequest")
public class GetRolesRequest extends PageRequest {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
