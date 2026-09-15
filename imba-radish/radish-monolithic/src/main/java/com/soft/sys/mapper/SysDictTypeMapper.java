package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysDictType;
import com.soft.sys.model.request.GetDictTypesDTO;
import com.soft.sys.model.vo.DictTypeVO;
import com.soft.sys.model.vo.DictTypesVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Mapper
* @createDate 2024-11-04 15:51:07
* @Entity com.soft.base.entity.SysDictType
*/
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    IPage<DictTypesVO> getdictTypes(@Param("page") IPage<DictTypesVO> page,
                                    @Param("request") GetDictTypesDTO request);

    DictTypeVO getDictType(@Param("id") Long id);

    void enableDictType(@Param("id") Long id);

    void forbiddenDictType(@Param("id") Long id);
}




