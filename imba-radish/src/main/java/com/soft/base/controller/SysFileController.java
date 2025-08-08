package com.soft.base.controller;

import com.soft.base.core.annotation.SysLog;
import com.soft.base.constants.BaseConstant;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.dto.FileDetailDto;
import com.soft.base.model.request.FilesRequest;
import com.soft.base.model.vo.FilesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.UploadAvatarVo;
import com.soft.base.model.vo.UploadFileVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/26 15:22
 **/

@RestController
@RequestMapping(value = "/file")
@Tag(name = "文件")
@Slf4j
@Validated
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
    public R<UploadFileVo> uploadFile(@RequestPart(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") MultipartFile multipartFile) {
        UploadFileVo uploadFileVo = sysFileService.uploadFile(multipartFile);
        return R.ok(uploadFileVo);
    }

    @GetMapping (value = "/downloadFile")
    @Operation(summary = "下载文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<StreamingResponseBody> downloadFile(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        HttpHeaders headers = new HttpHeaders();

        FileDetailDto fileDetail = sysFileService.getFileDetailById(id);
        if (fileDetail == null) {
            throw new GlobalException("不存在的文件");
        }

        try {
            // 设置响应头
            String mimeType = Files.probeContentType(Paths.get(fileDetail.getOriginalName()));
            headers.setContentDisposition(ContentDisposition.attachment().filename(URLEncoder.encode(fileDetail.getOriginalName(), StandardCharsets.UTF_8)).build()); // 设置文件名
            headers.setContentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE));
            headers.setContentLength(fileDetail.getFileSize());

            //流式传输
            StreamingResponseBody responseBody = outputStream -> {
                byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
                int bytesRead;
                InputStream is;
                File file;

                // 根据存储位置来获取文件源
                switch (fileDetail.getLocation()) {
                    case BaseConstant.DEFAULT_STORAGE_LOCATION: {
                        is = minioUtil.download(fileDetail.getObjectKey());
                        break;
                    }
                    case BaseConstant.DISK_STORAGE_LOCATION: {
                        file = new File(bigfileLocation + BaseConstant.LEFT_SLASH + fileDetail.getObjectKey());
                        if (!file.exists()) {
                            throw new GlobalException("资源不存在");
                        }
                        is = new FileInputStream(file);
                        break;
                    }
                    default: {
                        throw new GlobalException("资源不存在");
                    }
                }

                while ((bytesRead = is.read(buffer)) != BaseConstant.FILE_OVER_SIGN) {
                    outputStream.write(buffer, BaseConstant.INTEGER_INIT_VAL, bytesRead);
                }
                outputStream.flush();
                is.close();
            };

            // 返回文件内容
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SysLog(value = "删除文件", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_file_del')")
    @DeleteMapping
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteFile(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        sysFileService.deleteFile(id);
        return R.ok();
    }

    @PostMapping(value = "/getFiles")
    @Operation(summary = "获取文件（复）")
    public R<PageVo<FilesVo>> getFiles(@RequestBody FilesRequest request) {
        PageVo<FilesVo> pageVo = sysFileService.getFiles(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/uploadAvatar")
    @Operation(summary = "上传用户头像")
    public R<Object> uploadAvatar(@RequestPart(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") MultipartFile multipartFile) {
        UploadAvatarVo uploadAvatarVo = sysFileService.uploadAvatar(multipartFile);
        return R.ok(uploadAvatarVo);
    }
}
