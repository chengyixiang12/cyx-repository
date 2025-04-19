package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Operation(summary = "获取当前用户的菜单")
    public R<List<MenusVo>> getMenuRoute() {
        try {
            List<MenusVo> pageVo = sysMenuService.getMenuRoute();
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getSelectMenu")
    @Operation(summary = "获取下拉菜单结构")
    @Parameter(name = "type", description = "类型", required = true, in = ParameterIn.QUERY)
    public R<List<GetSelectMenuVo>> getSelectMenu(@RequestParam(value = "type", required = false) String type) {
        try {
            List<GetSelectMenuVo> pageVo = sysMenuService.getSelectMenu(type);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "添加菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_add')")
    @PostMapping
    @Operation(summary = "添加菜单")
    public R<Object> saveMenu(@RequestBody SaveMenuRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            return R.fail("菜单名称不能为空");
        }
        if (StringUtils.isBlank(request.getPath())) {
            return R.fail("菜单路径不能为空");
        }
        if (StringUtils.isBlank(request.getType())) {
            return R.fail("菜单类型不能为空");
        }
        try {
            sysMenuService.saveMenu(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "编辑菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_edit')")
    @PutMapping
    @Operation(summary = "编辑菜单")
    public R<Object> editMenu(@RequestBody EditMenuRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能不能为空");
        }
        if (StringUtils.isBlank(request.getName())) {
            return R.fail("菜单名称不能为空");
        }
        if (StringUtils.isBlank(request.getPath())) {
            return R.fail("菜单路径不能为空");
        }
        if (StringUtils.isBlank(request.getType())) {
            return R.fail("菜单类型不能为空");
        }
        try {
            sysMenuService.editMenu(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "删除菜单", module = LogModuleEnum.MENU)
    @PreAuthorize(value = "@cps.hasPermission('sys_menu_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> deleteMenu(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysMenuService.deleteMenu(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PostMapping(value = "/getMenuList")
    @Operation(summary = "获取菜单列表")
    public R<PageVo<GetMenuListVo>> getMenuList(@RequestBody GetMenuListRequest request) {
        try {
            PageVo<GetMenuListVo> pageVo = sysMenuService.getMenuList(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getMenu")
    @Operation(summary = "获取菜单（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<GetMenuVo> getMenu(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return R.fail("id不能为空");
        }
        try {
            GetMenuVo getMenuVo = sysMenuService.getMenu(id);
            return R.ok(getMenuVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/enableMenu/{id}")
    @Operation(summary = "启用菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> enableMenu(@PathVariable(value = "id") Integer id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysMenuService.enableMenu(id);
            return R.ok("启用成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/disableMenu/{id}")
    @Operation(summary = "禁用菜单")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> disableMenu(@PathVariable(value = "id") Integer id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysMenuService.disableMenu(id);
            return R.ok("禁用成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
