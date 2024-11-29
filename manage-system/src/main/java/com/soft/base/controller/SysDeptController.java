package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.request.DeleteRequest;
import com.soft.base.request.EditDeptRequest;
import com.soft.base.request.SaveDeptRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDeptService;
import com.soft.base.vo.DeptTreeVo;
import com.soft.base.vo.DeptVo;
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
 * @DateTime: 2024/10/26 10:05
 **/

@RestController
@RequestMapping(value = "/dept")
@Tag(name = "部门")
@Slf4j
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Autowired
    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

    @SysLog(value = "获取组织架构", module = LogModuleEnum.DEPT)
    @GetMapping(value = "/getDeptTree")
    @Operation(summary = "获取组织架构")
    public R<List<DeptTreeVo>> getDeptTree() {
        try {
            List<DeptTreeVo> deptTreeVos = sysDeptService.getDeptTree();
            return R.ok(deptTreeVos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "添加部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_add')")
    @PostMapping
    @Operation(summary = "添加部门")
    public R saveDept(@RequestBody SaveDeptRequest request) {
        if (StringUtils.isBlank(request.getCode())) {
            return R.fail("部门编码不能为空");
        }
        if (StringUtils.isBlank(request.getName())) {
            return R.fail("部门名称不能为空");
        }

        try {
            Boolean notEmpty = sysDeptService.isNotEmpty();
            if (notEmpty && request.getParentId() == null) {
                return R.fail("父级id不能为空");
            }
            Boolean existCode = sysDeptService.existCode(request.getCode());
            if (existCode) {
                return R.fail("编码：" + request.getCode() + "已存在");
            }
            sysDeptService.saveDept(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "编辑部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_edit')")
    @PutMapping
    @Operation(summary = "编辑部门")
    public R editDept(@RequestBody EditDeptRequest request) {
        if (request.getId() == null) {
            return R.fail("id不能为空");
        }
        try {
            sysDeptService.editDept(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "删除部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R deleteDept(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("id不能为空");
        }
        try {
            sysDeptService.removeById(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "批量删除部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_del')")
    @DeleteMapping(value = "/deleteRoleBatch")
    @Operation(summary = "批量删除部门")
    public R deleteRoleBatch(@RequestBody DeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return R.fail("id不能为空");
        }
        try {
            sysDeptService.deleteDeptBatch(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "获取部门（单）", module = LogModuleEnum.DEPT)
    @GetMapping(value = "/{id}")
    @Operation(summary = "获取部门（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<DeptVo> getDept(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("id不能为空");
        }
        try {
            DeptVo deptVo = sysDeptService.getDept(id);
            return R.ok(deptVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
