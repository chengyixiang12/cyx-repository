package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysMetricRollup;
import com.soft.sys.model.dto.MetricRowDTO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cyx
 * @description 针对表【sys_metric_rollup(监控指标聚合数据)】的数据库操作Mapper
 * @date 2026-11-05
 */
public interface SysMetricRollupMapper extends BaseMapper<SysMetricRollup> {

    /**
     * 按粒度查询时间范围内的聚合数据
     */
    List<MetricRowDTO> selectRollupRange(@Param("granularity") String granularity,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    /**
     * 按 1 小时桶聚合 5 分钟数据
     */
    int insert1hRollup(@Param("start") LocalDateTime start,
                       @Param("end") LocalDateTime end);

    /**
     * 删除指定粒度、指定时间区间的聚合行（重算前先清桶）
     */
    int deleteRange(@Param("granularity") String granularity,
                    @Param("start") LocalDateTime start,
                    @Param("end") LocalDateTime end);

    /**
     * 分批删除指定粒度的过期数据
     */
    int deleteBefore(@Param("granularity") String granularity,
                     @Param("time") LocalDateTime time,
                     @Param("batch") int batch);
}
