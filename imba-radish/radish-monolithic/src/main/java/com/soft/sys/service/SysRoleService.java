package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysRole;
import com.soft.sys.model.dto.FixRolesDTO;
import com.soft.sys.model.request.EditRoleDTO;
import com.soft.sys.model.request.GetRolesDTO;
import com.soft.sys.model.request.SetMenusDTO;
import com.soft.sys.model.request.SetPermissionsDTO;
import com.soft.sys.model.vo.GetRoleSelectVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.SysRoleVO;
import com.soft.sys.model.vo.SysRolesVO;

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

    SysRoleVO getRole(Long id);

    PageVO<SysRolesVO> getRoles(GetRolesDTO request);

    void enableRole(Long id);

    void forbiddenRole(Long id);

    void setDefaultRole(Long id);

    List<FixRolesDTO> fixRolesFlag(List<Long> ids);

    void setMenus(SetMenusDTO request);

    void setPermissions(SetPermissionsDTO request);

    List<String> getUserRole(Long userId);

    List<String> getRoleCodesByUserId(Long userId);

    Long getDefaultRole(Integer defaultRoleFlag);

    void setFixRole(Long id);

    void cancelFixRole(Long id);

    void editRole(EditRoleDTO request);

    List<GetRoleSelectVO> getRoleSelect();
}
