package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.model.dto.FixRolesDto;
import com.soft.base.model.dto.GetUserRoleDto;
import com.soft.base.entity.SysRole;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.GetRolesRequest;
import com.soft.base.model.request.SetMenusRequest;
import com.soft.base.model.request.SetPermissionsRequest;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.SysRoleVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_role】的数据库操作Service
* @createDate 2024-10-25 09:40:00
*/
public interface SysRoleService extends IService<SysRole> {

    Boolean existCode(String code);

    void deleteRoleBatch(DeleteRequest request);

    Boolean fixRoleFlag(Long id);

    SysRoleVo getRole(Long id);

    PageVo<SysRoleVo> getRoles(GetRolesRequest request);

    void enableRole(Long id);

    void forbiddenRole(Long id);

    void setDefaultRole(Long id);

    List<FixRolesDto> fixRolesFlag(List<Long> ids);

    void setMenus(SetMenusRequest request);

    void setPermissions(SetPermissionsRequest request);

    List<GetUserRoleDto> getUserRole(Long userId);

    List<String> getRoleCodesByUserId(Long userId);

    Long getDefaultRole(Integer defaultRoleFlag);
}
