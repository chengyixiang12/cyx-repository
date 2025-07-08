package com.soft.base.controller;

import com.soft.base.core.annotation.SysLock;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.constants.BaseConstant;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.model.dto.ExportDeptDto;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.DeptTreeVo;
import com.soft.base.model.vo.DeptVo;
import com.soft.base.model.vo.GetDeptsVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
@Validated
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Autowired
    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

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

    @SysLock(name = "dept")
    @SysLog(value = "添加部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_add')")
    @PostMapping
    @Operation(summary = "添加部门")
    public R<Object> saveDept(@RequestBody @Valid SaveDeptRequest request) {
        if (sysDeptService.existCode(request.getCode())) {
            return R.fail("部门编码已存在");
        }
        Boolean notEmpty = sysDeptService.isNotEmpty();
        // 如果数据库中存在值，则父id不能为空
        if (notEmpty && request.getParentId() == null) {
            return R.fail("父级id不能为空");
        }
        Boolean existCode = sysDeptService.existCode(request.getCode());
        if (existCode) {
            return R.fail("编码：" + request.getCode() + "已存在");
        }
        sysDeptService.saveDept(request);
        return R.ok();
    }

    @SysLock(name = "dept")
    @SysLog(value = "编辑部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_edit')")
    @PutMapping
    @Operation(summary = "编辑部门")
    public R<Object> editDept(@RequestBody @Valid EditDeptRequest request) {
        if (sysDeptService.existCode(request.getCode(), request.getId())) {
            return R.fail("部门编码已存在");
        }
        sysDeptService.editDept(request);
        return R.ok();
    }

    @SysLog(value = "删除部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_del')")
    @DeleteMapping
    @Operation(summary = "删除部门")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> deleteDept(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysDeptService.removeById(id);
        return R.ok();
    }

    @SysLog(value = "批量删除部门", module = LogModuleEnum.DEPT)
    @PreAuthorize(value = "@cps.hasPermission('sys_dept_del')")
    @DeleteMapping(value = "/deleteRoleBatch")
    @Operation(summary = "批量删除部门")
    public R<Object> deleteRoleBatch(@RequestBody @Valid DeleteRequest request) {
        sysDeptService.deleteDeptBatch(request);
        return R.ok();
    }

    @GetMapping
    @Operation(summary = "获取部门（单）")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<DeptVo> getDept(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        DeptVo deptVo = sysDeptService.getDept(id);
        return R.ok(deptVo);
    }

    @PostMapping(value = "/exportDept")
    @Operation(summary = "导出部门")
    public ResponseEntity<byte[]> exportDept(@RequestBody @Valid ExportDeptRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String fileName = request.getFileName();
        if (StringUtils.isBlank(fileName)) {
            request.setFileName(BaseConstant.EXPORT_DEPT_EXCEL_NAME);
        }
        ClassPathResource resource = new ClassPathResource("template/exportDept.xlsx");
        try(InputStream is = resource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            List<ExportDeptDto> exportDeptDtos = sysDeptService.exportDept(request);

            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < exportDeptDtos.size(); i++) {
                ExportDeptDto exportDeptDto = exportDeptDtos.get(i);
                XSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(exportDeptDto.getId());
                row.createCell(1).setCellValue(exportDeptDto.getCode());
                row.createCell(2).setCellValue(exportDeptDto.getName());
                row.createCell(3).setCellValue(exportDeptDto.getParentName());
            }

            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            workbook.write(bis);

            byte[] byteArray = bis.toByteArray();

            // 设置响应头
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // 设置文件类型
            headers.setContentDisposition(ContentDisposition.attachment().filename(URLEncoder.encode(fileName.endsWith(BaseConstant.EXCEL_SUFFIX) ? fileName : fileName + BaseConstant.EXCEL_SUFFIX, StandardCharsets.UTF_8)).build()); // 设置文件名
            headers.setContentLength(byteArray.length);

            // 返回 ResponseEntity，带上文件内容和响应头
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArray);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/getDepts")
    @Operation(summary = "获取部门（复）")
    public R<PageVo<GetDeptsVo>> getDepts(@RequestBody GetDeptsRequest request) {
        PageVo<GetDeptsVo> pageVo = sysDeptService.getDepts(request);
        return R.ok(pageVo);
    }
}
