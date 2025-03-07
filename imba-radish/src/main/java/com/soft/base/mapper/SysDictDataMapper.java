package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysDictData;
import com.soft.base.request.DeleteRequest;
import com.soft.base.request.DictDatasRequest;
import com.soft.base.vo.DictDataVo;
import com.soft.base.vo.DictDatasVo;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Mapper
* @createDate 2024-11-05 17:23:13
* @Entity com.soft.base.entity.SysDictData
*/
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    IPage<DictDatasVo> getDictDatas(IPage<DictDatasVo> page, @Param("request") DictDatasRequest request);

    DictDataVo getDictData(@Param("id") Long id);

    void deleteDictDataBatch(@Param("request") DeleteRequest request);
}




