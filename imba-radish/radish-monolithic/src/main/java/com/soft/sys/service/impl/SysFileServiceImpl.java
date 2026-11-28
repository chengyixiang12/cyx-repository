package com.soft.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.common.constants.BaseConstant;
import com.soft.common.constants.DictConstant;
import com.soft.common.enums.RedisKeyPrefix;
import com.soft.common.exception.GlobalException;
import com.soft.sys.async.FileDeleteAsync;
import com.soft.sys.entity.SysFile;
import com.soft.sys.entity.SysFolder;
import com.soft.sys.mapper.SysFileMapper;
import com.soft.sys.mapper.SysFolderMapper;
import com.soft.sys.model.dto.FileDetailDTO;
import com.soft.sys.model.dto.FileHashDTO;
import com.soft.sys.model.dto.FolderFileCountDTO;
import com.soft.sys.model.request.FileListDTO;
import com.soft.sys.model.request.FilesDTO;
import com.soft.sys.model.vo.FilesVO;
import com.soft.sys.model.vo.FolderContentVO;
import com.soft.sys.model.vo.FolderTreeNodeVO;
import com.soft.sys.model.vo.FolderVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.UploadFileVO;
import com.soft.sys.properties.MinioProperty;
import com.soft.sys.service.SysDictDataService;
import com.soft.sys.service.SysFileService;
import com.soft.sys.utils.FileNameUtil;
import com.soft.sys.utils.MinioUtil;
import com.soft.sys.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author cyq
 * @description 针对表【sys_file】的数据库操作Service实现
 * @createDate 2024-10-26 15:19:23
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements SysFileService {

    /** 分段锁：秒传/合并的"查重 + 落库"必须互斥，否则并发会产生重复记录或重复写 MinIO */
    private static final int LOCK_STRIPES = 64;
    private final Object[] uploadLocks = createUploadLocks();

    private final SysFileMapper sysFileMapper;

    private final SysFolderMapper sysFolderMapper;

    private final MinioUtil minioUtil;

    private final MinioProperty minioProperty;

    private final SysDictDataService sysDictDataService;

    private final SecurityUtil securityUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FileDeleteAsync fileDeleteAsync;

    private static Object[] createUploadLocks() {
        Object[] stripes = new Object[LOCK_STRIPES];
        for (int i = 0; i < LOCK_STRIPES; i++) {
            stripes[i] = new Object();
        }
        return stripes;
    }

    private Object uploadLock(String key) {
        return uploadLocks[Math.floorMod(key.hashCode(), uploadLocks.length)];
    }

    @Override
    public UploadFileVO uploadFile(MultipartFile multipartFile, String fileMd5, Long folderId) {
        String originalName = FileNameUtil.sanitize(multipartFile.getOriginalFilename());
        Long userId = securityUtil.getUserInfo().getId();
        String lockKey = userId + ":" + fileMd5 + ":" + originalName;
        synchronized (uploadLock(lockKey)) {
            // 幂等：同一用户、同一内容、同一文件名重复上传直接返回已有记录
            Long existingId = sysFileMapper.getFileIdByHashAndOwner(fileMd5, originalName, userId);
            if (existingId != null) {
                return buildUploadVO(existingId, originalName);
            }
            try {
                FileHashDTO fileHashDto = sysFileMapper.getFileByHash(fileMd5);
                // 哈希命中但 MinIO 对象已被清理时不能复用，否则会写入一条下载不到的记录
                if (fileHashDto != null && !minioUtil.exists(fileHashDto.getObjectKey())) {
                    fileHashDto = null;
                }
                SysFile sysFile = new SysFile();
                if (fileHashDto != null) {
                    BeanUtils.copyProperties(fileHashDto, sysFile);
                } else {
                    long fileSize = multipartFile.getSize();
                    String fileSuffix = FileNameUtil.suffixOf(originalName);
                    String fileKey = IdUtil.fastSimpleUUID();
                    String objectKey = minioUtil.getObjectKey(fileKey, fileSuffix);
                    // 先写对象存储，成功后再落库
                    minioUtil.upload(multipartFile.getInputStream(), fileSize, objectKey);
                    sysFile.setFileKey(fileKey);
                    sysFile.setFileSuffix(fileSuffix);
                    sysFile.setLocation(BaseConstant.Minio.MINIO);
                    sysFile.setBucket(minioProperty.getDefaultBucket());
                    sysFile.setObjectKey(objectKey);
                    sysFile.setFileSize(fileSize);
                    sysFile.setFileHash(fileMd5);
                }
                sysFile.setOriginalName(originalName);
                sysFile.setFolderId(requireFolder(folderId));
                sysFile.setId(null);
                sysFileMapper.insert(sysFile);
                return buildUploadVO(sysFile.getId(), originalName);
            } catch (IOException e) {
                throw new GlobalException(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public FileDetailDTO getFileDetailById(Long id) {
        return sysFileMapper.getFileDetailById(id);
    }

    @Override
    public void deleteFile(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            // 记录不存在，保持幂等
            return;
        }

        sysFileMapper.deleteById(id);

        // 秒传场景下同一个 objectKey 会被多条记录引用，只有最后一条引用被删除时才清理对象
        Long refCount = sysFileMapper.selectCount(
                Wrappers.lambdaQuery(SysFile.class).eq(SysFile::getObjectKey, sysFile.getObjectKey()));
        if (refCount != null && refCount == 0) {
            String bucket = StringUtils.isBlank(sysFile.getBucket())
                    ? minioProperty.getDefaultBucket()
                    : sysFile.getBucket();
            fileDeleteAsync.deleteObject(bucket, sysFile.getObjectKey());
        }
    }

    @Override
    public PageVO<FilesVO> getFiles(FilesDTO request) {

        IPage<FilesVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysFileMapper.getFiles(page, request);

        Map<String, String> fileStorageLocation = sysDictDataService.getDictDataMap(DictConstant.FILE_STORAGE_LOCATION);

        page.getRecords().forEach(item -> item.setLocationName(fileStorageLocation.get(String.valueOf(item.getLocation()))));

        PageVO<FilesVO> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public PageVO<FilesVO> getMyFiles(FilesDTO request) {
        IPage<FilesVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        Long userId = securityUtil.getUserInfo().getId();
        page = sysFileMapper.getMyFiles(page, request, userId);

        Map<String, String> fileStorageLocation = sysDictDataService.getDictDataMap(DictConstant.FILE_STORAGE_LOCATION);

        page.getRecords().forEach(item -> item.setLocationName(fileStorageLocation.get(String.valueOf(item.getLocation()))));

        PageVO<FilesVO> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    /**
     * nginx额外配置如下
     * 备注：proxy_set_header # 核心2：修改Host为MinIO自身地址（必须和生成签名时的Host一致，否则签名不匹配）
     * @param id
     * @param isInline
     * @return
     */
    @Override
    public String getFileUrl(Long id, String isInline) {
        String redisKey = RedisKeyPrefix.FILE_SIGNATURE_URL.getPrefix() + id;
        String url = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(url)) {
            SysFile sysFile = sysFileMapper.selectById(id);

            if ("1".equals(isInline)) {
                Map<String, String> headerMap = new HashMap<>();
                String value = sysFile.getFileSuffix().toLowerCase().replaceFirst("\\.", "");
                String label = sysDictDataService.getDictDataByValue(value, DictConstant.INLINE_FILE_TYPE);
                headerMap.put("response-content-type", label);
                headerMap.put("response-content-disposition", "inline");
                url = minioUtil.generateUrl(sysFile.getBucket(), sysFile.getObjectKey(), headerMap);
            } else {
                url = minioUtil.generateUrl(sysFile.getBucket(), sysFile.getObjectKey());
            }

            // 减5是为了防止minio签名过期，redis未过期，导致获取失败
            redisTemplate.opsForValue().set(redisKey, url, minioProperty.getExpire() - 5, TimeUnit.SECONDS);
        }
        return replaceMinioUrlWithProxy(url);
    }

    /**
     * 将MinIO原始URL替换为nginx代理前缀 /download
     * 使用URI替换更安全，避免字符串误替换
     */
    private String replaceMinioUrlWithProxy(String minioUrl) {
        try {
            URI uri = new URI(minioUrl);
            // 只替换 scheme + authority（主机:端口），保留路径和查询参数
            return "/download" + uri.getRawPath() + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
        } catch (URISyntaxException e) {
            // 降级处理：如果URL格式异常，回退到原逻辑，但使用正则确保只替换前缀
            return minioUrl.replaceFirst("^" + Pattern.quote(minioProperty.getUrl()), "/download");
        }
    }

    /**
     * 合并后的文件入库。
     * 与旧实现的关键差别：不再 @Async 后台传 MinIO，而是同步上传成功并确认对象存在后才 insert，
     * 否则列表里会出现"能看不能下载"的幽灵文件。
     */
    @Override
    public UploadFileVO mergeChunk(File fileTemp, String fileMd5, String originalName, Long folderId) {
        long fileSize = fileTemp.length();
        Long userId = securityUtil.getUserInfo().getId();
        String lockKey = userId + ":" + fileMd5 + ":" + originalName;
        synchronized (uploadLock(lockKey)) {
            Long existingId = sysFileMapper.getFileIdByHashAndOwner(fileMd5, originalName, userId);
            if (existingId != null) {
                return buildUploadVO(existingId, originalName);
            }

            String fileKey = IdUtil.fastSimpleUUID();
            String fileSuffix = FileNameUtil.suffixOf(originalName);
            String objectKey;
            FileHashDTO sameHash = sysFileMapper.getFileByHash(fileMd5);
            if (sameHash != null && minioUtil.exists(sameHash.getObjectKey())) {
                // 并发下另一个任务已把同一内容写完，复用对象即可
                objectKey = sameHash.getObjectKey();
            } else {
                objectKey = minioUtil.getObjectKey(fileKey, fileSuffix);
                try (InputStream is = Files.newInputStream(fileTemp.toPath())) {
                    minioUtil.upload(is, fileSize, objectKey);
                } catch (IOException e) {
                    throw new GlobalException("文件写入存储失败：" + e.getMessage());
                }
                if (!minioUtil.exists(objectKey)) {
                    minioUtil.delete(objectKey);
                    throw new GlobalException("文件存储校验失败，请重试");
                }
            }

            SysFile sysFile = new SysFile();
            sysFile.setFileKey(fileKey);
            sysFile.setFileSuffix(fileSuffix);
            sysFile.setLocation(BaseConstant.Minio.MINIO);
            sysFile.setBucket(minioProperty.getDefaultBucket());
            sysFile.setObjectKey(objectKey);
            sysFile.setOriginalName(originalName);
            sysFile.setFileSize(fileSize);
            sysFile.setFileHash(fileMd5);
            sysFile.setFolderId(requireFolder(folderId));
            sysFileMapper.insert(sysFile);
            return buildUploadVO(sysFile.getId(), originalName);
        }
    }

    /**
     * 秒传：命中同一内容时按「当前用户 + 当前文件名」复用已有记录，
     * 避免重复点击/并发产生重复行；同时保留每个用户各自的文件名。
     */
    @Override
    public String getFileByMd5(String fileMd5, String originalName, Long folderId) {
        Long userId = securityUtil.getUserInfo().getId();
        String lockKey = userId + ":" + fileMd5 + ":" + originalName;
        synchronized (uploadLock(lockKey)) {
            Long existingId = sysFileMapper.getFileIdByHashAndOwner(fileMd5, originalName, userId);
            if (existingId != null) {
                return String.valueOf(existingId);
            }
            SysFile source = sysFileMapper.getFileByMd5(fileMd5);
            // 对象已被删除时不能秒传，让前端走正常上传重新写入存储
            if (source == null || !minioUtil.exists(source.getObjectKey())) {
                return null;
            }
            SysFile sysFile = new SysFile();
            sysFile.setFileKey(source.getFileKey());
            sysFile.setFileSuffix(source.getFileSuffix());
            sysFile.setLocation(source.getLocation());
            sysFile.setBucket(source.getBucket());
            sysFile.setObjectKey(source.getObjectKey());
            sysFile.setFileSize(source.getFileSize());
            sysFile.setFileHash(fileMd5);
            sysFile.setOriginalName(originalName);
            sysFile.setFolderId(requireFolder(folderId));
            sysFileMapper.insert(sysFile);
            return String.valueOf(sysFile.getId());
        }
    }

    @Override
    public FolderContentVO listByFolder(FileListDTO request) {
        FolderContentVO vo = new FolderContentVO();
        String keyword = request.getKeyword();
        boolean searching = StringUtils.isNotBlank(keyword);

        // 当前文件夹与面包屑（搜索模式下仍然展示当前路径，方便返回）
        Long folderId = request.getFolderId();
        SysFolder current = folderId == null ? null : sysFolderMapper.selectById(folderId);
        if (folderId != null && current == null) {
            throw new GlobalException("文件夹不存在或已被删除");
        }
        vo.setCurrentFolder(current == null ? null : toFolderVO(current, 0L));
        vo.setBreadcrumb(buildBreadcrumb(current));

        // 子文件夹：搜索模式下不展示，避免和全局搜索结果混淆
        if (!searching) {
            var wrapper = Wrappers.lambdaQuery(SysFolder.class);
            if (current == null) {
                wrapper.isNull(SysFolder::getParentId);
            } else {
                wrapper.eq(SysFolder::getParentId, folderId);
            }
            wrapper.orderByAsc(SysFolder::getName);
            List<SysFolder> folders = sysFolderMapper.selectList(wrapper);
            vo.setFolders(listFoldersWithCounts(folders));
        } else {
            vo.setFolders(List.of());
        }

        // 文件分页：搜索模式下忽略 folderId 全局搜索
        IPage<FilesVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysFileMapper.getFilesByFolder(page, searching ? null : folderId, keyword);
        Map<String, String> fileStorageLocation = sysDictDataService.getDictDataMap(DictConstant.FILE_STORAGE_LOCATION);
        page.getRecords().forEach(item -> item.setLocationName(fileStorageLocation.get(String.valueOf(item.getLocation()))));

        PageVO<FilesVO> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        vo.setFiles(pageVo);
        return vo;
    }

    @Override
    public void addFolder(String name, Long parentId) {
        if (parentId != null && sysFolderMapper.selectById(parentId) == null) {
            throw new GlobalException("目标文件夹不存在或已被删除");
        }
        checkFolderNameUnique(name, parentId, null);
        SysFolder folder = new SysFolder();
        folder.setName(name);
        folder.setParentId(parentId);
        sysFolderMapper.insert(folder);
    }

    @Override
    public void renameFolder(Long id, String name) {
        if (BaseConstant.SYSTEM_FOLDER_ID.equals(id)) {
            throw new GlobalException("系统文件夹不允许重命名");
        }
        SysFolder folder = sysFolderMapper.selectById(id);
        if (folder == null) {
            throw new GlobalException("文件夹不存在或已被删除");
        }
        checkFolderNameUnique(name, folder.getParentId(), id);
        folder.setName(name);
        sysFolderMapper.updateById(folder);
    }

    @Override
    public void deleteFolder(Long id) {
        if (BaseConstant.SYSTEM_FOLDER_ID.equals(id)) {
            throw new GlobalException("系统文件夹不允许删除");
        }
        SysFolder folder = sysFolderMapper.selectById(id);
        if (folder == null) {
            return;
        }
        // 非空禁删：子文件夹、直属文件任一存在即拒绝，避免误删整棵子树
        Long childCount = sysFolderMapper.selectCount(Wrappers.lambdaQuery(SysFolder.class)
                .eq(SysFolder::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new GlobalException("文件夹内还有子文件夹，请先清空后再删除");
        }
        Long fileCount = sysFileMapper.selectCount(Wrappers.lambdaQuery(SysFile.class)
                .eq(SysFile::getFolderId, id));
        if (fileCount != null && fileCount > 0) {
            throw new GlobalException("文件夹内还有文件，请先清空后再删除");
        }
        sysFolderMapper.deleteById(id);
    }

    @Override
    public List<FolderTreeNodeVO> folderTree() {
        List<SysFolder> all = sysFolderMapper.selectList(Wrappers.lambdaQuery(SysFolder.class)
                .orderByAsc(SysFolder::getName));
        // 伪根键：不与雪花 id 冲突，parent_id 为 NULL 的文件夹挂在它下面
        final long rootKey = -1L;
        Map<Long, List<SysFolder>> byParent = all.stream()
                .collect(Collectors.groupingBy(folder -> folder.getParentId() == null ? rootKey : folder.getParentId()));
        return buildFolderTree(rootKey, byParent);
    }

    private List<FolderTreeNodeVO> buildFolderTree(Long parentId, Map<Long, List<SysFolder>> byParent) {
        List<FolderTreeNodeVO> nodes = new ArrayList<>();
        for (SysFolder folder : byParent.getOrDefault(parentId, List.of())) {
            FolderTreeNodeVO node = new FolderTreeNodeVO();
            node.setId(String.valueOf(folder.getId()));
            node.setName(folder.getName());
            node.setChildren(buildFolderTree(folder.getId(), byParent));
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public void moveFile(Long fileId, Long targetFolderId) {
        SysFile file = sysFileMapper.selectById(fileId);
        if (file == null) {
            throw new GlobalException("文件不存在或已被删除");
        }
        Long target = requireFolder(targetFolderId);
        if (target.equals(file.getFolderId())) {
            throw new GlobalException("文件已在该文件夹中");
        }
        SysFile update = new SysFile();
        update.setId(fileId);
        update.setFolderId(target);
        sysFileMapper.updateById(update);
    }

    /**
     * 解析上传目标文件夹：未指定（根目录上传）时默认归入系统文件夹；
     * 两者都校验存在性，避免写入一条归属失效的记录
     */
    private Long requireFolder(Long folderId) {
        Long target = folderId == null ? BaseConstant.SYSTEM_FOLDER_ID : folderId;
        if (sysFolderMapper.selectById(target) == null) {
            throw new GlobalException("目标文件夹不存在或已被删除");
        }
        return target;
    }

    private void checkFolderNameUnique(String name, Long parentId, Long excludeId) {
        var wrapper = Wrappers.lambdaQuery(SysFolder.class)
                .eq(SysFolder::getName, name)
                .ne(excludeId != null, SysFolder::getId, excludeId);
        if (parentId == null) {
            wrapper.isNull(SysFolder::getParentId);
        } else {
            wrapper.eq(SysFolder::getParentId, parentId);
        }
        Long sameName = sysFolderMapper.selectCount(wrapper);
        if (sameName != null && sameName > 0) {
            throw new GlobalException("同级目录下已存在同名文件夹");
        }
    }

    private FolderVO toFolderVO(SysFolder folder, Long fileCount) {
        FolderVO vo = new FolderVO();
        vo.setId(String.valueOf(folder.getId()));
        vo.setName(folder.getName());
        vo.setParentId(folder.getParentId() == null ? null : String.valueOf(folder.getParentId()));
        vo.setCreateTime(folder.getCreateTime());
        vo.setFileCount(fileCount);
        return vo;
    }

    private List<FolderVO> listFoldersWithCounts(List<SysFolder> folders) {
        if (folders.isEmpty()) {
            return List.of();
        }
        List<Long> ids = folders.stream().map(SysFolder::getId).toList();
        Map<Long, Long> countMap = new HashMap<>();
        for (FolderFileCountDTO dto : sysFolderMapper.countFilesInFolders(ids)) {
            countMap.put(dto.getFolderId(), dto.getCnt());
        }
        return folders.stream()
                .map(folder -> toFolderVO(folder, countMap.getOrDefault(folder.getId(), 0L)))
                .toList();
    }

    /**
     * 面包屑：根目录 → 当前层；逐层向上查询，深度兜底防止脏数据成环
     */
    private List<FolderVO> buildBreadcrumb(SysFolder current) {
        List<FolderVO> path = new java.util.ArrayList<>();
        SysFolder node = current;
        int depth = 0;
        while (node != null && depth < 32) {
            path.add(toFolderVO(node, 0L));
            node = node.getParentId() == null ? null : sysFolderMapper.selectById(node.getParentId());
            depth++;
        }
        java.util.Collections.reverse(path);
        return path;
    }

    private UploadFileVO buildUploadVO(Long id, String originalName) {
        UploadFileVO vo = new UploadFileVO();
        vo.setFileName(originalName);
        vo.setFileId(String.valueOf(id));
        return vo;
    }
}
