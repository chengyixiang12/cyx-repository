package com.soft.base.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.async.FileUploadAsync;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.model.dto.FileDetailDto;
import com.soft.base.model.dto.FileHashDto;
import com.soft.base.model.dto.SelectDeletedFileDto;
import com.soft.base.model.request.FilesRequest;
import com.soft.base.model.vo.FilesVo;
import com.soft.base.model.vo.PageVO;
import com.soft.base.model.vo.UploadAvatarVo;
import com.soft.base.model.vo.UploadFileVo;
import com.soft.base.properties.MinioProperty;
import com.soft.base.service.SysDictDataService;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final SysFileMapper sysFileMapper;

    private final MinioUtil minioUtil;

    private final MinioProperty minioProperty;

    private final SysDictDataService sysDictDataService;

    private final SecurityUtil securityUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FileUploadAsync fileUploadAsync;

    @Override
    public UploadFileVo uploadFile(MultipartFile multipartFile, String fileMd5) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        SysFile sysFile = new SysFile();
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new GlobalException("文件名不能为空");
        }

        try {
            FileHashDto fileHashDto = sysFileMapper.getFileByHash(fileMd5);
            if (fileHashDto != null) {
                BeanUtils.copyProperties(fileHashDto, sysFile);
            } else {
                long fileSize = multipartFile.getSize();
                String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileKey = IdUtil.fastSimpleUUID();
                String objectKey = minioUtil.getObjectKey(fileKey, fileSuffix);

                minioUtil.upload(multipartFile.getInputStream(), fileSize, objectKey);

                sysFile.setFileKey(fileKey);
                sysFile.setFileSuffix(fileSuffix);
                sysFile.setLocation(BaseConstant.Minio.MINIO);
                sysFile.setBucket(minioProperty.getDefaultBucket());
                sysFile.setObjectKey(objectKey);
                sysFile.setOriginalName(originalFilename);
                sysFile.setFileSize(fileSize);
                sysFile.setFileHash(fileMd5);
            }
            sysFileMapper.insert(sysFile);
            uploadFileVo.setFileId(String.valueOf(sysFile.getId()));
            uploadFileVo.setFileName(sysFile.getOriginalName());

            return uploadFileVo;
        } catch (IOException e) {
            throw new GlobalException(e.getLocalizedMessage());
        }
    }

    @Override
    public FileDetailDto getFileDetailById(Long id) {
        return sysFileMapper.getFileDetailById(id);
    }

    @Override
    public void deleteFile(Long id) {
        sysFileMapper.deleteById(id);
    }

    @Override
    public PageVO<FilesVo> getFiles(FilesRequest request) {

        IPage<FilesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysFileMapper.getFiles(page, request);

        Map<String, String> fileStorageLocation = sysDictDataService.getDictDataMap(1984484774405574660L);

        page.getRecords().forEach(item -> item.setLocationName(fileStorageLocation.get(String.valueOf(item.getLocation()))));

        PageVO<FilesVo> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public UploadAvatarVo uploadAvatar(MultipartFile multipartFile) {
        UploadAvatarVo uploadAvatarVo = new UploadAvatarVo();
        SysFile sysFile = new SysFile();
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new GlobalException("文件名不能为空");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(BaseConstant.TYPE_ALGORITHM);
            try (DigestInputStream dis = new DigestInputStream(multipartFile.getInputStream(), digest)) {
                byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
                int length = BaseConstant.BUFFER_SIZE;
                while (length != BaseConstant.FILE_OVER_SIGN) {
                    length = dis.read(buffer);
                }
            }
            byte[] hashBytes = digest.digest();
            String hashCode = new BigInteger(BaseConstant.SIGN_NUM_POSITIVE, hashBytes).toString(BaseConstant.SCALE_SIXTEEN);

            FileHashDto fileHashDto = sysFileMapper.getFileByHash(hashCode);

            if (fileHashDto != null) {
                BeanUtils.copyProperties(fileHashDto, sysFile);
                if (!originalFilename.equals(sysFile.getOriginalName())) {
                    sysFile.setOriginalName(originalFilename);
                }
                sysFileMapper.insert(sysFile);
            } else {
                long fileSize = multipartFile.getSize();
                String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileKey = IdUtil.fastSimpleUUID();
                String objectKey = minioUtil.getObjectKey(fileKey, fileSuffix);
                minioUtil.upload(multipartFile.getInputStream(), minioProperty.getAvatarBucket(), fileSize, objectKey);

                sysFile.setFileKey(fileKey);
                sysFile.setFileSuffix(fileSuffix);
                sysFile.setLocation(BaseConstant.Minio.MINIO);
                sysFile.setBucket(minioProperty.getAvatarBucket());
                sysFile.setObjectKey(objectKey);
                sysFile.setOriginalName(originalFilename);
                sysFile.setFileSize(fileSize);
                sysFileMapper.insert(sysFile);
            }
            uploadAvatarVo.setId(String.valueOf(sysFile.getId()));
            uploadAvatarVo.setUri(sysFile.getObjectKey());

            return uploadAvatarVo;
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SelectDeletedFileDto> selectDeletedFiles() {
        return sysFileMapper.selectDeletedFiles();
    }

    @Override
    public PageVO<FilesVo> getMyFiles(FilesRequest request) {
        IPage<FilesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        Long userId = securityUtil.getUserInfo().getId();
        page = sysFileMapper.getMyFiles(page, request, userId);

        Map<String, String> fileStorageLocation = sysDictDataService.getDictDataMap(1984484774405574660L);

        page.getRecords().forEach(item -> item.setLocationName(fileStorageLocation.get(String.valueOf(item.getLocation()))));

        PageVO<FilesVo> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public void deleteRealByIds(List<Long> list) {
        sysFileMapper.deleteRealByIds(list);
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
        String redisKey = RedisConstant.FILE_SIGNATURE_URL + id;
        String url = (String) redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(url)) {
            SysFile sysFile = sysFileMapper.selectById(id);


            if ("1".equals(isInline)) {
                Map<String, String> headerMap = new HashMap<>();
                String value = sysFile.getFileSuffix().toLowerCase().replaceFirst("\\.", "");
                final Long parentId = 2001266854774767618L;
                String label = sysDictDataService.getDictDataByValue(value, parentId);
                headerMap.put("response-content-type", label);
                headerMap.put("response-content-disposition", "inline");
                url = minioUtil.generateUrl(sysFile.getBucket(), sysFile.getObjectKey(), headerMap);
            } else {
                url = minioUtil.generateUrl(sysFile.getBucket(), sysFile.getObjectKey());
            }

            redisTemplate.opsForValue().set(redisKey, url);
            // 减5是为了防止minio签名过期，redis未过期，导致获取失败
            redisTemplate.expire(redisKey, minioProperty.getExpire() - 5, minioProperty.getTimeUnit());
        }
        return url.replaceFirst(minioProperty.getUrl(), "/download");
    }

    @Override
    public UploadFileVo mergeChunk(File fileTemp, String fileMd5) {
        String fileName = fileTemp.getName();
        String fileKey = IdUtil.fastSimpleUUID();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        long fileSize = fileTemp.length();
        String objectKey = minioUtil.getObjectKey(fileKey, fileSuffix);

        SysFile sysFile = new SysFile();
        sysFile.setFileKey(fileKey);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setLocation(BaseConstant.Minio.MINIO);
        sysFile.setBucket(minioProperty.getDefaultBucket());
        sysFile.setObjectKey(objectKey);
        sysFile.setOriginalName(fileName);
        sysFile.setFileSize(fileSize);
        sysFile.setFileSize(fileSize);
        sysFile.setFileHash(fileMd5);
        sysFileMapper.insert(sysFile);

        fileUploadAsync.fileUpload(fileTemp, objectKey, fileSize);

        UploadFileVo uploadFileVo = new UploadFileVo();
        uploadFileVo.setFileName(fileName);
        uploadFileVo.setFileId(String.valueOf(sysFile.getId()));

        return uploadFileVo;
    }

    @Override
    public String getFileByMd5(String fileMd5, String fileName) {
        SysFile sysFile = sysFileMapper.getFileByMd5(fileMd5);
        if (sysFile == null) {
            return null;
        }
        sysFile.setOriginalName(fileName);
        sysFileMapper.insert(sysFile);
        return String.valueOf(sysFile.getId());
    }
}




