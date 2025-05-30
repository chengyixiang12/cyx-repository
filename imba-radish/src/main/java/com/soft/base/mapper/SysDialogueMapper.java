package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysDialogue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.model.vo.GetDialoguesVo;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_dialogue(智能对话表)】的数据库操作Mapper
* @createDate 2025-05-30 11:01:21
* @Entity com.soft.base.entity.SysDialogueHistory
*/
public interface SysDialogueMapper extends BaseMapper<SysDialogue> {

    IPage<GetDialoguesVo> getDialogues(@Param("page") IPage<GetDialoguesVo> page,
                                       @Param("keyword") String keyword,
                                       @Param("userId") Long userId);
}




