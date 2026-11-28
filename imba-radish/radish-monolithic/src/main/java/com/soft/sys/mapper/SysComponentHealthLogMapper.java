package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysComponentHealthLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cyx
 * @description 针对表【sys_component_health_log(组件状态变更记录)】的数据库操作Mapper
 * @date 2026-11-05
 */
public interface SysComponentHealthLogMapper extends BaseMapper<SysComponentHealthLog> {

    List<SysComponentHealthLog> selectRecent(@Param("limit") int limit);
}
