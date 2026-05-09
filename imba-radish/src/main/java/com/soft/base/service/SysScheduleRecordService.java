package com.soft.base.service;

import com.soft.base.entity.SysScheduleRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.request.GetQuartzRecordListRequest;
import com.soft.base.model.vo.GetQuartzRecordListVo;
import com.soft.base.model.vo.PageVO;

/**
* @author 程益祥
* @description 针对表【sys_schedule_record(定时任务执行记录)】的数据库操作Service
* @createDate 2026-04-23 14:11:58
*/
public interface SysScheduleRecordService extends IService<SysScheduleRecord> {

    PageVO<GetQuartzRecordListVo> getQuartzRecordList(GetQuartzRecordListRequest request);

    String getLogDetail(Long id);
}
