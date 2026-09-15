package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDialogue;
import com.soft.sys.model.request.GetDialoguesDTO;
import com.soft.sys.model.request.RenameDTO;
import com.soft.sys.model.request.SaveDialogueDTO;
import com.soft.sys.model.vo.GetDialoguesVO;
import com.soft.sys.model.vo.GetTitleVO;
import com.soft.sys.model.vo.PageVO;

/**
* @author cyq
* @description 针对表【sys_dialogue_history(智能对话表)】的数据库操作Service
* @createDate 2025-05-30 11:01:21
*/
public interface SysDialogueService extends IService<SysDialogue> {

    PageVO<GetDialoguesVO> getDialogues(GetDialoguesDTO request);

    Long saveDialogue(SaveDialogueDTO request);

    void deleteDialogue(Long id);

    void rename(RenameDTO request);

    GetTitleVO getTitle(Long id);
}
