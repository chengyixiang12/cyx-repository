package com.soft.sys.controller;

import com.soft.common.annotation.LogIgnore;
import com.soft.common.annotation.SysLock;
import com.soft.common.annotation.SysLog;
import com.soft.common.constants.BaseConstant;
import com.soft.common.constants.RegexConstant;
import com.soft.common.enums.LogModuleEnum;
import com.soft.common.exception.GlobalException;
import com.soft.common.resultapi.R;
import com.soft.sys.file.ChunkStorage;
import com.soft.sys.model.dto.FileDetailDTO;
import com.soft.sys.model.request.FileListDTO;
import com.soft.sys.model.request.FilesDTO;
import com.soft.sys.model.vo.*;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.service.SysFileService;
import com.soft.sys.utils.FileNameUtil;
import com.soft.sys.utils.MinioUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

    /** 单分片允许范围，与 spring.servlet.multipart.max-request-size 保持一致 */
    private static final long MIN_CHUNK_SIZE = 1024L * 1024L;
    private static final long MAX_CHUNK_SIZE = 12L * 1024 * 1024L;

    private final RadishProperty radishProperty;

    private final SysFileService sysFileService;

    private final MinioUtil minioUtil;

    private final ChunkStorage chunkStorage;

    @PostMapping(value = "/upload")
    @Operation(summary = "上传文件")
    @Parameters({
            @Parameter(name = "multipartFile", description = "文件", required = true),
            @Parameter(name = "fileMd5", description = "文件MD5", required = true),
            @Parameter(name = "folderId", description = "目标文件夹主键；为空表示根目录")
    })
    @SysLog(value = "上传文件", module = LogModuleEnum.FILE)
    public R<UploadFileVO> uploadFile(@RequestParam(value = "multipartFile", required = false) @NotNull(message = "文件不能为空") @LogIgnore MultipartFile multipartFile,
                                      @RequestParam(value = "fileMd5", required = false) @NotBlank(message = "fileMd5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5,
                                      @RequestParam(value = "folderId", required = false) Long folderId) {
        UploadFileVO uploadFileVo = sysFileService.uploadFile(multipartFile, fileMd5, folderId);
        // 不带 msg：批量上传由任务面板逐条展示结果，头像上传由页面自行提示
        return R.ok(uploadFileVo);
    }

    @GetMapping(value = "/downloadFile")
    @Operation(summary = "下载文件")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public ResponseEntity<StreamingResponseBody> downloadFile(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        HttpHeaders headers = new HttpHeaders();

        FileDetailDTO fileDetail = sysFileService.getFileDetailById(id);
        if (fileDetail == null) {
            throw new GlobalException("不存在的文件");
        }

        // 数据库有记录但 MinIO 里的对象可能已被删除（例如在文件管理里删掉了头像文件）。
        // 必须在写响应头之前校验：一旦进入流式写出阶段，异常无法再转成正常的 JSON 错误响应，
        // 客户端只能拿到断流或 500。
        if (!BaseConstant.Minio.MINIO.equals(fileDetail.getLocation())) {
            throw new GlobalException("文件存储位置异常");
        }
        if (!minioUtil.exists(fileDetail.getObjectKey())) {
            throw new GlobalException("文件不存在或已被删除");
        }

        try {
            String mimeType = Files.probeContentType(Path.of(fileDetail.getOriginalName()));
            headers.setContentDisposition(ContentDisposition.attachment().filename(URLEncoder.encode(fileDetail.getOriginalName(), StandardCharsets.UTF_8)).build());
            headers.setContentType(MediaType.parseMediaType(mimeType != null ? mimeType : MediaType.APPLICATION_OCTET_STREAM_VALUE));
            headers.setContentLength(fileDetail.getFileSize());

            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream is = minioUtil.download(fileDetail.getObjectKey())) {
                    byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != BaseConstant.FILE_OVER_SIGN) {
                        outputStream.write(buffer, BaseConstant.INTEGER_INIT_VAL, bytesRead);
                    }
                    outputStream.flush();
                }
            };

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
            @RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5) {
        chunkStorage.deleteChunks(fileMd5);
        return R.ok();
    }

    @PostMapping(value = "/getFiles")
    @Operation(summary = "获取文件列表")
    public R<PageVO<FilesVO>> getFiles(@RequestBody FilesDTO request) {
        PageVO<FilesVO> pageVo = sysFileService.getFiles(request);
        return R.ok(pageVo);
    }

    @PostMapping(value = "/getMyFiles")
    @Operation(summary = "获取我的文件列表")
    public R<PageVO<FilesVO>> getMyFiles(@RequestBody FilesDTO request) {
        PageVO<FilesVO> pageVo = sysFileService.getMyFiles(request);
        return R.ok(pageVo);
    }

    @SysLock(name = "file")
    @GetMapping(value = "/getFileUrl")
    @Operation(summary = "获取文件url")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isInline", description = "是否在线预览；1：是；0：否", in = ParameterIn.QUERY)
    })
    public R<GetFileUrlVO> getFileUrl(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id,
                                @RequestParam(value = "isInline", required = false, defaultValue = "0") String isInline) {
        String url = sysFileService.getFileUrl(id, isInline);
        GetFileUrlVO getFileUrlVO = new GetFileUrlVO();
        getFileUrlVO.setUrl(url);
        return R.ok(getFileUrlVO);
    }

    @PostMapping(value = "/uploadChunk")
    @Operation(summary = "上传分片")
    @Parameters({
            @Parameter(name = "fileMd5", description = "文件MD5", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "chunkIndex", description = "索引", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "chunk", description = "分片", required = true, in = ParameterIn.QUERY)
    })
    public R<Object> uploadChunk(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5,
                                 @RequestParam(value = "chunkIndex", required = false) @NotNull(message = "索引不能为空") Integer chunkIndex,
                                 @RequestPart(value = "chunk", required = false) @NotNull(message = "分片不能为空") @LogIgnore MultipartFile chunk) throws IOException {
        if (chunkIndex == null || chunkIndex < 0 || chunkIndex >= (radishProperty.getUpload().getMaxTotalFileSize() + MIN_CHUNK_SIZE - 1) / MIN_CHUNK_SIZE) {
            throw new GlobalException("分片索引不合法");
        }
        if (chunk.isEmpty() || chunk.getSize() > MAX_CHUNK_SIZE) {
            throw new GlobalException("分片大小不合法");
        }
        chunkStorage.writeChunk(fileMd5, chunkIndex, chunk);
        return R.ok();
    }

    @GetMapping(value = "/getUploadProgress")
    @Operation(summary = "查询分片上传进度")
    @Parameters({
            @Parameter(name = "fileMd5", description = "文件MD5", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "fileSize", description = "文件总大小（字节）", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "chunkSize", description = "分片大小（字节）", required = true, in = ParameterIn.QUERY)
    })
    public R<ChunkProgressVO> getUploadProgress(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5,
                                                 @RequestParam(value = "fileSize", required = false) @NotNull(message = "文件大小不能为空") Long fileSize,
                                                 @RequestParam(value = "chunkSize", required = false) @NotNull(message = "分片大小不能为空") Long chunkSize) {
        validateFileParams(fileSize, chunkSize);
        ChunkProgressVO vo = new ChunkProgressVO();
        List<Integer> indices = chunkStorage.uploadedIndices(fileMd5, fileSize, chunkSize);
        vo.setUploadedIndices(indices);
        return R.ok(vo);
    }

    /**
     * 合并分片。
     * 从 GET 改为 POST（原实现用有副作用的 GET，浏览器/代理缓存和预取都可能重复触发），
     * 并按 md5 加锁保证并发幂等：MinIO 上传成功并落库后才删除分片目录，失败保留分片可重试。
     */
    @PostMapping(value = "/mergeChunk")
    @Operation(summary = "合并分片")
    public R<UploadFileVO> mergeChunk(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "文件MD5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5,
                                      @RequestParam(value = "fileName", required = false) @NotBlank(message = "文件名不能为空") String fileName,
                                      @RequestParam(value = "total", required = false) @NotNull(message = "分片总数不能为空") Integer total,
                                      @RequestParam(value = "fileSize", required = false) @NotNull(message = "文件大小不能为空") Long fileSize,
                                      @RequestParam(value = "chunkSize", required = false) @NotNull(message = "分片大小不能为空") Long chunkSize,
                                      @RequestParam(value = "folderId", required = false) Long folderId) {
        validateFileParams(fileSize, chunkSize);
        long expectedTotal = (fileSize + chunkSize - 1) / chunkSize;
        if (total == null || total <= 0 || total != expectedTotal) {
            throw new GlobalException("分片数量不合法");
        }
        String originalName = FileNameUtil.sanitize(fileName);

        synchronized (chunkStorage.lock(chunkStorage.chunkDir(fileMd5))) {
            try {
                File merged = chunkStorage.merge(fileMd5, total, fileSize, chunkSize).toFile();
                UploadFileVO vo = sysFileService.mergeChunk(merged, fileMd5, originalName, folderId);
                // 走到这里说明 MinIO 已确认可下载、数据库记录已落库，分片可以安全删除
                chunkStorage.deleteChunks(fileMd5);
                // 不带 msg：批量上传时任务面板已经逐条展示结果，避免弹一屏重复 toast
                return R.ok(vo);
            } catch (IOException e) {
                chunkStorage.deleteMerged(fileMd5);
                throw new GlobalException("分片合并失败：" + e.getMessage());
            } catch (RuntimeException e) {
                // 只删合并产物，保留分片，前端"重试"无需重传
                chunkStorage.deleteMerged(fileMd5);
                throw e;
            }
        }
    }

    @GetMapping(value = "/getFileByMd5")
    @Operation(summary = "根据md5获取文件")
    @Parameters({
            @Parameter(name = "fileMd5", description = "文件MD5", required = true),
            @Parameter(name = "fileName", description = "文件名", required = true),
            @Parameter(name = "folderId", description = "目标文件夹主键；为空表示根目录")
    })
    public R<String> getFileByMd5(@RequestParam(value = "fileMd5", required = false) @NotBlank(message = "md5不能为空") @Pattern(regexp = RegexConstant.FILE_HASH_REGEX, message = "文件MD5不合法") String fileMd5,
                                  @RequestParam(value = "fileName", required = false) @NotBlank(message = "文件名不能为空") String fileName,
                                  @RequestParam(value = "folderId", required = false) Long folderId) {
        String id = sysFileService.getFileByMd5(fileMd5, FileNameUtil.sanitize(fileName), folderId);
        return R.ok(id);
    }

    @PostMapping(value = "/list")
    @Operation(summary = "获取文件夹层内容（子文件夹 + 文件分页 + 面包屑）")
    public R<FolderContentVO> listByFolder(@RequestBody FileListDTO request) {
        return R.ok(sysFileService.listByFolder(request));
    }

    @SysLog(value = "新建文件夹", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_folder_add')")
    @PostMapping(value = "/folder")
    @Operation(summary = "新建文件夹")
    @Parameters({
            @Parameter(name = "name", description = "文件夹名称", required = true),
            @Parameter(name = "parentId", description = "父文件夹主键；为空表示根目录")
    })
    public R<Object> addFolder(@RequestParam(value = "name") @NotBlank(message = "文件夹名称不能为空") String name,
                               @RequestParam(value = "parentId", required = false) Long parentId) {
        sysFileService.addFolder(name, parentId);
        return R.ok("创建成功", null);
    }

    @SysLog(value = "重命名文件夹", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_folder_edit')")
    @PutMapping(value = "/folder")
    @Operation(summary = "重命名文件夹")
    @Parameters({
            @Parameter(name = "id", description = "文件夹主键", required = true),
            @Parameter(name = "name", description = "新名称", required = true)
    })
    public R<Object> renameFolder(@RequestParam(value = "id") @NotNull(message = "id不能为空") Long id,
                                  @RequestParam(value = "name") @NotBlank(message = "文件夹名称不能为空") String name) {
        sysFileService.renameFolder(id, name);
        return R.ok("重命名成功", null);
    }

    @SysLog(value = "删除文件夹", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_folder_del')")
    @DeleteMapping(value = "/folder")
    @Operation(summary = "删除文件夹（非空禁删）")
    @Parameter(name = "id", description = "文件夹主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteFolder(@RequestParam(value = "id") @NotNull(message = "id不能为空") Long id) {
        sysFileService.deleteFolder(id);
        return R.ok("删除成功", null);
    }

    @GetMapping(value = "/folderTree")
    @Operation(summary = "获取全部文件夹树")
    public R<List<FolderTreeNodeVO>> folderTree() {
        return R.ok(sysFileService.folderTree());
    }

    @SysLog(value = "移动文件", module = LogModuleEnum.FILE)
    @PreAuthorize(value = "@cps.hasPermission('sys_file_move')")
    @PutMapping(value = "/move")
    @Operation(summary = "移动文件到指定文件夹")
    @Parameters({
            @Parameter(name = "id", description = "文件主键", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "folderId", description = "目标文件夹主键；为空时归入系统文件夹", in = ParameterIn.QUERY)
    })
    public R<Object> moveFile(@RequestParam(value = "id") @NotNull(message = "id不能为空") Long id,
                              @RequestParam(value = "folderId", required = false) Long folderId) {
        sysFileService.moveFile(id, folderId);
        return R.ok("移动成功", null);
    }

    private void validateFileParams(Long fileSize, Long chunkSize) {
        if (fileSize == null || fileSize <= 0 || fileSize > radishProperty.getUpload().getMaxTotalFileSize()) {
            throw new GlobalException("文件大小超出限制");
        }
        if (chunkSize == null || chunkSize < MIN_CHUNK_SIZE || chunkSize > MAX_CHUNK_SIZE) {
            throw new GlobalException("分片大小不合法");
        }
    }
}
