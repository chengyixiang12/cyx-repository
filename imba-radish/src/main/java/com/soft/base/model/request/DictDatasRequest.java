package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 11:41
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "字典数据请求参数")
public class DictDatasRequest extends PageRequest {

    @Schema(description = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "状态；1：启用；0：禁用")
    private Integer status;
}
