package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysDictData;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.DictDatasRequest;
import com.soft.base.model.request.EditDictDataRequest;
import com.soft.base.model.request.SaveDictDataRequest;
import com.soft.base.model.vo.DictDataVo;
import com.soft.base.model.vo.DictDatasVo;
import com.soft.base.model.vo.PageVo;

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

    void deleteDictDataBatch(DeleteRequest request);

    boolean existCode(String code);

    boolean existCode(String code, Long id);

    void enableDictData(Long id);

    void forbiddenDictData(Long id);

    void setDefaultData(Long id, String dictType);
}
