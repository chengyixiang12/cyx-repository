package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysPermission;
import com.soft.base.request.PermissionsRequest;
import com.soft.base.request.SavePermissionRequest;
import com.soft.base.vo.PageVo;
import com.soft.base.vo.PermissionsVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_permission】的数据库操作Service
* @createDate 2024-11-19 09:36:53
*/
public interface SysPermissionService extends IService<SysPermission> {

    PageVo<PermissionsVo> getPermissions(PermissionsRequest request);

    List<String> getPermissionsByUserId(Long id);

    void savePermission(SavePermissionRequest request);

    List<String> getPermissionsByRoleCodes(List<String> roleCodes);
}
