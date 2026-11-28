package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysFolder;
import com.soft.sys.model.dto.FolderFileCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cyx
 * @description 针对表【sys_folder】的数据库操作Mapper
 * @date 2026-11-28
 */
public interface SysFolderMapper extends BaseMapper<SysFolder> {

    /**
     * 批量统计各文件夹的直属文件数量
     */
    List<FolderFileCountDTO> countFilesInFolders(@Param("folderIds") List<Long> folderIds);
}
