package com.soft.base.service;

import com.soft.base.entity.SysDialogueDetails;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Service
* @createDate 2025-05-30 11:01:48
*/
public interface SysDialogueDetailsService extends IService<SysDialogueDetails> {

    List<String> getRecentContext(Long dialogueId, Long maxContextNum);
}
