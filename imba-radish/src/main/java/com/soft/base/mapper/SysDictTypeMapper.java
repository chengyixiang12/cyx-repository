package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.entity.SysDictType;
import com.soft.base.vo.DictTypeVo;
import com.soft.base.vo.DictTypesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Mapper
* @createDate 2024-11-04 15:51:07
* @Entity com.soft.base.entity.SysDictType
*/
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    List<DictTypesVo> getdictTypes();

    DictTypeVo getDictType(@Param("id") Long id);

    void deleteDictTypeBatch(@Param("ids") List<Long> ids);
}




