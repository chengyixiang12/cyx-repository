package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysRole;
import com.soft.sys.model.dto.FixRolesDto;
import com.soft.sys.model.request.EditRoleRequest;
import com.soft.sys.model.request.GetRolesRequest;
import com.soft.sys.model.request.SetMenusRequest;
import com.soft.sys.model.request.SetPermissionsRequest;
import com.soft.sys.model.vo.GetRoleSelectVo;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.SysRoleVo;
import com.soft.sys.model.vo.SysRolesVo;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_role】的数据库操作Service
* @createDate 2024-10-25 09:40:00
*/
public interface SysRoleService extends IService<SysRole> {

    Boolean existCode(String code);

    Boolean existCode(String code, Long id);

    void deleteRoleBatch(List<Long> ids);

    Boolean fixRoleFlag(Long id);

    SysRoleVo getRole(Long id);

    PageVO<SysRolesVo> getRoles(GetRolesRequest request);

    void enableRole(Long id);

    void forbiddenRole(Long id);

    void setDefaultRole(Long id);

    List<FixRolesDto> fixRolesFlag(List<Long> ids);

    void setMenus(SetMenusRequest request);

    void setPermissions(SetPermissionsRequest request);

    List<String> getUserRole(Long userId);

    List<String> getRoleCodesByUserId(Long userId);

    Long getDefaultRole(Integer defaultRoleFlag);

    void setFixRole(Long id);

    void cancelFixRole(Long id);

    void editRole(EditRoleRequest request);

    List<GetRoleSelectVo> getRoleSelect();
}
