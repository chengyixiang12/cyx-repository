package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.entity.SysDictData;
import com.soft.sys.mapper.SysDictDataMapper;
import com.soft.sys.model.dto.DictDataDTO;
import com.soft.sys.model.request.DictDatasDTO;
import com.soft.sys.model.request.EditDictDataDTO;
import com.soft.sys.model.request.SaveDictDataDTO;
import com.soft.sys.model.vo.DictDataVO;
import com.soft.sys.model.vo.DictDatasVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.service.SysDictDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
    public PageVO<DictDatasVO> getDictDatas(DictDatasDTO request) {
        IPage<DictDatasVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDictDataMapper.getDictDatas(page, request);
        PageVO<DictDatasVO> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords());
        return pageVo;
    }

    @Override
    public DictDataVO getDictData(Long id) {
        return sysDictDataMapper.getDictData(id);
    }

    @Override
    public void saveDictData(SaveDictDataDTO request) {
        if (BaseConstant.Status.STATUS_ENABLE.equals(request.getIsDefault())) {
            sysDictDataMapper.setNotDefault(request.getParentId());
        }
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.insert(sysDictData);
    }

    @Override
    @CacheEvict(key = "#request.parentId")
    public void editDictData(EditDictDataDTO request) {
        if (BaseConstant.Status.STATUS_ENABLE.equals(request.getIsDefault())) {
            sysDictDataMapper.setNotDefault(request.getParentId());
        }
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.updateById(sysDictData);
    }

    @Override
    public void deleteDictData(Long id) {
        sysDictDataMapper.deleteById(id);
        removeCache(id);
    }

    @Override
    public void deleteDictDataBatch(List<Long> ids) {
        sysDictDataMapper.deleteByIds(ids);
        ids.forEach(this::removeCache);
    }

    @Override
    public boolean existValue(Long parentId, String value) {
        return sysDictDataMapper.exists(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getParentId, parentId).eq(SysDictData::getValue, value));
    }

    @Override
    public boolean existCode(Long parentId, String value, Long id) {
        return sysDictDataMapper.exists(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getParentId, parentId).eq(SysDictData::getValue, value).ne(SysDictData::getId, id));
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
    public void setDefaultData(Long id, Long parentId) {
        sysDictDataMapper.setNotDefault(parentId);
        sysDictDataMapper.setDefaultData(id);
    }

    @Override
    public List<DictDataDTO> getByDictType(String dictType) {
        return sysDictDataMapper.getByDictType(dictType);
    }

    @Override
    @Cacheable(key = "#dictType", unless = "#result.size() == 0")
    public Map<String, String> getDictDataMap(String dictType) {
        List<DictDataDTO> sysDictDataList = sysDictDataMapper.getByDictType(dictType);
        return sysDictDataList.stream().collect(Collectors.toMap(DictDataDTO::getValue, DictDataDTO::getLabel, (a, b) -> a));
    }

    @Override
    public String getDictDataByValue(String value, String dictType) {
        return sysDictDataMapper.getDictDataByValue(value, dictType);
    }

    /**
     * 移除缓存
     * @param id
     */
    private void removeCache(Long id) {
        SysDictData sysDictData = sysDictDataMapper.selectById(id);
        redisTemplate.delete(RedisConstant.DICT_KEY + sysDictData.getParentId());
    }
}




