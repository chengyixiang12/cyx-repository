package com.soft.base.service;

import com.soft.base.entity.SysDialogue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.request.GetDialoguesRequest;
import com.soft.base.model.request.RenameRequest;
import com.soft.base.model.request.SaveDialogueRequest;
import com.soft.base.model.vo.GetDialoguesVo;
import com.soft.base.model.vo.GetTitleVo;
import com.soft.base.model.vo.PageVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
* @author cyq
* @description 针对表【sys_dialogue_history(智能对话表)】的数据库操作Service
* @createDate 2025-05-30 11:01:21
*/
public interface SysDialogueService extends IService<SysDialogue> {

    PageVo<GetDialoguesVo> getDialogues(GetDialoguesRequest request);

    Long saveDialogue(SaveDialogueRequest request);

    void deleteDialogue(Long id);

    void rename(RenameRequest request);

    GetTitleVo getTitle(Long id);
}
