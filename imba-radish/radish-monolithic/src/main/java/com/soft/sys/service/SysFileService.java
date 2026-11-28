package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysFile;
import com.soft.sys.model.dto.FileDetailDTO;
import com.soft.sys.model.request.FileListDTO;
import com.soft.sys.model.request.FilesDTO;
import com.soft.sys.model.vo.FolderContentVO;
import com.soft.sys.model.vo.FolderTreeNodeVO;
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

    UploadFileVO uploadFile(MultipartFile multipartFile, String fileMd5, Long folderId);

    FileDetailDTO getFileDetailById(Long id);

    void deleteFile(Long id);

    PageVO<FilesVO> getFiles(FilesDTO request);

    PageVO<FilesVO> getMyFiles(FilesDTO request);

    /**
     * 文件夹层内容：子文件夹 + 文件分页 + 面包屑
     */
    FolderContentVO listByFolder(FileListDTO request);

    void addFolder(String name, Long parentId);

    void renameFolder(Long id, String name);

    /**
     * 删除文件夹（非空禁删：存在子文件夹或直属文件时拒绝）
     */
    void deleteFolder(Long id);

    /**
     * 全量文件夹树（移动文件选择目标用）
     */
    List<FolderTreeNodeVO> folderTree();

    /**
     * 移动文件到目标文件夹；targetFolderId 为空时按根目录上传同样的规则归入系统文件夹
     */
    void moveFile(Long fileId, Long targetFolderId);

    String getFileUrl(Long id, String isInline);

    UploadFileVO mergeChunk(File fileTemp, String fileMd5, String originalName, Long folderId);

    String getFileByMd5(String fileMd5, String fileName, Long folderId);

}
