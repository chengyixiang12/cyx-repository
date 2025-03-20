package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.soft.base.entity.SysPermission;
import com.soft.base.model.request.PermissionsRequest;
import com.soft.base.model.vo.PermissionsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_permission】的数据库操作Mapper
* @createDate 2024-11-19 09:36:53
* @Entity com.soft.base.entity.SysPermission
*/
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    IPage<PermissionsVo> getPermissions(IPage<PermissionsVo> page,
                                        @Param("request") PermissionsRequest request);

    List<String> getPermissionsByUserId(@Param("id") Long id);

    List<String> getPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}




