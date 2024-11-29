package com.soft.base.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 16:45
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "获取文件（复）请求参数")
public class FilesRequest extends PageRequest {
}
