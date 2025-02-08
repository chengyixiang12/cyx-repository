package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.soft.base.exception.GlobalException;
import com.soft.base.request.FilesRequest;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Service
* @createDate 2024-10-26 15:19:23
*/
public interface SysFileService extends IService<SysFile> {

    UploadFileVo uploadFile(MultipartFile multipartFile) throws GlobalException;

    FileDetailDto getFileDetailById(Long id);

    void deleteFile(Long id);

    PageVo<FilesVo> getFiles(FilesRequest request);
}
