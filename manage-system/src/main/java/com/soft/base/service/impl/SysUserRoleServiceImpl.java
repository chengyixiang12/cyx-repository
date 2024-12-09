package com.soft.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.entity.SysUserRole;
import com.soft.base.service.SysUserRoleService;
import com.soft.base.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Service实现
* @createDate 2024-12-09 09:27:10
*/
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
    implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    public SysUserRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public void insertBatch(List<SysUserRole> userRoles) {
        sysUserRoleMapper.insertBatch(userRoles);
    }

    @Override
    public List<Long> getByUserId(Long UserId) {
        return sysUserRoleMapper.getByUserId(UserId);
    }
}




