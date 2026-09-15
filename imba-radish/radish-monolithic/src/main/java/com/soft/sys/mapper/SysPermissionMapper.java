package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.sys.entity.SysPermission;
import com.soft.sys.model.request.PermissionsDTO;
import com.soft.sys.model.vo.GetAllPermissionVO;
import com.soft.sys.model.vo.GetAssignPerVO;
import com.soft.sys.model.vo.PermissionsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_permission】的数据库操作Mapper
* @createDate 2024-11-19 09:36:53
* @Entity com.soft.base.entity.SysPermission
*/
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    IPage<PermissionsVO> getPermissions(IPage<PermissionsVO> page,
                                        @Param("request") PermissionsDTO request);

    List<String> getPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    List<GetAllPermissionVO> getAllPermission();

    List<GetAssignPerVO> getAssignPer(@Param("roleId") Long roleId);

    void enablePermission(@Param("id") Long id);

    void forbiddenPermission(@Param("id") Long id);
}




