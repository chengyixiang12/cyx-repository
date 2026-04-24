package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysScheduleRecord;
import com.soft.base.model.request.GetQuartzRecordListRequest;
import com.soft.base.model.vo.GetQuartzRecordListVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysScheduleRecordService;
import com.soft.base.mapper.SysScheduleRecordMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author 程益祥
* @description 针对表【sys_schedule_record(定时任务执行记录)】的数据库操作Service实现
* @createDate 2026-04-23 14:11:58
*/
@Service
@RequiredArgsConstructor
public class SysScheduleRecordServiceImpl extends ServiceImpl<SysScheduleRecordMapper, SysScheduleRecord>
    implements SysScheduleRecordService{

    private final SysScheduleRecordMapper sysScheduleRecordMapper;

    @Override
    public PageVo<GetQuartzRecordListVo> getQuartzRecordList(GetQuartzRecordListRequest request) {
        IPage<GetQuartzRecordListVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysScheduleRecordMapper.getQuartzRecordList(page, request.getKeyword(), request.getJobId(), request.getStartTime(), request.getEndTime());
        PageVo<GetQuartzRecordListVo> pageVo = new PageVo<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public String getLogDetail(Long id) {
        return sysScheduleRecordMapper.getLogDetail(id);
    }
}




