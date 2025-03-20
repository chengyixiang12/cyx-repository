package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.model.dto.FixRolesDto;
import com.soft.base.model.dto.GetUserRoleDto;
import com.soft.base.entity.SysRole;
import com.soft.base.model.request.DeleteRequest;
import com.soft.base.model.request.GetRolesRequest;
import com.soft.base.model.request.SetMenusRequest;
import com.soft.base.model.request.SetPermissionsRequest;
import com.soft.base.model.vo.SysRoleVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_role】的数据库操作Mapper
* @createDate 2024-10-25 09:40:00
* @Entity com.soft.base.entity.SysRole
*/
public interface SysRoleMapper extends BaseMapper<SysRole> {

    void deleteRoleBatch(@Param("request") DeleteRequest request);

    SysRoleVo getRole(@Param("id") Long id);

    Page<SysRoleVo> getRoles(IPage<SysRoleVo> page,
                             @Param("request") GetRolesRequest request);

    void enableRole(@Param("id") Long id);

    void forbiddenRole(@Param("id") Long id);

    void setDefaultRole(@Param("id") Long id);

    void cancelDefaultRole();

    List<String> getRoleCodeByUserId(@Param("userId") Long id);

    List<FixRolesDto> fixRolesFlag(@Param("ids") List<Long> ids);

    void setMenus(@Param("request") SetMenusRequest request);

    void deleteRoleMenus(@Param("request") SetMenusRequest request);

    void deleteRolePermissions(@Param("request") SetPermissionsRequest request);

    void setPermissions(@Param("request") SetPermissionsRequest request);

    List<GetUserRoleDto> getUserRole(@Param("userId") Long userId);

    List<String> getRoleCodesByUserId(@Param("userId") Long userId);
}




