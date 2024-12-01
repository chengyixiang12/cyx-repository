package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.constants.HttpConstant;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.request.FilesRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 15:22
 **/

@RestController
@RequestMapping(value = "/file")
@Tag(name = "文件")
@Slf4j
public class SysFileController {

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;

    @Autowired
    public SysFileController(SysFileService sysFileService, MinioUtil minioUtil) {
        this.sysFileService = sysFileService;
        this.minioUtil = minioUtil;
    }

//    @SysLog(value = "上传文件", module = LogModuleEnum.FILE)
    @PostMapping
    @Operation(summary = "上传文件")
    @Parameter(name = "multipartFile", description = "文件流", required = true, in = ParameterIn.QUERY)
    public R uploadFile(@RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile) {
        if (multipartFile == null) {
            return R.fail("文件不能为空");
        }
        try {
            sysFileService.uploadFile(multipartFile);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "下载文件", module = LogModuleEnum.FILE)
    @GetMapping (value = "/downloadFile")
    @Operation(summary = "下载文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<R<Object>> downloadFile(@RequestParam(value = "id", required = false) Long id,
                                       HttpServletResponse response) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fail("主键不能为空"));
        }
        OutputStream os;
        InputStream is = null;
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            FileDetailDto fileDetail = sysFileService.getFileDetailById(id);
            if (fileDetail == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fail("不存在的文件"));
            }
            is = minioUtil.download(fileDetail.getObjectKey());
            os = response.getOutputStream();

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            // 设置响应头
            response.setContentType(HttpConstant.CONTENT_TYPE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    HttpConstant.HEADERS[0] + fileDetail.getOriginalName() + HttpConstant.HEADERS[1]); // 设置文件名
            response.setContentLength(-1); // 可以设置为 -1 以让 Servlet 自动计算长度
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.fail());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @SysLog(value = "删除文件", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_file_del')")
    @DeleteMapping(value = "/{id}")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R deleteFile(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            sysFileService.deleteFile(id);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "获取文件（复）", module = LogModuleEnum.FILE)
    @PostMapping(value = "/getFiles")
    @Operation(summary = "获取文件（复）")
    public R<PageVo<FilesVo>> getFiles(@RequestBody FilesRequest request) {
        try {
            PageVo<FilesVo> pageVo = sysFileService.getFiles(request);
            return R.ok(pageVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
