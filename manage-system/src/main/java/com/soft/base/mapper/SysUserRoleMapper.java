package com.soft.base.mapper;

import com.soft.base.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Mapper
* @createDate 2024-12-09 09:27:10
* @Entity com.soft.base.entity.UserRole
*/
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    void insertBatch(@Param("userRoles") List<SysUserRole> userRoles);

    List<Long> getByUserId(@Param("userId") Long userId);
}




