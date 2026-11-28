package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysComponentStatus;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cyx
 * @description 针对表【sys_component_status(组件当前状态)】的数据库操作Mapper
 * @date 2026-11-05
 */
public interface SysComponentStatusMapper extends BaseMapper<SysComponentStatus> {

    List<SysComponentStatus> selectAllOrdered();

    /**
     * 状态原地更新；状态变化时才刷新 since_time
     */
    int upsert(@Param("component") String component,
               @Param("componentName") String componentName,
               @Param("status") String status,
               @Param("detail") String detail,
               @Param("now") LocalDateTime now);
}
