package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysDictData;
import com.soft.sys.model.dto.DictDataDTO;
import com.soft.sys.model.request.DeleteDTO;
import com.soft.sys.model.request.DictDatasDTO;
import com.soft.sys.model.vo.DictDataVO;
import com.soft.sys.model.vo.DictDatasVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Mapper
* @createDate 2024-11-05 17:23:13
* @Entity com.soft.base.entity.SysDictData
*/
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    IPage<DictDatasVO> getDictDatas(IPage<DictDatasVO> page, @Param("request") DictDatasDTO request);

    DictDataVO getDictData(@Param("id") Long id);

    void deleteDictDataBatch(@Param("request") DeleteDTO request);

    void enableDictData(@Param("id") Long id);

    void forbiddenDictData(@Param("id") Long id);

    void setNotDefault(@Param("parentId") Long parentId);

    void setDefaultData(@Param("id") Long id);

    List<DictDataDTO> getByDictType(@Param("dictType") String dictType);

    String getDictDataByValue(@Param("value") String value,
                              @Param("dictType") String dictType);
}




