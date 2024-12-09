package com.soft.base.service;

import com.soft.base.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Service
* @createDate 2024-12-09 09:27:10
*/
public interface SysUserRoleService extends IService<SysUserRole> {

    void insertBatch(List<SysUserRole> userRoles);

    List<Long> getByUserId(Long UserId);
}
