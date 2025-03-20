package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.EditMenuRequest;
import com.soft.base.model.request.SaveMenuRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysMenuService;
import com.soft.base.model.vo.MenusVo;
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


    @GetMapping(value = "/getMenus")
    @Operation(summary = "获取菜单")
    public R<List<MenusVo>> getMenus() {
        try {
            List<MenusVo> pageVo = sysMenuService.getMenus();
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
    public R saveMenu(@RequestBody SaveMenuRequest request) {
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
    public R editMenu(@RequestBody EditMenuRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能不能为空");
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
    public R deleteMenu(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
