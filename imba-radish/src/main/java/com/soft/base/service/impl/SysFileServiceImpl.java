package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.model.dto.FileDetailDto;
import com.soft.base.model.dto.FileHashDto;
import com.soft.base.model.request.FilesRequest;
import com.soft.base.model.vo.FilesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.UploadFileVo;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.utils.UniversalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Service实现
* @createDate 2024-10-26 15:19:23
*/
@Service
@Slf4j
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
    implements SysFileService{

    private final SysFileMapper sysFileMapper;

    private final MinioUtil minioUtil;

    private final UniversalUtil universalUtil;

    @Autowired
    public SysFileServiceImpl(SysFileMapper sysFileMapper, MinioUtil minioUtil, UniversalUtil universalUtil) {
        this.sysFileMapper = sysFileMapper;
        this.minioUtil = minioUtil;
        this.universalUtil = universalUtil;
    }

    @Override
    public UploadFileVo uploadFile(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        UploadFileVo uploadFileVo = new UploadFileVo();
        SysFile sysFile = new SysFile();
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new GlobalException("文件名不能为空");
        }

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
            if (!originalFilename.equals(fileHashDto.getOriginalName())) {
                sysFileMapper.insert(sysFile);
            }
        } else {
            long fileSize = multipartFile.getSize();
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX));
            String fileKey = universalUtil.fileKeyGen();
            String objectKey = minioUtil.upload(multipartFile.getInputStream(), fileKey, fileSuffix, fileSize);

            sysFile.setFileKey(fileKey);
            sysFile.setFileSuffix(fileSuffix);
            sysFile.setLocation(BaseConstant.DEFAULT_STORAGE_LOCATION);
            sysFile.setObjectKey(objectKey);
            sysFile.setOriginalName(originalFilename);
            sysFile.setFileSize(fileSize);
            sysFileMapper.insert(sysFile);

        }
        uploadFileVo.setFileId(sysFile.getId());
        uploadFileVo.setFileName(sysFile.getOriginalName());

        return uploadFileVo;
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
    public PageVo<FilesVo> getFiles(FilesRequest request) {
        IPage<FilesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysFileMapper.getFiles(page, request);
        PageVo<FilesVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }
}




