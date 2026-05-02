package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysActuator;
import com.soft.base.model.vo.ListActuatorVO;
import com.soft.base.service.SysActuatorService;
import com.soft.base.mapper.SysActuatorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 程益祥
* @description 针对表【sys_actuator(系统监控表)】的数据库操作Service实现
* @createDate 2026-04-30 23:04:53
*/
@Service
@RequiredArgsConstructor
public class SysActuatorServiceImpl extends ServiceImpl<SysActuatorMapper, SysActuator>
    implements SysActuatorService{

    private final SysActuatorMapper sysActuatorMapper;

    @Override
    public List<ListActuatorVO> listActuator(LocalDateTime startTime, LocalDateTime endTime) {
        return sysActuatorMapper.listActuator(startTime, endTime);
    }
}




