package com.soft.sys.controller;

import com.soft.sys.constants.BaseConstant;
import com.soft.sys.core.annotation.LogIgnore;
import com.soft.sys.core.annotation.SysLock;
import com.soft.sys.core.annotation.SysLog;
import com.soft.sys.enums.LogModuleEnum;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.model.dto.FileDetailDto;
import com.soft.sys.model.request.FilesRequest;
import com.soft.sys.model.vo.FilesVo;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.UploadFileVo;
import com.soft.sys.model.vo.ChunkProgressVo;
import com.soft.sys.resultapi.R;
import com.soft.sys.service.SysFileService;
import com.soft.sys.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @SysLog(value = "上传文件", module = LogModuleEnum.FILE)
    public R<UploadFileVo> uploadFile(@RequestParam(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") @LogIgnore MultipartFile multipartFile,
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

    @SysLog(value = "取消分片上传", module = LogModuleEnum.FILE)
    @DeleteMapping(value = "/cancelChunk")
    @Operation(summary = "取消分片上传")
    @Parameter(name = "fileMd5", description = "文件MD5", required = true, in = ParameterIn.QUERY)
    public R<Object> cancelChunk(
            @RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") String fileMd5) {

        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);

        if (!chunkDir.exists()) {
            return R.ok("分片已清空", null);
        }

        File[] files = chunkDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    log.warn("分片删除失败：{}", file.getAbsolutePath());
                }
            }
        }

        if (!chunkDir.delete()) {
            log.warn("分片目录删除失败：{}", chunkDir.getAbsolutePath());
            return R.fail("分片删除失败");
        }

        return R.ok("分片已清空", null);
    }

    @PostMapping(value = "/getFiles")
    @Operation(summary = "获取文件列表")
    public R<PageVO<FilesVo>> getFiles(@RequestBody FilesRequest request) {
        PageVO<FilesVo> pageVo = sysFileService.getFiles(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/getMyFiles")
    @Operation(summary = "获取我的文件列表")
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
                                 @RequestPart(value = "chunk", required = false) @NotNull(message = "分片不能为空") @LogIgnore MultipartFile chunk) throws IOException {

        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);
        if (!chunkDir.exists() && !chunkDir.mkdirs()) {
            return R.fail("分片目录创建失败");
        }

        File chunkFile = new File(chunkDir, chunkIndex.toString());

        // 幂等：分片已存在且有效则跳过
        if (chunkFile.exists() && chunkFile.length() > 0) {
            log.debug("分片已存在，跳过：fileMd5={}, chunkIndex={}", fileMd5, chunkIndex);
            return R.ok();
        }

        chunk.transferTo(chunkFile);
        return R.ok();
    }

    @GetMapping(value = "/getUploadProgress")
    @Operation(summary = "查询分片上传进度")
    @Parameter(name = "fileMd5", description = "文件MD5", required = true, in = ParameterIn.QUERY)
    public R<ChunkProgressVo> getUploadProgress(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") String fileMd5) {

        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);
        ChunkProgressVo vo = new ChunkProgressVo();

        if (!chunkDir.exists() || !chunkDir.isDirectory()) {
            vo.setUploadedIndices(new ArrayList<>());
            return R.ok(vo);
        }

        List<Integer> indices = getIndices(chunkDir);

        vo.setUploadedIndices(indices);
        return R.ok(vo);
    }

    /**
     * 获取已上传的分片索引
     * @param chunkDir
     * @return
     */
    private static @NonNull List<Integer> getIndices(File chunkDir) {
        File[] files = chunkDir.listFiles();
        List<Integer> indices = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                // 只统计有效分片：是文件、大小>0、文件名为纯数字
                if (file.isFile() && file.length() > 0) {
                    try {
                        indices.add(Integer.parseInt(file.getName()));
                    } catch (NumberFormatException ignored) {
                        // 跳过非数字文件名（如合并产生的临时文件）
                    }
                }
            }
        }
        return indices;
    }

    @GetMapping(value = "/mergeChunk")
    @Operation(summary = "合并分片")
    public R<Object> mergeChunk(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") String fileMd5,
                                @RequestParam(value = "fileName", required = false) @NotBlank(message = "文件名不能为空") String fileName,
                                @RequestParam(value = "total", required = false) @NotNull(message = "分片总数不能为空") Integer total) {
        File chunkDir = new File(tmp + BaseConstant.LEFT_SLASH + fileMd5);
        File[] allFiles = chunkDir.listFiles();

        if (allFiles == null || allFiles.length == 0) {
            return R.fail("分片未找到");
        }

        // 只取纯数字文件名的有效分片，排除非分片文件（如之前合并产生的临时文件）
        Map<String, File> chunkMap = Arrays.stream(allFiles)
                .filter(f -> f.isFile() && f.getName().matches("\\d+"))
                .collect(Collectors.toMap(File::getName, Function.identity()));

        // 逐一校验：每个索引对应的分片都存在且非空
        for (int i = 0; i < total; i++) {
            File chunk = chunkMap.get(String.valueOf(i));
            if (chunk == null || !chunk.exists() || chunk.length() <= 0) {
                return R.fail("分片缺失，缺少索引：" + i);
            }
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
