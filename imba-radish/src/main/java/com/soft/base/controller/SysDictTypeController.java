package com.soft.base.controller;

import com.soft.base.annotation.SysLock;
import com.soft.base.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.EditDictTypeRequest;
import com.soft.base.model.request.GetDictTypesRequest;
import com.soft.base.model.request.SaveDictTypeRequest;
import com.soft.base.model.vo.DictTypeVo;
import com.soft.base.model.vo.DictTypesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDictTypeService;
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
 * @DateTime: 2024/11/4 15:57
 **/
@RestController
@RequestMapping(value = "/dictType")
@Tag(name = "字典类型")
@Slf4j
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @Autowired
    public SysDictTypeController(SysDictTypeService sysDictTypeService) {
        this.sysDictTypeService = sysDictTypeService;
    }

    @PostMapping(value = "/getDictTypes")
    @Operation(summary = "获取字典类型（复）")
    public R<PageVo<DictTypesVo>> getDictTypes(@RequestBody GetDictTypesRequest request) {
        try {
            PageVo<DictTypesVo> dictTypesVos = sysDictTypeService.getdictTypes(request);
            return R.ok(dictTypesVos);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLock(name = "dictType")
    @SysLog(value = "添加字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_add')")
    @PostMapping
    @Operation(summary = "添加字典类型")
    public R<Object> saveDictType(@RequestBody SaveDictTypeRequest request) {
        if (StringUtils.isBlank(request.getDictName())) {
            return R.fail("字典名称不能为空");
        }
        if (StringUtils.isBlank(request.getDictType())) {
            return R.fail("字典类型不能为空");
        }
        if (sysDictTypeService.existDictType(request.getDictType())) {
            return R.fail("字典类型已存在");
        }
        try {
            sysDictTypeService.saveDictType(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLock(name = "dictType")
    @SysLog(value = "编辑字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_edit')")
    @PutMapping
    @Operation(summary = "编辑字典类型")
    public R<Object> editDictType(@RequestBody EditDictTypeRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能为空");
        }
        if (StringUtils.isBlank(request.getDictType())) {
            return R.fail("字典类型不能为空");
        }
        if (sysDictTypeService.existDictType(request.getDictType(), request.getId())) {
            return R.fail("字典类型已存在");
        }
        try {
            sysDictTypeService.editDictType(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "获取字典类型（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<DictTypeVo> getDictType(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            DictTypeVo dictTypeVo = sysDictTypeService.getDictType(id);
            return R.ok(dictTypeVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "删除字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> deleteDictType(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysDictTypeService.deleteDictType(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "批量删除字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_del')")
    @DeleteMapping(value = "/deleteDictTypeBatch")
    @Operation(summary = "批量删除字典类型")
    public R deleteDictTypeBatch(@RequestBody DeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return R.fail("主键集合不能为空");
        }
        try {
            sysDictTypeService.deleteDictTypeBatch(request.getIds());
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/enableDictType")
    @Operation(summary = "启用字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enableDictType(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysDictTypeService.enableDictType(id);
            return R.ok("启用成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/forbiddenDictType")
    @Operation(summary = "禁用字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> forbiddenDictType(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysDictTypeService.forbiddenDictType(id);
            return R.ok("禁用成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
