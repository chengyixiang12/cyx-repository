package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.dto.GetUserDto;
import com.soft.base.dto.UserEmailDto;
import com.soft.base.entity.SysUser;
import com.soft.base.vo.AllUserVo;
import com.soft.base.vo.DeptUserVo;
import com.soft.base.vo.GetUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【users】的数据库操作Mapper
* @createDate 2024-09-30 15:49:52
* @Entity com.soft.entity.Users
*/
public interface SysUsersMapper extends BaseMapper<SysUser> {
    Page<AllUserVo> getAllUsers(IPage<AllUserVo> page);

    void editPassword(@Param("username") String username,
                      @Param("password") String password);

    List<DeptUserVo> getAllUser();

    String getManager(@Param("roleCode") String managerRoleCode);

    UserEmailDto getEmailByUsername(@Param("username") String username);

    String getEmail(@Param("username") String username);

    GetUserDto getUser(@Param("id") Long id);
}




