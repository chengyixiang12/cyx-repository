package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.HttpConstant;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.request.FilesRequest;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.UploadFileVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @Value(value = "${storage.big-file.location}")
    private String bigfileLocation;

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;

    @Autowired
    public SysFileController(SysFileService sysFileService,
                             MinioUtil minioUtil) {
        this.sysFileService = sysFileService;
        this.minioUtil = minioUtil;
    }

    @PostMapping
    @Operation(summary = "上传文件")
    public R uploadFile(@RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile) {
        if (multipartFile == null) {
            return R.fail("文件不能为空");
        }
        try {
            UploadFileVo uploadFileVo = sysFileService.uploadFile(multipartFile);
            return R.ok(uploadFileVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping (value = "/downloadFile")
    @Operation(summary = "下载文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<Object> downloadFile(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return ResponseEntity.ok().body(R.fail("主键不能为空"));
        }
        InputStream is = null;


        try {
            FileDetailDto fileDetail = sysFileService.getFileDetailById(id);
            if (fileDetail == null) {
                return ResponseEntity.status(HttpStatusCode.valueOf(HttpConstant.SUCCESS)).body(R.fail("不存在的文件"));
            }

            // 根据存储位置来获取文件源
            switch (fileDetail.getLocation()) {
                case BaseConstant.DEFAULT_STORAGE_LOCATION: {
                    is = minioUtil.download(fileDetail.getObjectKey());
                    break;
                }
                case BaseConstant.DISK_STORAGE_LOCATION: {
                    is = new FileInputStream(bigfileLocation + BaseConstant.LEFT_SLASH + fileDetail.getObjectKey());
                    break;
                }
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(URLEncoder.encode(fileDetail.getOriginalName(), StandardCharsets.UTF_8)).build()); // 设置文件名
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 返回文件内容
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(is.readAllBytes());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok().body(R.fail());
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
