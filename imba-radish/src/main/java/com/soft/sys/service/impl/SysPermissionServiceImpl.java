package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.entity.SysPermission;
import com.soft.sys.mapper.SysPermissionMapper;
import com.soft.sys.model.request.EditPermissionDTO;
import com.soft.sys.model.request.PermissionsDTO;
import com.soft.sys.model.request.SavePermissionDTO;
import com.soft.sys.model.vo.*;
import com.soft.sys.service.SysPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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
    public PageVO<PermissionsVO> getPermissions(PermissionsDTO request) {
        IPage<PermissionsVO> page = new Page<>(request.getPageNum(), request.getPageSize());
        page = sysPermissionMapper.getPermissions(page, request);
        PageVO<PermissionsVO> pageVo = new PageVO<>();
        pageVo.setRecords(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public void savePermission(SavePermissionDTO request) {
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(request, sysPermission);
        sysPermissionMapper.insert(sysPermission);
    }

    @Override
    public List<String> getPermissionsByRoleCodes(List<String> roleCodes) {
        return sysPermissionMapper.getPermissionsByRoleCodes(roleCodes);
    }

    @Override
    public boolean existCode(String code) {
        return sysPermissionMapper.exists(Wrappers.lambdaQuery(SysPermission.class).eq(SysPermission::getCode, code));
    }

    @Override
    public List<GetAllPermissionVO> getAllPermission() {
        return sysPermissionMapper.getAllPermission();
    }

    @Override
    public List<GetAssignPerVO> getAssignPer(Long roleId) {
        return sysPermissionMapper.getAssignPer(roleId);
    }

    @Override
    public void editPermission(EditPermissionDTO request) {
        SysPermission sysPermission = new SysPermission();
        BeanUtils.copyProperties(request, sysPermission);
        sysPermissionMapper.updateById(sysPermission);
    }

    @Override
    public void deletePermission(Long id) {
        sysPermissionMapper.deleteById(id);
    }

    @Override
    public void enablePermission(Long id) {
        sysPermissionMapper.enablePermission(id);
    }

    @Override
    public void forbiddenPermission(Long id) {
        sysPermissionMapper.forbiddenPermission(id);
    }

    @Override
    public boolean existEnableCode(String[] permissions) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, BaseConstant.Status.STATUS_ENABLE);
        wrapper.in(SysPermission::getCode, Arrays.asList(permissions));
        return sysPermissionMapper.exists(wrapper);
    }

    @Override
    public GetPermissionVO getPermission(Long id) {
        GetPermissionVO getPermissionVo = new GetPermissionVO();
        SysPermission sysPermission = sysPermissionMapper.selectById(id);
        BeanUtils.copyProperties(sysPermission, getPermissionVo);
        return getPermissionVo;
    }
}




