package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysLog;
import com.soft.sys.model.dto.LogDto;
import com.soft.sys.model.request.LogsRequest;
import com.soft.sys.model.vo.GetLogVo;
import com.soft.sys.model.vo.LogsVo;
import com.soft.sys.model.vo.PageVO;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Service
* @createDate 2024-11-21 10:54:37
*/
public interface SysLogService extends IService<SysLog> {

    void saveLog(LogDto logDto);

    PageVO<LogsVo> getLogs(LogsRequest request);

    GetLogVo getLog(Long id);

    void deleteLog(Long id);
}
