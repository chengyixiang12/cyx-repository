package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDialogue;
import com.soft.sys.model.request.GetDialoguesRequest;
import com.soft.sys.model.request.RenameRequest;
import com.soft.sys.model.request.SaveDialogueRequest;
import com.soft.sys.model.vo.GetDialoguesVo;
import com.soft.sys.model.vo.GetTitleVo;
import com.soft.sys.model.vo.PageVO;

/**
* @author cyq
* @description 针对表【sys_dialogue_history(智能对话表)】的数据库操作Service
* @createDate 2025-05-30 11:01:21
*/
public interface SysDialogueService extends IService<SysDialogue> {

    PageVO<GetDialoguesVo> getDialogues(GetDialoguesRequest request);

    Long saveDialogue(SaveDialogueRequest request);

    void deleteDialogue(Long id);

    void rename(RenameRequest request);

    GetTitleVo getTitle(Long id);
}
