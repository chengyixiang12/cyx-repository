package com.soft.base.service;

import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.exception.GlobelException;
import com.soft.base.request.FilesRequest;
import com.soft.base.vo.FilesVo;
import com.soft.base.vo.PageVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Service
* @createDate 2024-10-26 15:19:23
*/
public interface SysFileService extends IService<SysFile> {

    void uploadFile(MultipartFile multipartFile) throws GlobelException;

    FileDetailDto getFileDetailById(Long id);

    void deleteFile(Long id);

    PageVo<FilesVo> getFiles(FilesRequest request);
}
