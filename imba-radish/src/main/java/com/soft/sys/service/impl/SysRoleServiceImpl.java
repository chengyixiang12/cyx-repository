package com.soft.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.BaseConstant;
import com.soft.sys.entity.SysRole;
import com.soft.sys.mapper.SysRoleMapper;
import com.soft.sys.model.dto.FixRolesDTO;
import com.soft.sys.model.request.EditRoleDTO;
import com.soft.sys.model.request.GetRolesDTO;
import com.soft.sys.model.request.SetMenusDTO;
import com.soft.sys.model.request.SetPermissionsDTO;
import com.soft.sys.model.vo.GetRoleSelectVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.SysRoleVO;
import com.soft.sys.model.vo.SysRolesVO;
import com.soft.sys.service.SysRoleService;
import org.springframework.beans.BeanUtils;
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

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public Boolean existCode(String code) {
        return sysRoleMapper.exists(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getCode, code));
    }

    @Override
    public Boolean existCode(String code, Long id) {
        return sysRoleMapper.exists(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getCode, code).ne(SysRole::getId, id));
    }

    @Override
    public void deleteRoleBatch(List<Long> ids) {
        sysRoleMapper.deleteByIds(ids);
    }

    @Override
    public Boolean fixRoleFlag(Long id) {
        return sysRoleMapper.exists(Wrappers.lambdaQuery(SysRole.class).eq(SysRole::getId, id).eq(SysRole::getFixRole, BaseConstant.Role.FIX_ROLE_FLAG));
    }

    @Override
    public SysRoleVO getRole(Long id) {
        return sysRoleMapper.getRole(id);
    }

    @Override
    public PageVO<SysRolesVO> getRoles(GetRolesDTO request) {
        PageVO<SysRolesVO> pageVo = new PageVO<>();
        IPage<SysRolesVO> page = new Page<>(request.getPageNum(), request.getPageSize());

        page = sysRoleMapper.getRoles(page, request);

        pageVo.setRecords(page.getRecords());
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
    public List<FixRolesDTO> fixRolesFlag(List<Long> ids) {
        return sysRoleMapper.fixRolesFlag(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMenus(SetMenusDTO request) {
        sysRoleMapper.deleteRoleMenus(request.getRoleId());
        if (CollectionUtil.isEmpty(request.getMenuIds())) {
            return;
        }
        sysRoleMapper.setMenus(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPermissions(SetPermissionsDTO request) {
        sysRoleMapper.deleteRolePermissions(request);
        if (CollectionUtil.isEmpty(request.getPermissionIds())) {
            return;
        }
        sysRoleMapper.setPermissions(request);
    }

    @Override
    public List<String> getUserRole(Long userId) {
        return sysRoleMapper.getUserRole(userId);
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return sysRoleMapper.getRoleCodesByUserId(userId);
    }

    @Override
    public Long getDefaultRole(Integer defaultRoleFlag) {
        return sysRoleMapper.getDefaultRole(defaultRoleFlag);
    }

    @Override
    public void setFixRole(Long id) {
        sysRoleMapper.setFixRole(id);
    }

    @Override
    public void cancelFixRole(Long id) {
        sysRoleMapper.cancelFixRole(id);
    }

    @Override
    public void editRole(EditRoleDTO request) {
        if (BaseConstant.Role.DEFAULT_ROLE_FLAG.equals(request.getIsDefault())) {
            sysRoleMapper.cancelDefaultRole();
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(request, sysRole);
        sysRoleMapper.updateById(sysRole);
    }

    @Override
    public List<GetRoleSelectVO> getRoleSelect() {
        return sysRoleMapper.getRoleSelect();
    }
}




