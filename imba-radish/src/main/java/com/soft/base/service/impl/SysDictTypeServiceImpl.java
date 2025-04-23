package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.entity.SysDictType;
import com.soft.base.mapper.SysDictTypeMapper;
import com.soft.base.model.request.EditDictTypeRequest;
import com.soft.base.model.request.GetDictTypesRequest;
import com.soft.base.model.request.SaveDictTypeRequest;
import com.soft.base.model.vo.DictTypeVo;
import com.soft.base.model.vo.DictTypesVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysDictTypeService;
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
    public PageVo<DictTypesVo> getdictTypes(GetDictTypesRequest request) {
        IPage<DictTypesVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDictTypeMapper.getdictTypes(page, request);
        PageVo<DictTypesVo> pageVo = new PageVo<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public void saveDictType(SaveDictTypeRequest request) {
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

    @Override
    public boolean existDictType(String dictType) {
        return sysDictTypeMapper.exists(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dictType));
    }

    @Override
    public boolean existDictType(String dictType, Long id) {
        return sysDictTypeMapper.exists(Wrappers.lambdaQuery(SysDictType.class).eq(SysDictType::getDictType, dictType).ne(SysDictType::getId, id));
    }

    @Override
    public void enableDictType(Long id) {
        sysDictTypeMapper.enableDictType(id);
    }

    @Override
    public void forbiddenDictType(Long id) {
        sysDictTypeMapper.forbiddenDictType(id);
    }
}




