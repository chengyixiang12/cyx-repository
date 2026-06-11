package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDictType;
import com.soft.sys.model.request.EditDictTypeRequest;
import com.soft.sys.model.request.GetDictTypesRequest;
import com.soft.sys.model.request.SaveDictTypeRequest;
import com.soft.sys.model.vo.DictTypeVo;
import com.soft.sys.model.vo.DictTypesVo;
import com.soft.sys.model.vo.PageVO;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service
* @createDate 2024-11-04 15:51:07
*/
public interface SysDictTypeService extends IService<SysDictType> {

    PageVO<DictTypesVo> getdictTypes(GetDictTypesRequest request);

    void saveDictType(SaveDictTypeRequest request);

    void editDictType(EditDictTypeRequest request);

    DictTypeVo getDictType(Long id);

    void deleteDictType(Long id);

    void deleteDictTypeBatch(List<Long> ids);

    void enableDictType(Long id);

    void forbiddenDictType(Long id);
}
