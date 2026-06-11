package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysDictType;
import com.soft.sys.model.request.GetDictTypesRequest;
import com.soft.sys.model.vo.DictTypeVo;
import com.soft.sys.model.vo.DictTypesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Mapper
* @createDate 2024-11-04 15:51:07
* @Entity com.soft.base.entity.SysDictType
*/
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    IPage<DictTypesVo> getdictTypes(@Param("page") IPage<DictTypesVo> page,
                                    @Param("request") GetDictTypesRequest request);

    DictTypeVo getDictType(@Param("id") Long id);

    void enableDictType(@Param("id") Long id);

    void forbiddenDictType(@Param("id") Long id);
}




