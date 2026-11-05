package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.common.exception.GlobalException;
import com.soft.sys.entity.SysDialogue;
import com.soft.sys.entity.SysDialogueDetails;
import com.soft.sys.mapper.SysDialogueDetailsMapper;
import com.soft.sys.mapper.SysDialogueMapper;
import com.soft.sys.model.dto.GetRecentContentDTO;
import com.soft.sys.model.request.GetDialogueDetailsDTO;
import com.soft.sys.model.vo.DialogueDetailsVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysDialogueDetailsService;
import com.soft.sys.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

    private final SysDialogueMapper sysDialogueMapper;

    private final SecurityUtil securityUtil;

    public SysDialogueDetailsServiceImpl(SysDialogueDetailsMapper sysDialogueDetailsMapper,
                                          SysDialogueMapper sysDialogueMapper,
                                          SecurityUtil securityUtil) {
        this.sysDialogueDetailsMapper = sysDialogueDetailsMapper;
        this.sysDialogueMapper = sysDialogueMapper;
        this.securityUtil = securityUtil;
    }

    @Override
    public List<GetRecentContentDTO> getRecentContext(Long dialogueId, Long maxContextNum) {
        return sysDialogueDetailsMapper.getRecentContext(dialogueId, maxContextNum);
    }

    @Override
    public PageVO<DialogueDetailsVO> getDetails(GetDialogueDetailsDTO request) {
        // 归属校验：只能查看自己的对话，管理员除外
        SysDialogue dialogue = sysDialogueMapper.selectById(request.getDialogueId());
        if (dialogue == null) {
            throw new GlobalException("对话不存在");
        }
        Long userId = securityUtil.getUserInfo().getId();
        if (!securityUtil.isAdmin() && !userId.equals(dialogue.getCreateBy())) {
            throw new GlobalException("无权查看该对话");
        }

        // 倒序分页：第 1 页是最近的消息，页码越大越早，前端据此向上懒加载
        Page<SysDialogueDetails> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<SysDialogueDetails> result = lambdaQuery()
                .eq(SysDialogueDetails::getParentId, request.getDialogueId())
                .orderByDesc(SysDialogueDetails::getCreateTime)
                .orderByDesc(SysDialogueDetails::getId)
                .page(page);

        // 页内再翻转成正序，便于前端按时间顺序拼接展示
        List<DialogueDetailsVO> records = new ArrayList<>(result.getRecords().size());
        for (SysDialogueDetails item : result.getRecords()) {
            DialogueDetailsVO vo = new DialogueDetailsVO();
            vo.setId(String.valueOf(item.getId()));
            vo.setTag(item.getTag());
            vo.setContent(item.getContent());
            vo.setCreateTime(item.getCreateTime());
            records.add(vo);
        }
        Collections.reverse(records);

        return new PageVO<>(records, result.getTotal());
    }
}




