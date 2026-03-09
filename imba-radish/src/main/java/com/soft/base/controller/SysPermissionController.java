package com.soft.base.controller;

import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.EditPermissionRequest;
import com.soft.base.model.request.PermissionsRequest;
import com.soft.base.model.request.SavePermissionRequest;
import com.soft.base.model.vo.*;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 14:40
 **/

@RestController
@RequestMapping(value = "/permission")
@Slf4j
@Tag(name = "权限")
@Validated
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @Autowired
    public SysPermissionController(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

    @PostMapping(value = "/getPermissions")
    @Operation(summary = "获取权限（复）")
    public R<PageVo<PermissionsVo>> getPermissions(@RequestBody PermissionsRequest request) {
        PageVo<PermissionsVo> pageVo = sysPermissionService.getPermissions(request);
        return R.ok(pageVo);
    }

    @SysLog(value = "添加权限", module = LogModuleEnum.PERMISSION)
    @PreAuthorize(value = "@cps.hasPermission('sys_pms_add')")
    @PostMapping
    @Operation(summary = "添加权限")
    public R<Object> savePermission(@RequestBody SavePermissionRequest request) {
        if (sysPermissionService.existCode(request.getCode())) {
            R.fail("权限编码已存在");
        }
        sysPermissionService.savePermission(request);
        return R.ok("权限添加成功", null);
    }

    @PutMapping(value = "/editPermission")
    @Operation(summary = "编辑权限")
    public R<Object> editPermission(@RequestBody EditPermissionRequest request) {
        if (sysPermissionService.existCode(request.getCode())) {
            R.fail("权限编码已存在");
        }
        sysPermissionService.editPermission(request);
        return R.ok("权限编辑成功", null);
    }

    @GetMapping(value = "/getAllPermission")
    @Operation(summary = "获取所有权限")
    public R<List<GetAllPermissionVo>> getAllPermission() {
        List<GetAllPermissionVo> notAssignPerVos = sysPermissionService.getAllPermission();
        return R.ok(notAssignPerVos);
    }

    @GetMapping(value = "/getAssignPer")
    @Operation(summary = "获取被赋予的权限")
    @Parameter(name = "roleId", description = "角色id", required = true, in = ParameterIn.QUERY)
    public R<List<GetAssignPerVo>> getAssignPer(@RequestParam(value = "roleId", required = false) @NotNull(message = "角色id不能为空") Long roleId) {
        List<GetAssignPerVo> notAssignPerVos = sysPermissionService.getAssignPer(roleId);
        return R.ok(notAssignPerVos);
    }

    @DeleteMapping
    @Operation(summary = "删除权限")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deletePermission(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysPermissionService.deletePermission(id);
        return R.ok("删除成功", null);
    }

    @GetMapping(value = "/enablePermission")
    @Operation(summary = "启用权限")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enablePermission(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysPermissionService.enablePermission(id);
        return R.ok("启用成功", null);
    }

    @GetMapping(value = "/forbiddenPermission")
    @Operation(summary = "禁用权限")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> forbiddenPermission(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysPermissionService.forbiddenPermission(id);
        return R.ok("禁用成功", null);
    }

    @GetMapping(value = "/getPermission")
    @Operation(summary = "获取权限")
    public R<GetPermissionVo> getPermission(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        GetPermissionVo getPermissionVo =  sysPermissionService.getPermission(id);
        return R.ok(getPermissionVo);
    }
}
