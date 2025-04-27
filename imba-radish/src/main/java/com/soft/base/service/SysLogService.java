package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysLog;
import com.soft.base.model.dto.LogDto;
import com.soft.base.model.request.LogsRequest;
import com.soft.base.model.vo.GetLogVo;
import com.soft.base.model.vo.LogsVo;
import com.soft.base.model.vo.PageVo;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Service
* @createDate 2024-11-21 10:54:37
*/
public interface SysLogService extends IService<SysLog> {

    void saveLog(LogDto logDto);

    PageVo<LogsVo> getLogs(LogsRequest request);

    GetLogVo getLog(Long id);

    void deleteLog(Long id);
}
