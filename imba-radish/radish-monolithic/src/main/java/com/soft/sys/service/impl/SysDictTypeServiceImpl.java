package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.entity.SysDictData;
import com.soft.sys.entity.SysDictType;
import com.soft.sys.mapper.SysDictDataMapper;
import com.soft.sys.mapper.SysDictTypeMapper;
import com.soft.sys.model.request.EditDictTypeDTO;
import com.soft.sys.model.request.GetDictTypesDTO;
import com.soft.sys.model.request.SaveDictTypeDTO;
import com.soft.sys.model.vo.DictTypeVO;
import com.soft.sys.model.vo.DictTypesVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dict_type】的数据库操作Service实现
* @createDate 2024-11-04 15:51:07
*/
@Service
@CacheConfig(cacheNames = "radish:dict")
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
    implements SysDictTypeService{

    private final SysDictTypeMapper sysDictTypeMapper;

    private final SysDictDataMapper sysDictDataMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageVO<DictTypesVO> getdictTypes(GetDictTypesDTO request) {
        IPage<DictTypesVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDictTypeMapper.getdictTypes(page, request);
        PageVO<DictTypesVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public void saveDictType(SaveDictTypeDTO request) {
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictTypeMapper.insert(sysDictType);
    }

    @Override
    public void editDictType(EditDictTypeDTO request) {
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictTypeMapper.updateById(sysDictType);
    }

    @Override
    public DictTypeVO getDictType(Long id) {
        return sysDictTypeMapper.getDictType(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        sysDictTypeMapper.deleteById(id);

        // 级联删除该类型下的全部字典数据
        sysDictDataMapper.delete(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictTypeId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        sysDictTypeMapper.deleteByIds(ids);

        // 级联删除这些类型下的全部字典数据
        sysDictDataMapper.delete(Wrappers.lambdaQuery(SysDictData.class).in(SysDictData::getDictTypeId, ids));
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




