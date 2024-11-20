package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysPermission;
import com.soft.base.mapper.SysDictDataMapper;
import com.soft.base.mapper.SysPermissionMapper;
import com.soft.base.request.PermissionsRequest;
import com.soft.base.service.SysPermissionService;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.PermissionsVo;
import org.springframework.stereotype.Service;

/**
* @author cyq
* @description 针对表【sys_permission】的数据库操作Service实现
* @createDate 2024-11-19 09:36:53
*/
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
    implements SysPermissionService{

    private final SysPermissionMapper sysPermissionMapper;


    public SysPermissionServiceImpl(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public PageVo<PermissionsVo> getPermissions(PermissionsRequest request) {
        IPage<PermissionsVo> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysPermissionMapper.getPermissions(page, request);
        PageVo<PermissionsVo> pageVo = new PageVo<>();
        pageVo.setResult(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }
}




