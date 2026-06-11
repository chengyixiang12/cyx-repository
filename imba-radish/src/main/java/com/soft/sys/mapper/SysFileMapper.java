package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysFile;
import com.soft.sys.model.dto.FileDetailDto;
import com.soft.sys.model.dto.FileHashDto;
import com.soft.sys.model.dto.SelectDeletedFileDto;
import com.soft.sys.model.request.FilesRequest;
import com.soft.sys.model.vo.FilesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Mapper
* @createDate 2024-10-26 15:19:23
* @Entity com.soft.base.entity.SysFile
*/
public interface SysFileMapper extends BaseMapper<SysFile> {

    FileDetailDto getFileDetailById(@Param("id") Long id);

    IPage<FilesVo> getFiles(IPage<FilesVo> page, @Param("request") FilesRequest request);

    FileHashDto getFileByHash(@Param("hashCode") String hashCode);

    List<SelectDeletedFileDto> selectDeletedFiles();

    IPage<FilesVo> getMyFiles(IPage<FilesVo> page,
                               @Param("request") FilesRequest request,
                               @Param("userId") Long userId);

    void deleteRealByIds(@Param("ids") List<Long> ids);

    SysFile getFileByMd5(@Param("fileMd5") String fileMd5);
}




