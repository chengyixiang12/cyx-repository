package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.dto.LogDto;
import com.soft.base.entity.SysLog;
import com.soft.base.request.LogsRequest;
import com.soft.base.vo.LogsVo;
import com.soft.base.vo.PageVo;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Service
* @createDate 2024-11-21 10:54:37
*/
public interface SysLogService extends IService<SysLog> {

    void saveLog(LogDto logDto);

    PageVo<LogsVo> getLogs(LogsRequest request);
}
