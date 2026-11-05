package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDialogueDetails;
import com.soft.sys.model.dto.GetRecentContentDTO;
import com.soft.sys.model.request.GetDialogueDetailsDTO;
import com.soft.sys.model.vo.DialogueDetailsVO;
import com.soft.sys.model.vo.PageVO;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Service
* @createDate 2025-05-30 11:01:48
*/
public interface SysDialogueDetailsService extends IService<SysDialogueDetails> {

    List<GetRecentContentDTO> getRecentContext(Long dialogueId, Long maxContextNum);

    /**
     * 分页获取对话明细（倒序分页：第一页为最新消息，供前端向上懒加载更早记录）
     */
    PageVO<DialogueDetailsVO> getDetails(GetDialogueDetailsDTO request);
}
