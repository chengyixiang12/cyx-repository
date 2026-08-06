package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysScheduleRecord;
import com.soft.sys.model.vo.GetQuartzRecordListVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* @author 程益祥
* @description 针对表【sys_schedule_record(定时任务执行记录)】的数据库操作Mapper
* @createDate 2026-04-23 14:11:58
* @Entity com.soft.base.entity.SysScheduleRecord
*/
public interface SysScheduleRecordMapper extends BaseMapper<SysScheduleRecord> {

    IPage<GetQuartzRecordListVO> getQuartzRecordList(IPage<GetQuartzRecordListVO> page,
                                                     @Param("keyword") String keyword,
                                                     @Param("jobId") String jobId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);

    String getLogDetail(@Param("id") Long id);
}




