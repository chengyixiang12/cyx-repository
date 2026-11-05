package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysMetricSample;
import com.soft.sys.model.dto.MetricRowDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cyx
 * @description 针对表【sys_metric_sample(监控指标原始样本)】的数据库操作Mapper
 * @date 2026-11-05
 */
public interface SysMetricSampleMapper extends BaseMapper<SysMetricSample> {

    /**
     * 查询时间范围内的原始样本
     */
    List<MetricRowDTO> selectRawRange(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    /**
     * 按 5 分钟桶聚合到 sys_metric_rollup
     */
    int insert5mRollup(@Param("start") LocalDateTime start,
                       @Param("end") LocalDateTime end);

    /**
     * 分批删除过期样本
     */
    int deleteBefore(@Param("time") LocalDateTime time,
                     @Param("batch") int batch);
}
