package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysFile;
import com.soft.sys.model.dto.FileDetailDTO;
import com.soft.sys.model.dto.FileHashDTO;
import com.soft.sys.model.request.FilesDTO;
import com.soft.sys.model.vo.FilesVO;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Mapper
* @createDate 2024-10-26 15:19:23
* @Entity com.soft.base.entity.SysFile
*/
public interface SysFileMapper extends BaseMapper<SysFile> {

    FileDetailDTO getFileDetailById(@Param("id") Long id);

    IPage<FilesVO> getFiles(IPage<FilesVO> page, @Param("request") FilesDTO request);

    FileHashDTO getFileByHash(@Param("hashCode") String hashCode);

    IPage<FilesVO> getMyFiles(IPage<FilesVO> page,
                               @Param("request") FilesDTO request,
                               @Param("userId") Long userId);

    SysFile getFileByMd5(@Param("fileMd5") String fileMd5);

    /** 秒传/合并幂等：查当前用户是否已用同名上传过同一内容 */
    Long getFileIdByHashAndOwner(@Param("fileMd5") String fileMd5,
                                 @Param("originalName") String originalName,
                                 @Param("userId") Long userId);
}




