package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.entity.SysLog;
import com.soft.sys.mapper.SysLogMapper;
import com.soft.sys.model.dto.LogDto;
import com.soft.sys.model.request.LogsRequest;
import com.soft.sys.model.vo.GetLogVo;
import com.soft.sys.model.vo.LogsVo;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author cyq
* @description 针对表【sys_log(日志表)】的数据库操作Service实现
* @createDate 2024-11-21 10:54:37
*/
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog>
    implements SysLogService {

    private final SysLogMapper sysLogMapper;

    public SysLogServiceImpl(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    @Override
    public void saveLog(LogDto logDto) {
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(logDto, sysLog);
        sysLogMapper.insert(sysLog);
    }

    @Override
    public PageVO<LogsVo> getLogs(LogsRequest request) {
        IPage<LogsVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysLogMapper.getLogs(page, request);
        PageVO<LogsVo> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public GetLogVo getLog(Long id) {
        return sysLogMapper.getLog(id);
    }

    @Override
    public void deleteLog(Long id) {
        sysLogMapper.deleteById(id);
    }
}




