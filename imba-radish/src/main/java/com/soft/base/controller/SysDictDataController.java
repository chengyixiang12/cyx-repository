package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
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
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    @Autowired
    public SysDictDataController(SysDictDataService sysDictDataService) {
        this.sysDictDataService = sysDictDataService;
    }

    @PostMapping(value = "/getDictDatas")
    @Operation(summary = "获取字典数据（复）")
    public R<PageVo<DictDatasVo>> getDictDatas(@RequestBody DictDatasRequest request) {
        if (StringUtils.isBlank(request.getDictType())) {
            return R.fail("字典类型不能为空");
        }
        try {
            PageVo<DictDatasVo> pageVo = sysDictDataService.getDictDatas(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/{id}")
    @Operation(summary = "获取字典数据（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<DictDataVo> getDictData(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            DictDataVo dictDataVo = sysDictDataService.getDictData(id);
            return R.ok(dictDataVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "添加字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_add')")
    @PostMapping
    @Operation(summary = "添加字典数据")
    public R<Object> saveDictData(@RequestBody SaveDictDataRequest request) {
        if (StringUtils.isBlank(request.getDictType())) {
            return R.fail("字典类型不能为空");
        }
        if (StringUtils.isBlank(request.getCode())) {
            return R.fail("字典编码不能为空");
        }
        if (sysDictDataService.existCode(request.getCode())) {
            return R.fail("字典编码已存在");
        }
        if (StringUtils.isBlank(request.getValue())) {
            return R.fail("字典值不能为空");
        }
        try {
            sysDictDataService.saveDictData(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "编辑字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_edit')")
    @PutMapping
    @Operation(summary = "编辑字典数据")
    public R<Object> editDictData(@RequestBody EditDictDataRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能为空");
        }
        if (StringUtils.isBlank(request.getDictType())) {
            return R.fail("字典类型不能为空");
        }
        if (StringUtils.isBlank(request.getCode())) {
            return R.fail("字典编码不能为空");
        }
        if (sysDictDataService.existCode(request.getCode(), request.getId())) {
            return R.fail("字典编码已存在");
        }
        if (StringUtils.isBlank(request.getValue())) {
            return R.fail("字典值不能为空");
        }
        try {
            sysDictDataService.editDictData(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "删除字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> deleteDictData(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysDictDataService.deleteDictData(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "批量删除字典数据", module = LogModuleEnum.DICT_DATA)
    @PreAuthorize(value = "@cps.hasPermission('sys_dict_data_del')")
    @DeleteMapping(value = "/deleteDictDataBatch")
    @Operation(summary = "批量删除字典数据")
    public R<Object> deleteDictDataBatch(@RequestBody DeleteRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return R.fail("主键不能为空");
        }
        try {
            sysDictDataService.deleteDictDataBatch(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
