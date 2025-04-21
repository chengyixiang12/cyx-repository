package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.PermissionsRequest;
import com.soft.base.model.request.SavePermissionRequest;
import com.soft.base.model.vo.GetAssignPerVo;
import com.soft.base.model.vo.GetAllPermissionVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.PermissionsVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<Object> savePermission(@RequestBody SavePermissionRequest request) {
        if (sysPermissionService.existCode(request.getCode())) {
            R.fail("权限编码已存在");
        }
        try {
            sysPermissionService.savePermission(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getAllPermission")
    @Operation(summary = "获取所有权限")
    public R<List<GetAllPermissionVo>> getAllPermission() {
        try {
            List<GetAllPermissionVo> notAssignPerVos = sysPermissionService.getAllPermission();
            return R.ok(notAssignPerVos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getAssignPer")
    @Operation(summary = "获取被赋予的权限")
    @Parameter(name = "roleId", description = "角色id", required = true, in = ParameterIn.QUERY)
    public R<List<GetAssignPerVo>> getAssignPer(@RequestParam(value = "roleId", required = false) Long roleId) {
        if (roleId == null) {
            return R.fail("角色id不能为空");
        }
        try {
            List<GetAssignPerVo> notAssignPerVos = sysPermissionService.getAssignPer(roleId);
            return R.ok(notAssignPerVos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }


}
