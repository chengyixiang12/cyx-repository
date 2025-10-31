package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.entity.SysDictData;
import com.soft.base.mapper.SysDictDataMapper;
import com.soft.base.model.dto.DictDataDto;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.DictDatasRequest;
import com.soft.base.model.request.EditDictDataRequest;
import com.soft.base.model.request.SaveDictDataRequest;
import com.soft.base.model.vo.DictDataVo;
import com.soft.base.model.vo.DictDatasVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.service.SysDictDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Service实现
* @createDate 2024-11-05 17:23:13
*/
@Service
@CacheConfig(cacheNames="radish:dict")
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
    implements SysDictDataService{

    private final SysDictDataMapper sysDictDataMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public SysDictDataServiceImpl(SysDictDataMapper sysDictDataMapper,
                                  RedisTemplate<String, Object> redisTemplate) {
        this.sysDictDataMapper = sysDictDataMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageVo<DictDatasVo> getDictDatas(DictDatasRequest request) {
        IPage<DictDatasVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDictDataMapper.getDictDatas(page, request);
        PageVo<DictDatasVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public DictDataVo getDictData(Long id) {
        return sysDictDataMapper.getDictData(id);
    }

    @Override
    public void saveDictData(SaveDictDataRequest request) {
        if (BaseConstant.Status.STATUS_ENABLE.equals(request.getIsDefault())) {
            sysDictDataMapper.setNotDefault(request.getDictType());
        }
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.insert(sysDictData);
    }

    @Override
    public void editDictData(EditDictDataRequest request) {
        if (BaseConstant.Status.STATUS_ENABLE.equals(request.getIsDefault())) {
            sysDictDataMapper.setNotDefault(request.getDictType());
        }
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.updateById(sysDictData);
        removeCache(request.getId());
    }

    @Override
    public void deleteDictData(Long id) {
        sysDictDataMapper.deleteById(id);
        removeCache(id);
    }

    @Override
    public void deleteDictDataBatch(DeleteRequest request) {
        sysDictDataMapper.deleteByIds(request.getIds());
        request.getIds().forEach(this::removeCache);
    }

    @Override
    public boolean existValue(String dictType, String value) {
        return sysDictDataMapper.exists(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType).eq(SysDictData::getValue, value));
    }

    @Override
    public boolean existCode(String dictType, String value, Long id) {
        return sysDictDataMapper.exists(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getDictType, dictType).eq(SysDictData::getValue, value).ne(SysDictData::getId, id));
    }

    @Override
    public void enableDictData(Long id) {
        sysDictDataMapper.enableDictData(id);
        removeCache(id);
    }

    @Override
    public void forbiddenDictData(Long id) {
        sysDictDataMapper.forbiddenDictData(id);
        removeCache(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultData(Long id, String dictType) {
        sysDictDataMapper.setNotDefault(dictType);
        sysDictDataMapper.setDefaultData(id);
    }

    @Override
    public List<DictDataDto> getByDictType(String dictType) {
        return sysDictDataMapper.getByDictType(dictType, BaseConstant.Status.STATUS_ENABLE);
    }

    @Override
    @Cacheable(key = "#dictType", unless = "#result.size() == 0")
    public Map<String, String> getByDictTypeMap(String dictType) {
        List<DictDataDto> sysDictDataList = sysDictDataMapper.getByDictType(dictType, BaseConstant.Status.STATUS_ENABLE);
        return sysDictDataList.stream().collect(Collectors.toMap(DictDataDto::getValue, DictDataDto::getLabel, (a, b) -> a));
    }

    /**
     * 移除缓存
     * @param id
     */
    private void removeCache(Long id) {
        SysDictData sysDictData = sysDictDataMapper.selectById(id);
        redisTemplate.delete(RedisConstant.DICT_KEY + sysDictData.getDictType());
    }
}




