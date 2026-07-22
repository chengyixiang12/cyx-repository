package com.soft.sys.controller;

import com.soft.sys.core.annotation.SysLock;
import com.soft.sys.core.annotation.SysLog;
import com.soft.sys.enums.LogModuleEnum;
import com.soft.sys.model.request.DictDatasDTO;
import com.soft.sys.model.request.EditDictDataDTO;
import com.soft.sys.model.request.SaveDictDataDTO;
import com.soft.sys.model.vo.DictDataVO;
import com.soft.sys.model.vo.DictDatasVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.resultapi.R;
import com.soft.sys.service.SysDictDataService;
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

import java.util.List;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: 
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
    @Operation(summary = "获取字典数据列表")
    public R<PageVO<DictDatasVO>> getDictDatas(@RequestBody DictDatasDTO request) {
        PageVO<DictDatasVO> pageVo = sysDictDataService.getDictDatas(request);
        return R.ok(pageVo);
    }

    @GetMapping
    @Operation(summary = "获取字典数据详情")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<DictDataVO> getDictData(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        DictDataVO dictDataVo = sysDictDataService.getDictData(id);
        return R.ok(dictDataVo);
    }

    @SysLock(name = "dictData")
    @SysLog(value = "添加字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_add')")
    @PostMapping
    @Operation(summary = "添加字典数据")
    public R<Object> saveDictData(@RequestBody @Valid SaveDictDataDTO request) {
        if (sysDictDataService.existValue(request.getParentId(), request.getValue())) {
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
    public R<Object> editDictData(@RequestBody @Valid EditDictDataDTO request) {
        if (sysDictDataService.existCode(request.getParentId(), request.getValue(), request.getId())) {
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
    public R<Object> deleteDictDataBatch(@RequestParam(value = "ids") @Valid List<Long> ids) {
        sysDictDataService.deleteDictDataBatch(ids);
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
            @Parameter(name = "parentId", description = "字典类型id", required = true, in = ParameterIn.QUERY)
    })
    public R<Object> setDefaultData(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id,
                                    @RequestParam(value = "parentId", required = false) @NotNull(message = "字典类型id不能为空") Long parentId) {
        sysDictDataService.setDefaultData(id, parentId);
        return R.ok("设置成功", null);
    }

    @GetMapping(value = "/getDictMap")
    @Operation(summary = "根据字典类型获取字典数据")
    @Parameter(name = "dictType", description = "字典类型", required = true, in = ParameterIn.QUERY)
    public R<Map<String, String>> getDictMap(@RequestParam(value = "dictType") @NotBlank(message = "字典类型") String dictType) {
        Map<String, String> dictDataMap = sysDictDataService.getDictDataMap(dictType);
        return R.ok(dictDataMap);
    }
}
