package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysDictType;
import com.soft.base.model.request.EditDictTypeRequest;
import com.soft.base.model.request.GetDictTypesRequest;
import com.soft.base.model.request.SaveDictTypeRequest;
import com.soft.base.model.vo.DictTypeVo;
import com.soft.base.model.vo.DictTypesVo;
import com.soft.base.model.vo.PageVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service
* @createDate 2024-11-04 15:51:07
*/
public interface SysDictTypeService extends IService<SysDictType> {

    PageVo<DictTypesVo> getdictTypes(GetDictTypesRequest request);

    void saveDictType(SaveDictTypeRequest request);

    void editDictType(EditDictTypeRequest request);

    DictTypeVo getDictType(Long id);

    void deleteDictType(Long id);

    void deleteDictTypeBatch(List<Long> ids);

    boolean existDictType(String dictType);

    boolean existDictType(String dictType, Long id);

    void enableDictType(Long id);

    void forbiddenDictType(Long id);
}
