package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RegexConstant;
import com.soft.base.dto.FixRolesDto;
import com.soft.base.entity.SysRole;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.request.*;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysRoleService;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.SysRoleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/role")
@Tag(name = "角色接口")
@Slf4j
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @SysLog(value = "添加角色", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_add')")
    @PostMapping
    @Operation(summary = "添加角色")
    public R saveRole(@RequestBody SaveRoleRequest request) {
        if (StringUtils.isBlank(request.getCode())) {
            return R.fail("角色编码不能为空");
        }
        if (!Pattern.matches(RegexConstant.ROLE_CODE_HEADER, request.getCode())) {
            return R.fail("无效的角色编码");
        }
        if (request.getStatus() == null) {
            request.setStatus(BaseConstant.DEF_STATUS);
        }
        try {
            Boolean existCode = sysRoleService.existCode(request.getCode());
            if (existCode) {
                return R.fail("编码：" + request.getCode() + "已存在");
            }
            SysRole sysRole = new SysRole();
            BeanUtils.copyProperties(request, sysRole);
            sysRole.setIsDefault(BaseConstant.UN_DEFAULT_ROLE_FLAG);
            sysRole.setFixRole(BaseConstant.UN_FIX_ROLE_FLAG);
            sysRoleService.save(sysRole);
            return R.ok("新增成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "编辑角色", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_edit')")
    @PutMapping
    @Operation(summary = "编辑角色")
    public R editRole(@RequestBody EditRoleRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能为空");
        }
        try {
            SysRole sysRole = new SysRole();
            BeanUtils.copyProperties(request, sysRole);
            sysRoleService.updateById(sysRole);
            return R.ok("编辑成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "删除角色", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R deleteRole(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            Boolean fixRoleFlag = sysRoleService.fixRoleFlag(id);
            if (fixRoleFlag) {
                return R.fail("固定角色不可删除");
            }
            sysRoleService.removeById(id);
            return R.ok("删除成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "批量删除角色", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_del')")
    @DeleteMapping(value = "/deleteRoleBatch")
    @Operation(summary = "批量删除角色")
    public R deleteRoleBatch(@RequestBody DeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return R.fail("主键不能为空");
        }
        try {
            List<FixRolesDto> fixRolesFlag = sysRoleService.fixRolesFlag(request.getIds());
            if (!fixRolesFlag.isEmpty()) {
                StringBuilder message = new StringBuilder();
                message.append(BaseConstant.LEFT_SQUARE_BRACKET);
                Iterator<FixRolesDto> iterator = fixRolesFlag.iterator();
                while (iterator.hasNext()) {
                    FixRolesDto next = iterator.next();
                    if (next.getFixRole().equals(BaseConstant.FIX_ROLE_FLAG)
                            || next.getIsDefault().equals(BaseConstant.DEFAULT_ROLE_FLAG)) {
                        message.append(next.getName());
                    }
                    if (iterator.hasNext()) {
                        message.append(",");
                    }
                }
                message.append(BaseConstant.RIGHT_SQUARE_BRACKET);
                message.append("不可被删除");
                return R.fail(message.toString());
            }

            sysRoleService.deleteRoleBatch(request);
            return R.ok("删除成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "获取角色（单）", module = LogModuleEnum.ROLE)
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取角色（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<SysRoleVo> getRole(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            SysRoleVo sysRoleVo = sysRoleService.getRole(id);
            return R.ok(sysRoleVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "获取角色（复）", module = LogModuleEnum.ROLE)
    @PostMapping(value = "/getRoles")
    @Operation(summary = "获取角色（复）")
    public R<PageVo<SysRoleVo>> getRoles(@RequestBody GetRolesRequest request) {
        try {
            PageVo<SysRoleVo> resultPage = sysRoleService.getRoles(request);
            return R.ok(resultPage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "启用", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_enable')")
    @GetMapping(value = "/enableRole/{id}")
    @Operation(summary = "启用")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R enableRole(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysRoleService.enableRole(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "禁用", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_fbn')")
    @GetMapping(value = "/forbiddenRole/{id}")
    @Operation(summary = "禁用")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R forbiddenRole(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysRoleService.forbiddenRole(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "设置默认角色", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_set_def')")
    @GetMapping(value = "/setDefaultRole/{id}")
    @Operation(summary = "设置默认角色")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R setDefaultRole(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysRoleService.setDefaultRole(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "赋予菜单", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_set_menu')")
    @PostMapping(value = "/setMenus")
    @Operation(summary = "赋予菜单")
    public R setMenus(@RequestBody SetMenusRequest request) {
        if (request.getRoleId() == null) {
            return R.fail("角色主键不能为空");
        }
        if (request.getMenuIds() == null || request.getMenuIds().isEmpty()) {
            return R.fail("菜单主键不能为空");
        }
        try {
            sysRoleService.setMenus(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "赋予权限", module = LogModuleEnum.ROLE)
    @PreAuthorize(value = "@cps.hasPermission('sys_role_set_per')")
    @PostMapping(value = "/setPermissions")
    @Operation(summary = "赋予权限")
    public R setPermissions(@RequestBody SetPermissionsRequest request) {
        if (request.getRoleId() == null) {
            return R.fail("角色主键不能为空");
        }
        if (request.getPermissionIds() == null || request.getPermissionIds().isEmpty()) {
            return R.fail("权限主键不能为空");
        }
        try {
            sysRoleService.setPermissions(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
