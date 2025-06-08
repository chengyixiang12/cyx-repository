package com.soft.base.controller;

import com.soft.base.model.request.GetDialoguesRequest;
import com.soft.base.model.request.SaveDialogueRequest;
import com.soft.base.model.vo.GetDialoguesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDialogueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 14:38
 **/

@RestController
@RequestMapping(value = "/dialogue")
@Tag(name = "历史对话")
@Slf4j
@Validated
public class SysDialogueController {

    private final SysDialogueService sysDialogueService;

    @Autowired
    public SysDialogueController(SysDialogueService sysDialogueService) {
        this.sysDialogueService = sysDialogueService;
    }

    @PostMapping(value = "/getDialogues")
    @Operation(summary = "获取历史对话（复）")
    public R<PageVo<GetDialoguesVo>> getDialogues(@RequestBody GetDialoguesRequest request) {
        PageVo<GetDialoguesVo> pageVo = sysDialogueService.getDialogues(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/saveDialogue")
    @Operation(summary = "新增对话")
    public R<Long> saveDialogue(@RequestBody SaveDialogueRequest request) {
        Long id = sysDialogueService.saveDialogue(request);
        return R.ok(id);
    }

    @DeleteMapping(value = "/deleteDialogue")
    @Operation(summary = "删除对话")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteDialogue(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysDialogueService.deleteDialogue(id);
        return R.ok();
    }
}
