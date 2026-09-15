package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysDictType;
import com.soft.sys.model.request.EditDictTypeDTO;
import com.soft.sys.model.request.GetDictTypesDTO;
import com.soft.sys.model.request.SaveDictTypeDTO;
import com.soft.sys.model.vo.DictTypeVO;
import com.soft.sys.model.vo.DictTypesVO;
import com.soft.sys.model.vo.PageVO;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service
* @createDate 2024-11-04 15:51:07
*/
public interface SysDictTypeService extends IService<SysDictType> {

    PageVO<DictTypesVO> getdictTypes(GetDictTypesDTO request);

    void saveDictType(SaveDictTypeDTO request);

    void editDictType(EditDictTypeDTO request);

    DictTypeVO getDictType(Long id);

    void deleteDictType(Long id);

    void deleteDictTypeBatch(List<Long> ids);

    void enableDictType(Long id);

    void forbiddenDictType(Long id);
}
