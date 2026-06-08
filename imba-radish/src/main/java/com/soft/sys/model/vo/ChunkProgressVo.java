package com.soft.sys.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 分片上传进度响应参数
 * @DateTime: 2026/6/8
 **/

@Data
@Schema(description = "分片上传进度响应参数")
public class ChunkProgressVo {

    @Schema(description = "已上传的分片索引列表")
    private List<Integer> uploadedIndices;
}
