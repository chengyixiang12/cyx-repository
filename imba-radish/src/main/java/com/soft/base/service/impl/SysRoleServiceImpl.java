package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.BaseConstant;
import com.soft.base.model.dto.FixRolesDto;
import com.soft.base.model.dto.GetUserRoleDto;
import com.soft.base.entity.SysRole;
import com.soft.base.mapper.SysRoleMapper;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.GetRolesRequest;
import com.soft.base.model.request.SetMenusRequest;
import com.soft.base.model.request.SetPermissionsRequest;
import com.soft.base.service.SysRoleService;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.SysRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_role】的数据库操作Service实现
* @createDate 2024-10-25 09:40:00
*/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public Boolean existCode(String code) {
        return sysRoleMapper.exists(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getCode, code));
    }

    @Override
    public void deleteRoleBatch(DeleteRequest request) {
        sysRoleMapper.deleteRoleBatch(request);
    }

    @Override
    public Boolean fixRoleFlag(Long id) {
        return sysRoleMapper.exists(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getId, id).eq(SysRole::getFixRole, BaseConstant.FIX_ROLE_FLAG));
    }

    @Override
    public SysRoleVo getRole(Long id) {
        return sysRoleMapper.getRole(id);
    }

    @Override
    public PageVo<SysRoleVo> getRoles(GetRolesRequest request) {
        PageVo<SysRoleVo> pageVo = new PageVo<>();
        IPage<SysRoleVo> page = new Page<>(request.getPageNum(), request.getPageSize());

        page = sysRoleMapper.getRoles(page, request);

        pageVo.setResult(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    public void enableRole(Long id) {
        sysRoleMapper.enableRole(id);
    }

    @Override
    public void forbiddenRole(Long id) {
        sysRoleMapper.forbiddenRole(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultRole(Long id) {
        sysRoleMapper.cancelDefaultRole();
        sysRoleMapper.setDefaultRole(id);
    }

    @Override
    public List<FixRolesDto> fixRolesFlag(List<Long> ids) {
        return sysRoleMapper.fixRolesFlag(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMenus(SetMenusRequest request) {
        sysRoleMapper.deleteRoleMenus(request);
        sysRoleMapper.setMenus(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPermissions(SetPermissionsRequest request) {
        sysRoleMapper.deleteRolePermissions(request);
        sysRoleMapper.setPermissions(request);
    }

    @Override
    public List<GetUserRoleDto> getUserRole(Long userId) {
        return sysRoleMapper.getUserRole(userId);
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return sysRoleMapper.getRoleCodesByUserId(userId);
    }
}




