package com.soft.base.service;

import com.soft.base.entity.SysDictType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.request.EditDictTypeRequest;
import com.soft.base.request.SaveDictTypeRequest;
import com.soft.base.vo.DictTypeVo;
import com.soft.base.vo.DictTypesVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service
* @createDate 2024-11-04 15:51:07
*/
public interface SysDictTypeService extends IService<SysDictType> {

    List<DictTypesVo> getdictTypes();

    void saveDictType(SaveDictTypeRequest request);

    void editDictType(EditDictTypeRequest request);

    DictTypeVo getDictType(Long id);

    void deleteDictType(Long id);

    void deleteDictTypeBatch(List<Long> ids);
}
