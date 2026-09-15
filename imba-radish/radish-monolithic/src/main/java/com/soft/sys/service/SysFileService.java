package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysFile;
import com.soft.sys.model.dto.FileDetailDTO;
import com.soft.sys.model.dto.SelectDeletedFileDTO;
import com.soft.sys.model.request.FilesDTO;
import com.soft.sys.model.vo.FilesVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.UploadFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Service
* @createDate 2024-10-26 15:19:23
*/
public interface SysFileService extends IService<SysFile> {

    UploadFileVO uploadFile(MultipartFile multipartFile, String fileMd5);

    FileDetailDTO getFileDetailById(Long id);

    void deleteFile(Long id);

    PageVO<FilesVO> getFiles(FilesDTO request);

    List<SelectDeletedFileDTO> selectDeletedFiles();

    PageVO<FilesVO> getMyFiles(FilesDTO request);

    void deleteRealByIds(List<Long> list);

    String getFileUrl(Long id, String isInline);

    UploadFileVO mergeChunk(File fileTemp, String fileMd5);

    String getFileByMd5(String fileMd5, String fileName);

}
