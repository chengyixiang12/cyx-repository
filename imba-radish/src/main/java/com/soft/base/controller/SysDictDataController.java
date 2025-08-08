package com.soft.base.controller;

import com.soft.base.core.annotation.SysLock;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.DictDatasRequest;
import com.soft.base.model.request.EditDictDataRequest;
import com.soft.base.model.request.SaveDictDataRequest;
import com.soft.base.model.vo.DictDataVo;
import com.soft.base.model.vo.DictDatasVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 11:22
 **/
@RestController
@RequestMapping(value = "/dictData")
@Slf4j
@Tag(name = "字典数据")
@Validated
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    @Autowired
    public SysDictDataController(SysDictDataService sysDictDataService) {
        this.sysDictDataService = sysDictDataService;
    }

    @PostMapping(value = "/getDictDatas")
    @Operation(summary = "获取字典数据（复）")
    public R<PageVo<DictDatasVo>> getDictDatas(@RequestBody DictDatasRequest request) {
        PageVo<DictDatasVo> pageVo = sysDictDataService.getDictDatas(request);
        return R.ok(pageVo);
    }

    @GetMapping
    @Operation(summary = "获取字典数据（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<DictDataVo> getDictData(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        DictDataVo dictDataVo = sysDictDataService.getDictData(id);
        return R.ok(dictDataVo);
    }

    @SysLock(name = "dictData")
    @SysLog(value = "添加字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_add')")
    @PostMapping
    @Operation(summary = "添加字典数据")
    public R<Object> saveDictData(@RequestBody @Valid SaveDictDataRequest request) {
        if (sysDictDataService.existCode(request.getCode())) {
            return R.fail("字典编码已存在");
        }
        sysDictDataService.saveDictData(request);
        return R.ok();
    }

    @SysLock(name = "dictData")
    @SysLog(value = "编辑字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_edit')")
    @PutMapping
    @Operation(summary = "编辑字典数据")
    public R<Object> editDictData(@RequestBody @Valid EditDictDataRequest request) {
        if (sysDictDataService.existCode(request.getCode(), request.getId())) {
            return R.fail("字典编码已存在");
        }
        sysDictDataService.editDictData(request);
        return R.ok();
    }

    @SysLog(value = "删除字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_del')")
    @DeleteMapping
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteDictData(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysDictDataService.deleteDictData(id);
        return R.ok();
    }

    @SysLog(value = "批量删除字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_del')")
    @DeleteMapping(value = "/deleteDictDataBatch")
    @Operation(summary = "批量删除字典数据")
    public R<Object> deleteDictDataBatch(@RequestBody @Valid DeleteRequest request) {
        sysDictDataService.deleteDictDataBatch(request);
        return R.ok();
    }

    @SysLog(value = "启用", module = LogModuleEnum.DICT_DATA)
    @GetMapping(value = "/enableDictData")
    @Operation(summary = "启用")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enableDictData(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysDictDataService.enableDictData(id);
        return R.ok("启用成功", null);
    }

    @SysLog(value = "禁用", module = LogModuleEnum.DICT_DATA)
    @GetMapping(value = "/forbiddenDictData")
    @Operation(summary = "禁用")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> forbiddenDictData(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysDictDataService.forbiddenDictData(id);
        return R.ok("禁用成功", null);
    }

    @SysLog(value = "设置默认", module = LogModuleEnum.DICT_DATA)
    @SysLock(name = "dictData")
    @GetMapping(value = "/setDefaultData")
    @Operation(summary = "设置默认")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "dictType", description = "字典类型", required = true, in = ParameterIn.QUERY)
    })
    public R<Object> setDefaultData(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id,
                                    @RequestParam(value = "dictType", required = false) @NotBlank(message = "字典类型不能为空") String dictType) {
        sysDictDataService.setDefaultData(id, dictType);
        return R.ok("设置成功", null);
    }
}
