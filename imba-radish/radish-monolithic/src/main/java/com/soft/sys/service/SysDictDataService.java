package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDictData;
import com.soft.sys.model.dto.DictDataDTO;
import com.soft.sys.model.request.DictDatasDTO;
import com.soft.sys.model.request.EditDictDataDTO;
import com.soft.sys.model.request.SaveDictDataDTO;
import com.soft.sys.model.vo.DictDataVO;
import com.soft.sys.model.vo.DictDatasVO;
import com.soft.sys.model.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Service
* @createDate 2024-11-05 17:23:13
*/
public interface SysDictDataService extends IService<SysDictData> {

    PageVO<DictDatasVO> getDictDatas(DictDatasDTO request);

    DictDataVO getDictData(Long id);

    void saveDictData(SaveDictDataDTO request);

    void editDictData(EditDictDataDTO request);

    void deleteDictData(Long id);

    void deleteDictDataBatch(List<Long> ids);

    boolean existValue(Long dictTypeId, String value);

    boolean existCode(Long dictTypeId, String value, Long id);

    void enableDictData(Long id);

    void forbiddenDictData(Long id);

    void setDefaultData(Long id, Long dictTypeId);

    List<DictDataDTO> getByDictType(String dictType);

    Map<String, String> getDictDataMap(String dictType);

    String getDictDataByValue(String value, String dictType);
}
