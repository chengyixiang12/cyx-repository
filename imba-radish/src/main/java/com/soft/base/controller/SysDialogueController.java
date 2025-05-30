package com.soft.base.controller;

import com.soft.base.model.request.GetDialoguesRequest;
import com.soft.base.model.request.SaveDialogueRequest;
import com.soft.base.model.vo.GetDialoguesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDialogueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 14:38
 **/

@RestController
@RequestMapping(value = "/dialogue")
@Tag(name = "历史对话")
@Slf4j
public class SysDialogueController {

    private final SysDialogueService sysDialogueService;

    @Autowired
    public SysDialogueController(SysDialogueService sysDialogueService) {
        this.sysDialogueService = sysDialogueService;
    }

    @PostMapping(value = "/getDialogues")
    @Operation(summary = "获取历史对话（复）")
    public R<PageVo<GetDialoguesVo>> getDialogues(@RequestBody GetDialoguesRequest request) {
        try {
            PageVo<GetDialoguesVo> pageVo = sysDialogueService.getDialogues(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PostMapping(value = "/saveDialogue")
    @Operation(summary = "新增对话")
    public R<Long> saveDialogue(@RequestBody SaveDialogueRequest request) {
        try {
            Long id = sysDialogueService.saveDialogue(request);
            return R.ok(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
