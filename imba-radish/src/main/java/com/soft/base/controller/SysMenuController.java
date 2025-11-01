package com.soft.base.controller;

import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.EditMenuRequest;
import com.soft.base.model.request.GetMenuListRequest;
import com.soft.base.model.request.SaveMenuRequest;
import com.soft.base.model.vo.*;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/10 13:34
 **/

@RestController
@RequestMapping(value = "/menu")
@Tag(name = "菜单")
@Slf4j
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }


    @GetMapping(value = "/getMenuRoute")
    @Operation(summary = "获取菜单路由")
    public R<List<MenusVo>> getMenuRoute() {
        List<MenusVo> pageVo = sysMenuService.getMenuRoute();
        return R.ok(pageVo);
    }

    @GetMapping(value = "/getSelectMenu")
    @Operation(summary = "获取下拉菜单结构")
    @Parameter(name = "type", description = "类型", required = true, in = ParameterIn.QUERY)
    public R<List<GetSelectMenuVo>> getSelectMenu(@RequestParam(value = "type", required = false) @NotBlank(message = "类型不能为空") String type) {
        List<GetSelectMenuVo> pageVo = sysMenuService.getSelectMenu(type);
        return R.ok(pageVo);
    }

    @SysLog(value = "添加菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_add')")
    @PostMapping
    @Operation(summary = "添加菜单")
    public R<Object> saveMenu(@RequestBody @Valid SaveMenuRequest request) {
        sysMenuService.saveMenu(request);
        return R.ok("添加成功", null);
    }

    @SysLog(value = "编辑菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_edit')")
    @PutMapping
    @Operation(summary = "编辑菜单")
    public R<Object> editMenu(@RequestBody @Valid EditMenuRequest request) {
        sysMenuService.editMenu(request);
        return R.ok("修改成功", null);
    }

    @SysLog(value = "删除菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_del')")
    @DeleteMapping
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteMenu(@RequestParam(value = "id") @NotNull(message = "主键不能为空") Long id) {
        sysMenuService.deleteMenu(id);
        return R.ok("删除成功", null);
    }

    @PostMapping(value = "/getMenuList")
    @Operation(summary = "获取菜单列表")
    public R<PageVo<GetMenuListVo>> getMenuList(@RequestBody GetMenuListRequest request) {
        PageVo<GetMenuListVo> pageVo = sysMenuService.getMenuList(request);
        return R.ok(pageVo);
    }

    @GetMapping(value = "/getMenu")
    @Operation(summary = "获取菜单（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<GetMenuVo> getMenu(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        GetMenuVo getMenuVo = sysMenuService.getMenu(id);
        return R.ok(getMenuVo);
    }

    @SysLog(value = "启用菜单", module = LogModuleEnum.MENU)
    @GetMapping(value = "/enableMenu")
    @Operation(summary = "启用菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enableMenu(@RequestParam(value = "id") @NotNull(message = "主键不能为空") Long id) {
        sysMenuService.enableMenu(id);
        return R.ok("启用成功", null);
    }

    @SysLog(value = "禁用菜单", module = LogModuleEnum.MENU)
    @GetMapping(value = "/disableMenu")
    @Operation(summary = "禁用菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> disableMenu(@RequestParam(value = "id") @NotNull(message = "主键不能为空") Long id) {
        sysMenuService.disableMenu(id);
        return R.ok("禁用成功", null);
    }

    @GetMapping(value = "/getMenuTree")
    @Operation(summary = "获取菜单树")
    public R<List<GetMenuTreeVo>> getMenuTree() {
        List<GetMenuTreeVo> menuTreeVos = sysMenuService.getMenuTree();
        return R.ok(menuTreeVos);
    }

    @GetMapping(value = "/getAssignedMenu")
    @Operation(summary = "获取已分配的菜单")
    @Parameter(name = "roleId", description = "角色id", required = true, in = ParameterIn.QUERY)
    public R<List<GetAssignedMenuVo>> getAssignedMenu(@RequestParam(value = "roleId", required = false) @NotNull(message = "角色id不能为空") Long roleId) {
        List<GetAssignedMenuVo> menuIds = sysMenuService.getAssignedMenu(roleId);
        return R.ok(menuIds);
    }

    @GetMapping(value = "/menuShow")
    @Operation(summary = "显示菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> menuShow(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysMenuService.menuShow(id);
        return R.ok("菜单已显示", null);
    }

    @GetMapping(value = "/menuHide")
    @Operation(summary = "隐藏菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> menuHide(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysMenuService.menuHide(id);
        return R.ok("菜单已隐藏", null);
    }

    @GetMapping(value = "/getLeftMenus")
    @Operation(summary = "获取左侧菜单")
    public R<List<MenusVo>> getLeftMenus() {
        List<MenusVo> menusVos = sysMenuService.getLeftMenus();
        return R.ok(menusVos);
    }
}
