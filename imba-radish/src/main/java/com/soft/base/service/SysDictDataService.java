package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysDictData;
import com.soft.base.model.dto.DictDataDto;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.DictDatasRequest;
import com.soft.base.model.request.EditDictDataRequest;
import com.soft.base.model.request.SaveDictDataRequest;
import com.soft.base.model.vo.DictDataVo;
import com.soft.base.model.vo.DictDatasVo;
import com.soft.base.model.vo.PageVo;

import java.util.List;
import java.util.Map;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Service
* @createDate 2024-11-05 17:23:13
*/
public interface SysDictDataService extends IService<SysDictData> {

    PageVo<DictDatasVo> getDictDatas(DictDatasRequest request);

    DictDataVo getDictData(Long id);

    void saveDictData(SaveDictDataRequest request);

    void editDictData(EditDictDataRequest request);

    void deleteDictData(Long id);

    void deleteDictDataBatch(List<Long> ids);

    boolean existValue(Long parentId, String value);

    boolean existCode(Long parentId, String value, Long id);

    void enableDictData(Long id);

    void forbiddenDictData(Long id);

    void setDefaultData(Long id, Long parentId);

    List<DictDataDto> getByDictType(Long parentId);

    Map<String, String> getDictDataMap(Long parentId);

    String getDictDataByValue(String value, Long parentId);
}
