package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.request.FilesRequest;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.utils.UniversalUtil;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.UploadFileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public UploadFileVo uploadFile(MultipartFile multipartFile) throws GlobalException {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)) {
                throw new GlobalException("文件名不能为空");
            }
            long fileSize = multipartFile.getSize();
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX));

            String fileKey = universalUtil.fileKeyGen();
            String objectKey = minioUtil.upload(multipartFile.getInputStream(), fileKey, fileSuffix, fileSize);
            SysFile sysFile = new SysFile();
            sysFile.setFileKey(fileKey);
            sysFile.setFileSuffix(fileSuffix);
            sysFile.setLocation(BaseConstant.DEFAULT_STORAGE_LOCATION);
            sysFile.setObjectKey(objectKey);
            sysFile.setOriginalName(originalFilename);
            sysFile.setFileSize(fileSize);
            sysFileMapper.insert(sysFile);

            UploadFileVo uploadFileVo = new UploadFileVo();
            uploadFileVo.setFileId(sysFile.getId());
            uploadFileVo.setFileName(sysFile.getOriginalName());
            return uploadFileVo;
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
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
    public PageVo<FilesVo> getFiles(FilesRequest request) {
        IPage<FilesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysFileMapper.getFiles(page, request);
        PageVo<FilesVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setResult(page.getRecords());
        return pageVo;
    }
}




