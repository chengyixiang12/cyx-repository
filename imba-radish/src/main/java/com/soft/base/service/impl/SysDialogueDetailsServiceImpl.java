package com.soft.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysDialogueDetails;
import com.soft.base.service.SysDialogueDetailsService;
import com.soft.base.mapper.SysDialogueDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Service实现
* @createDate 2025-05-30 11:01:48
*/
@Service
public class SysDialogueDetailsServiceImpl extends ServiceImpl<SysDialogueDetailsMapper, SysDialogueDetails>
    implements SysDialogueDetailsService{

    private final SysDialogueDetailsMapper sysDialogueDetailsMapper;

    @Autowired
    public SysDialogueDetailsServiceImpl(SysDialogueDetailsMapper sysDialogueDetailsMapper) {
        this.sysDialogueDetailsMapper = sysDialogueDetailsMapper;
    }

    @Override
    public List<String> getRecentContext(Long dialogueId, Long maxContextNum) {
        return sysDialogueDetailsMapper.getRecentContext(dialogueId, maxContextNum);
    }
}




