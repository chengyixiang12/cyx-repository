package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysLog;
import com.soft.base.model.request.LogsRequest;
import com.soft.base.model.vo.GetLogVo;
import com.soft.base.model.vo.LogsVo;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Mapper
* @createDate 2024-11-21 10:54:37
* @Entity com.soft.base.entity.SysLog
*/
public interface SysLogMapper extends BaseMapper<SysLog> {

    IPage<LogsVo> getLogs(IPage<LogsVo> page, @Param("request") LogsRequest request);

    GetLogVo getLog(@Param("id") Long id);
}




