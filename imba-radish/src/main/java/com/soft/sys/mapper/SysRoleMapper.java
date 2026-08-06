package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.SysRole;
import com.soft.sys.model.dto.FixRolesDTO;
import com.soft.sys.model.request.GetRolesDTO;
import com.soft.sys.model.request.SetMenusDTO;
import com.soft.sys.model.request.SetPermissionsDTO;
import com.soft.sys.model.vo.GetRoleSelectVO;
import com.soft.sys.model.vo.SysRoleVO;
import com.soft.sys.model.vo.SysRolesVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_role】的数据库操作Mapper
* @createDate 2024-10-25 09:40:00
* @Entity com.soft.base.entity.SysRole
*/
public interface SysRoleMapper extends BaseMapper<SysRole> {

    SysRoleVO getRole(@Param("id") Long id);

    Page<SysRolesVO> getRoles(IPage<SysRolesVO> page,
                              @Param("request") GetRolesDTO request);

    void enableRole(@Param("id") Long id);

    void forbiddenRole(@Param("id") Long id);

    void setDefaultRole(@Param("id") Long id);

    void cancelDefaultRole();

    List<String> getRoleCodeByUserId(@Param("userId") Long id);

    List<FixRolesDTO> fixRolesFlag(@Param("ids") List<Long> ids);

    void setMenus(@Param("request") SetMenusDTO request);

    void deleteRoleMenus(@Param("roleId") Long roleId);

    void deleteRolePermissions(@Param("request") SetPermissionsDTO request);

    void setPermissions(@Param("request") SetPermissionsDTO request);

    List<String> getUserRole(@Param("userId") Long userId);

    List<String> getRoleCodesByUserId(@Param("userId") Long userId);

    Long getDefaultRole(@Param("defaultRoleFlag") Integer defaultRoleFlag);

    void setFixRole(@Param("id") Long id);

    void cancelFixRole(@Param("id") Long id);

    List<GetRoleSelectVO> getRoleSelect();
}




