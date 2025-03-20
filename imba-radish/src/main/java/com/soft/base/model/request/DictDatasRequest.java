package com.soft.base.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 11:41
 **/
@EqualsAndHashCode(callSuper = false)
@Data
@Schema(description = "字典数据请求参数")
public class DictDatasRequest extends PageRequest {

    @Schema(description = "字典类型")
    private String dictType;
}
