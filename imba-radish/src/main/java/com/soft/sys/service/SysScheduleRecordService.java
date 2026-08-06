package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysScheduleRecord;
import com.soft.sys.model.request.GetQuartzRecordListDTO;
import com.soft.sys.model.vo.GetQuartzRecordListVO;
import com.soft.sys.model.vo.PageVO;

/**
* @author 程益祥
* @description 针对表【sys_schedule_record(定时任务执行记录)】的数据库操作Service
* @createDate 2026-04-23 14:11:58
*/
public interface SysScheduleRecordService extends IService<SysScheduleRecord> {

    PageVO<GetQuartzRecordListVO> getQuartzRecordList(GetQuartzRecordListDTO request);

    String getLogDetail(Long id);
}
