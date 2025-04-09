package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysDictData;
import com.soft.base.mapper.SysDictDataMapper;
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
import org.springframework.stereotype.Service;

/**
* @author cyq
* @description 针对表【sys_dict_data】的数据库操作Service实现
* @createDate 2024-11-05 17:23:13
*/
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
    implements SysDictDataService{

    private final SysDictDataMapper sysDictDataMapper;

    @Autowired
    public SysDictDataServiceImpl(SysDictDataMapper sysDictDataMapper) {
        this.sysDictDataMapper = sysDictDataMapper;
    }

    @Override
    public PageVo<DictDatasVo> getDictDatas(DictDatasRequest request) {
        IPage<DictDatasVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysDictDataMapper.getDictDatas(page, request);
        PageVo<DictDatasVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setResult(page.getRecords());
        return pageVo;
    }

    @Override
    public DictDataVo getDictData(Long id) {
        return sysDictDataMapper.getDictData(id);
    }

    @Override
    public void saveDictData(SaveDictDataRequest request) {
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.insert(sysDictData);
    }

    @Override
    public void editDictData(EditDictDataRequest request) {
        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictDataMapper.updateById(sysDictData);
    }

    @Override
    public void deleteDictData(Long id) {
        sysDictDataMapper.deleteById(id);
    }

    @Override
    public void deleteDictDataBatch(DeleteRequest request) {
        sysDictDataMapper.deleteByIds(request.getIds());
    }

    @Override
    public boolean existCode(String code) {
        return sysDictDataMapper.exists(Wrappers.lambdaQuery(SysDictData.class).eq(SysDictData::getCode, code));
    }
}




