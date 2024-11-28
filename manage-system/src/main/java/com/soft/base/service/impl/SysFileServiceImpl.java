package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobelException;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.request.FilesRequest;
import com.soft.base.service.SysFileService;
import com.soft.base.utils.MinioUtil;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

    @Autowired
    public SysFileServiceImpl(SysFileMapper sysFileMapper, MinioUtil minioUtil) {
        this.sysFileMapper = sysFileMapper;
        this.minioUtil = minioUtil;
    }

    @Override
    public void uploadFile(MultipartFile multipartFile) throws GlobelException{
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)) {
                throw new GlobelException("文件名不能为空");
            }
            uploadFile(multipartFile.getInputStream(), minioUtil.fileKeyGen(),
                    originalFilename.substring(originalFilename.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX)),
                    multipartFile.getSize(), originalFilename);
        } catch (Exception e) {
            throw new GlobelException(e.getMessage());
        }
    }

    /**
     * 异步上传文件
     * @param inputStream
     * @param fileKey
     * @param fileSuffix
     * @param fileSize
     * @param originalFilename
     */
    @Async
    protected void uploadFile(InputStream inputStream, String fileKey, String fileSuffix, Long fileSize, String originalFilename) {
        String objectKey = minioUtil.upload(inputStream, fileKey, fileSuffix, fileSize);
        SysFile sysFile = new SysFile();
        sysFile.setFileKey(fileKey);
        sysFile.setFileSuffix(fileSuffix);
        sysFile.setLocation(BaseConstant.DEFAULT_STORAGE_LOCATION);
        sysFile.setObjectKey(objectKey);
        sysFile.setOriginalName(originalFilename);
        sysFile.setFileSize(fileSize);
        sysFileMapper.insert(sysFile);
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




