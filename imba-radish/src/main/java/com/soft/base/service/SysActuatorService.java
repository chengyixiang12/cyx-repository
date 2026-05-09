package com.soft.base.service;

import com.soft.base.entity.SysActuator;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.request.ListActuatorPageRequest;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.model.vo.PageVO;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 程益祥
* @description 针对表【sys_actuator(系统监控表)】的数据库操作Service
* @createDate 2026-04-30 23:04:53
*/
public interface SysActuatorService extends IService<SysActuator> {

    List<ListActuatorVO> listActuator(LocalDateTime startTime, LocalDateTime endTime);

    PageVO<ListActuatorVO> listActuatorPage(ListActuatorPageRequest request);
}
