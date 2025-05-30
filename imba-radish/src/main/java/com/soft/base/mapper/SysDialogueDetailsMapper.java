package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysDialogueDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.model.vo.GetDialoguesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Mapper
* @createDate 2025-05-30 11:01:48
* @Entity com.soft.base.entity.SysDialogueDetails
*/
public interface SysDialogueDetailsMapper extends BaseMapper<SysDialogueDetails> {


    List<String> getRecentContext(@Param("dialogueId") Long dialogueId,
                                  @Param("maxContextNum") Long maxContextNum);
}




