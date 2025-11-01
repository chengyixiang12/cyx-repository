package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.RedisConstant;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service实现
* @createDate 2024-11-04 15:51:07
*/
@Service
@CacheConfig(cacheNames = "radish:dict")
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
    implements SysDictTypeService{

    private final SysDictTypeMapper sysDictTypeMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    public SysDictTypeServiceImpl(SysDictTypeMapper sysDictTypeMapper, RedisTemplate<String, Object> redisTemplate) {
        this.sysDictTypeMapper = sysDictTypeMapper;
        this.redisTemplate = redisTemplate;
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
        SysDictType sysDictType = sysDictTypeMapper.selectById(id);
        sysDictTypeMapper.deleteById(id);
//        redisTemplate.delete(RedisConstant.DICT_KEY + sysDictType.getDictType());
    }

    @Override
    public void deleteDictTypeBatch(List<String> ids) {
        sysDictTypeMapper.deleteByIds(ids);
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




