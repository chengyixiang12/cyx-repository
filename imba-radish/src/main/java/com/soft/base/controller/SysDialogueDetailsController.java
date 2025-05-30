package com.soft.base.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/30 16:11
 **/

@RestController
@RequestMapping(value = "/dialogueDetails")
@Tag(name = "对话明细")
@Slf4j
public class SysDialogueDetailsController {
}
