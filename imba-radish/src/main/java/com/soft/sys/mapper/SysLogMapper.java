package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysLog;
import com.soft.sys.model.request.LogsDTO;
import com.soft.sys.model.vo.GetLogVO;
import com.soft.sys.model.vo.LogsVO;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Mapper
* @createDate 2024-11-21 10:54:37
* @Entity com.soft.base.entity.SysLog
*/
public interface SysLogMapper extends BaseMapper<SysLog> {

    IPage<LogsVO> getLogs(IPage<LogsVO> page, @Param("request") LogsDTO request);

    GetLogVO getLog(@Param("id") Long id);
}




