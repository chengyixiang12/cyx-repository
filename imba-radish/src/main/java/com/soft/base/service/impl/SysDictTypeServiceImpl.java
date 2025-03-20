package com.soft.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysDictType;
import com.soft.base.mapper.SysDictTypeMapper;
import com.soft.base.model.request.EditDictTypeRequest;
import com.soft.base.model.request.SaveDictTypeRequest;
import com.soft.base.service.SysDictTypeService;
import com.soft.base.model.vo.DictTypeVo;
import com.soft.base.model.vo.DictTypesVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service实现
* @createDate 2024-11-04 15:51:07
*/
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
    implements SysDictTypeService{

    private final SysDictTypeMapper sysDictTypeMapper;

    public SysDictTypeServiceImpl(SysDictTypeMapper sysDictTypeMapper) {
        this.sysDictTypeMapper = sysDictTypeMapper;
    }

    @Override
    public List<DictTypesVo> getdictTypes() {
        return sysDictTypeMapper.getdictTypes();
    }

    @Override
    public void saveDictType(SaveDictTypeRequest request) {
        request.setStatus(BaseConstant.DICT_TYPE_STATUS_ENABLE);
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictTypeMapper.insert(sysDictType);
    }

    @Override
    public void editDictType(EditDictTypeRequest request) {
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictTypeMapper.updateById(sysDictType);
    }

    @Override
    public DictTypeVo getDictType(Long id) {
        return sysDictTypeMapper.getDictType(id);
    }

    @Override
    public void deleteDictType(Long id) {
        sysDictTypeMapper.deleteById(id);
    }

    @Override
    public void deleteDictTypeBatch(List<Long> ids) {
        sysDictTypeMapper.deleteByIds(ids);
    }
}




