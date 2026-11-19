package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.sys.entity.SysDialogueDetails;
import com.soft.sys.model.dto.GetRecentContentDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dialogue_details(智能对话详情表)】的数据库操作Mapper
* @createDate 2025-05-30 11:01:48
* @Entity com.soft.base.entity.SysDialogueDetails
*/
public interface SysDialogueDetailsMapper extends BaseMapper<SysDialogueDetails> {


    /**
     * 取最近 limit 条消息，并按时间正序返回
     */
    List<GetRecentContentDTO> getRecentContext(@Param("dialogueId") Long dialogueId,
                                               @Param("messageLimit") Integer messageLimit);

    /**
     * 取摘要覆盖点之后、保留窗口之前、尚未压缩的旧消息
     */
    List<GetRecentContentDTO> getPendingSummaryMessages(@Param("dialogueId") Long dialogueId,
                                                        @Param("afterId") Long afterId,
                                                        @Param("cutoffId") Long cutoffId,
                                                        @Param("limit") Integer limit);
}




