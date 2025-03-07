package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.request.PermissionsRequest;
import com.soft.base.request.SavePermissionRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysPermissionService;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.PermissionsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 14:40
 **/

@RestController
@RequestMapping(value = "/permission")
@Slf4j
@Tag(name = "权限")
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @Autowired
    public SysPermissionController(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

    @PostMapping(value = "/getPermissions")
    @Operation(summary = "获取权限（复）")
    public R<PageVo<PermissionsVo>> getPermissions(@RequestBody PermissionsRequest request) {
        try {
            PageVo<PermissionsVo> pageVo = sysPermissionService.getPermissions(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "添加权限", module = LogModuleEnum.PERMISSION)
    @PreAuthorize(value = "@cps.hasPermission('sys_pms_add')")
    @PostMapping
    @Operation(summary = "添加权限")
    public R savePermission(@RequestBody SavePermissionRequest request) {
        try {
            sysPermissionService.savePermission(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
