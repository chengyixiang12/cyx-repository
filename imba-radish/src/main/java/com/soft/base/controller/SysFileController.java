package com.soft.base.controller;

import com.soft.base.constants.BaseConstant;
import com.soft.base.core.annotation.SysLock;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.dto.FileDetailDto;
import com.soft.base.model.request.FilesRequest;
import com.soft.base.model.vo.FilesVo;
import com.soft.base.model.vo.PageVO;
import com.soft.base.model.vo.UploadAvatarVo;
import com.soft.base.model.vo.UploadFileVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: cyx
 * @Description:
 * @DateTime: 2024/10/26 15:22
 **/

@RestController
@RequestMapping(value = "/file")
@Tag(name = "文件")
@Slf4j
@Validated
@RequiredArgsConstructor
public class SysFileController {

    @Value(value = "${storage.big-file.location}")
    private String bigfileLocation;

    @Value(value = "${tmp.path}")
    private String tmp;

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;


    @PostMapping(value = "/upload")
    @Operation(summary = "上传文件")
    public R<UploadFileVo> uploadFile(@RequestParam(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") MultipartFile multipartFile,
                                      @RequestParam(value = "fileMd5", required = false) @NotBlank(message = "fileMd5不能为空") String fileMd5) {
        UploadFileVo uploadFileVo = sysFileService.uploadFile(multipartFile, fileMd5);
        return R.ok("上传成功", uploadFileVo);
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
                if (BaseConstant.Minio.MINIO.equals(fileDetail.getLocation())) {
                    is = minioUtil.download(fileDetail.getObjectKey());
                } else if (BaseConstant.Minio.DISK.equals(fileDetail.getLocation())) {
                    file = new File(bigfileLocation + BaseConstant.LEFT_SLASH + fileDetail.getObjectKey());
                    if (!file.exists()) {
                        throw new GlobalException("资源不存在");
                    }
                    is = new FileInputStream(file);
                } else {
                    throw new GlobalException("资源不存在");
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
        return R.ok("删除成功", null);
    }

    @PostMapping(value = "/getFiles")
    @Operation(summary = "获取文件（复）")
    public R<PageVO<FilesVo>> getFiles(@RequestBody FilesRequest request) {
        PageVO<FilesVo> pageVo = sysFileService.getFiles(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/uploadAvatar")
    @Operation(summary = "上传用户头像")
    public R<UploadAvatarVo> uploadAvatar(@RequestPart(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") MultipartFile multipartFile) {
        UploadAvatarVo uploadAvatarVo = sysFileService.uploadAvatar(multipartFile);
        return R.ok(uploadAvatarVo);
    }

    @PostMapping(value = "/getMyFiles")
    @Operation(summary = "获取我的文件（复）")
    public R<PageVO<FilesVo>> getMyFiles(@RequestBody FilesRequest request) {
        PageVO<FilesVo> pageVo = sysFileService.getMyFiles(request);
        return R.ok(pageVo);
    }

    @SysLock(name = "file")
    @GetMapping(value = "/getFileUrl")
    @Operation(summary = "获取文件url")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isInline", description = "是否在线预览；1：是；0：否", in = ParameterIn.QUERY)
    })
    public R<String> getFileUrl(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id,
                                @RequestParam(value = "isInline", required = false, defaultValue = "0") String isInline) {
        String url = sysFileService.getFileUrl(id, isInline);
        return R.ok(url);
    }

    @PostMapping(value = "/uploadChunk")
    @Operation(summary = "上传分片")
    @Parameters({
            @Parameter(name = "fileMd5", description = "文件MD5", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "chunkIndex", description = "索引", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "chunk", description = "分片", required = true, in = ParameterIn.QUERY)
    })
    public R<Object> uploadChunk(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") String fileMd5,
                                 @RequestParam(value = "chunkIndex", required = false) @NotNull(message = "索引不能为空") Integer chunkIndex,
                                 @RequestPart(value = "chunk", required = false) @NotNull(message = "分片不能为空") MultipartFile chunk) throws IOException {

        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);
        if (!chunkDir.exists() && !chunkDir.mkdirs()) {
            return R.fail("分片目录创建失败");
        }

        File chunkFile = new File(chunkDir, chunkIndex.toString());
        chunk.transferTo(chunkFile);
        return R.ok();
    }

    @GetMapping(value = "/mergeChunk")
    @Operation(summary = "合并分片")
    public R<Object> mergeChunk(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") String fileMd5,
                                @RequestParam(value = "fileName", required = false) @NotBlank(message = "文件名不能为空") String fileName,
                                @RequestParam(value = "total", required = false) @NotNull(message = "分片总数不能为空") Integer total) {
        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);
        File[] chunks = chunkDir.listFiles();

        if (chunks == null) {
            return R.fail("分片未找到");
        }

        if (total == null || !total.equals(chunks.length)) {
            return R.fail("分片数量错误");
        }

        File fileTemp = new File(chunkDir, fileName);
        try (FileOutputStream os = new FileOutputStream(fileTemp);
                FileChannel out = os.getChannel()) {
            if (!fileTemp.exists()) {
                boolean flag = fileTemp.createNewFile();
                if (!flag) {
                    return R.fail("文件缓存创建失败");
                }
            }
            Map<String, File> chunkMap = Arrays.stream(chunks).collect(Collectors.toMap(File::getName, Function.identity()));
            for (int i = 0; i < total; i++) {
                File chunk = chunkMap.get(String.valueOf(i));
                try (FileInputStream is = new FileInputStream(chunk);
                     FileChannel in = is.getChannel()) {
                    // 零拷贝传输
                    in.transferTo(0, in.size(), out);
                } catch (IOException e) {
                    throw new GlobalException(e.getMessage());
                }
                chunk.delete();
            }

            UploadFileVo uploadFileVo = sysFileService.mergeChunk(fileTemp, fileMd5);

            return R.ok("上传成功", uploadFileVo);
        } catch (IOException e) {
            throw new GlobalException(e);
        }
    }

    @GetMapping(value = "/getFileByMd5")
    @Operation(summary = "根据md5获取文件")
    public R<String> getFileByMd5(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "md5不能为空") String fileMd5,
                                  @RequestParam(value = "fileName", required = false) @NotBlank(message = "文件名不能为空") String fileName) {
        String id = sysFileService.getFileByMd5(fileMd5, fileName);
        return R.ok(id);
    }
}
