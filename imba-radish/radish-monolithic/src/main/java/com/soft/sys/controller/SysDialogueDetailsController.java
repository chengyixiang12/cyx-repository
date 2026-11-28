package com.soft.sys.controller;

import com.soft.common.resultapi.R;
import com.soft.sys.model.request.GetDialogueDetailsDTO;
import com.soft.sys.model.vo.DialogueDetailsVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysDialogueDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2025/5/30 16:11
 **/

@RestController
@RequestMapping(value = "/dialogueDetails")
@Tag(name = "对话明细")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SysDialogueDetailsController {

    private final SysDialogueDetailsService sysDialogueDetailsService;

    @PostMapping(value = "/getDetails")
    @Operation(summary = "分页获取对话明细(倒序, 用于向上懒加载历史消息)")
    public R<PageVO<DialogueDetailsVO>> getDetails(@RequestBody @Valid GetDialogueDetailsDTO request) {
        return R.ok(sysDialogueDetailsService.getDetails(request));
    }
}
