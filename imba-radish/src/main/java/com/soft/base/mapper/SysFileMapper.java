package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.dto.FileDetailDto;
import com.soft.base.entity.SysFile;
import com.soft.base.request.FilesRequest;
import com.soft.base.vo.FilesVo;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_file】的数据库操作Mapper
* @createDate 2024-10-26 15:19:23
* @Entity com.soft.base.entity.SysFile
*/
public interface SysFileMapper extends BaseMapper<SysFile> {

    FileDetailDto getFileDetailById(@Param("id") Long id);

    IPage<FilesVo> getFiles(IPage<FilesVo> page, @Param("request") FilesRequest request);
}




