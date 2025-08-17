package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * @author cyq
 * @date 2025/8/17
 * @description
 */
@Data
@Schema(description = "获取定时任务（复）响应参数")
@Alias(value = "GetQuartzTasksVo")
public class GetQuartzTasksVo {
}
