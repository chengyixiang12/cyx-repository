package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.async.FileUploadAsync;
import com.soft.base.constants.BaseConstant;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobalException;
import com.soft.base.mapper.SysFileMapper;
import com.soft.base.request.FilesRequest;
import com.soft.base.service.SysFileService;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
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

    private final FileUploadAsync fileUploadAsync;

    @Autowired
    public SysFileServiceImpl(SysFileMapper sysFileMapper, FileUploadAsync fileUploadAsync) {
        this.sysFileMapper = sysFileMapper;
        this.fileUploadAsync = fileUploadAsync;
    }

    @Override
    public void uploadFile(MultipartFile multipartFile) throws GlobalException {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            if (StringUtils.isBlank(originalFilename)) {
                throw new GlobalException("文件名不能为空");
            }
            fileUploadAsync.uploadFile(multipartFile.getInputStream(),
                    originalFilename.substring(originalFilename.lastIndexOf(BaseConstant.FILE_POINT_SUFFIX)),
                    multipartFile.getSize(), originalFilename, this);
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




