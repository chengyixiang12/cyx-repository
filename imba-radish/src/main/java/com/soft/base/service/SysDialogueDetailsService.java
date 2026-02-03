package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysDialogueDetails;
import com.soft.base.model.dto.GetRecentContentDto;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Service
* @createDate 2025-05-30 11:01:48
*/
public interface SysDialogueDetailsService extends IService<SysDialogueDetails> {

    List<GetRecentContentDto> getRecentContext(Long dialogueId, Long maxContextNum);
}
