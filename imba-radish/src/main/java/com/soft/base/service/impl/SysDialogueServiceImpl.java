package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysDialogue;
import com.soft.base.model.request.GetDialoguesRequest;
import com.soft.base.model.request.RenameRequest;
import com.soft.base.model.request.SaveDialogueRequest;
import com.soft.base.model.vo.GetDialoguesVo;
import com.soft.base.model.vo.GetTitleVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysDialogueService;
import com.soft.base.mapper.SysDialogueMapper;
import com.soft.base.utils.BeanUtil;
import com.soft.base.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author cyq
* @description 针对表【sys_dialogue_history(智能对话表)】的数据库操作Service实现
* @createDate 2025-05-30 11:01:21
*/
@Service
public class SysDialogueServiceImpl extends ServiceImpl<SysDialogueMapper, SysDialogue>
    implements SysDialogueService {

    private final SysDialogueMapper sysDialogueMapper;

    private final SecurityUtil securityUtil;

    public SysDialogueServiceImpl(SysDialogueMapper sysDialogueMapper, SecurityUtil securityUtil) {
        this.sysDialogueMapper = sysDialogueMapper;
        this.securityUtil = securityUtil;
    }

    @Override
    public PageVo<GetDialoguesVo> getDialogues(GetDialoguesRequest request) {
        IPage<GetDialoguesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDialogueMapper.getDialogues(page, request.getKeyword(), securityUtil.getUserInfo().getId());
        PageVo<GetDialoguesVo> pageVo = new PageVo<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public Long saveDialogue(SaveDialogueRequest request) {
        SysDialogue sysDialogue = new SysDialogue();
        BeanUtils.copyProperties(request, sysDialogue);
        sysDialogueMapper.insert(sysDialogue);
        return sysDialogue.getId();
    }

    @Override
    public void deleteDialogue(Long id) {
        sysDialogueMapper.deleteById(id);
    }

    @Override
    public void rename(RenameRequest request) {
        SysDialogue sysDialogue = new SysDialogue();
        sysDialogue.setId(request.getId());
        sysDialogue.setTitle(request.getTitle());
        sysDialogueMapper.updateById(sysDialogue);
    }

    @Override
    public GetTitleVo getTitle(Long id) {
        GetTitleVo getTitleVo = new GetTitleVo();
        SysDialogue sysDialogue = sysDialogueMapper.selectById(id);
        getTitleVo.setId(id);
        getTitleVo.setTitle(sysDialogue.getTitle());
        return getTitleVo;
    }

}




