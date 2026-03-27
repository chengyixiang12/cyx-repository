package com.soft.base.controller;

import com.soft.base.core.annotation.SysLock;
import com.soft.base.core.annotation.SysLog;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/4 15:57
 **/
@RestController
@RequestMapping(value = "/dictType")
@Tag(name = "字典类型")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @PostMapping(value = "/getDictTypes")
    @Operation(summary = "获取字典类型（复）")
    public R<PageVo<DictTypesVo>> getDictTypes(@RequestBody GetDictTypesRequest request) {
        PageVo<DictTypesVo> dictTypesVos = sysDictTypeService.getdictTypes(request);
        return R.ok(dictTypesVos);
    }

    @SysLock(name = "dictType")
    @SysLog(value = "添加字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_add')")
    @PostMapping
    @Operation(summary = "添加字典类型")
    public R<Object> saveDictType(@RequestBody @Valid SaveDictTypeRequest request) {
        sysDictTypeService.saveDictType(request);
        return R.ok("添加成功", null);
    }

    @SysLock(name = "dictType")
    @SysLog(value = "编辑字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_edit')")
    @PutMapping
    @Operation(summary = "编辑字典类型")
    public R<Object> editDictType(@RequestBody @Valid EditDictTypeRequest request) {
        sysDictTypeService.editDictType(request);
        return R.ok("修改成功", null);
    }

    @GetMapping
    @Operation(summary = "获取字典类型（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<DictTypeVo> getDictType(@RequestParam(value = "id", required = false) @NotBlank(message = "主键不能为空") String id) {
        DictTypeVo dictTypeVo = sysDictTypeService.getDictType(Long.parseLong(id));
        return R.ok(dictTypeVo);
    }

    @SysLog(value = "删除字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_del')")
    @DeleteMapping
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteDictType(@RequestParam(value = "id", required = false) @NotBlank(message = "主键不能为空") String id) {
        sysDictTypeService.deleteDictType(Long.parseLong(id));
        return R.ok("删除成功", null);
    }

    @SysLog(value = "批量删除字典类型", module = LogModuleEnum.DICT_TYPE)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_type_del')")
    @DeleteMapping(value = "/deleteDictTypeBatch")
    @Operation(summary = "批量删除字典类型")
    public R<Object> deleteDictTypeBatch(@RequestParam(value = "ids") @Valid List<Long> ids) {
        sysDictTypeService.deleteDictTypeBatch(ids);
        return R.ok("删除成功", null);
    }

    @GetMapping(value = "/enableDictType")
    @Operation(summary = "启用字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enableDictType(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysDictTypeService.enableDictType(id);
        return R.ok("启用成功", null);
    }

    @GetMapping(value = "/forbiddenDictType")
    @Operation(summary = "禁用字典类型")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> forbiddenDictType(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysDictTypeService.forbiddenDictType(id);
        return R.ok("禁用成功", null);
    }
}
