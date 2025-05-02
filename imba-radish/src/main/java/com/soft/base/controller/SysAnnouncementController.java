package com.soft.base.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2025/5/3 0:25
 **/

@RequestMapping(value = "/announcement")
@RestController
@Tag(name = "公告")
public class SysAnnouncementController {
}
