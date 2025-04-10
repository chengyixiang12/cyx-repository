package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.base.entity.SysUser;
import com.soft.base.model.dto.GetUserDto;
import com.soft.base.model.dto.UserEmailDto;
import com.soft.base.model.request.GetAllUsersRequest;
import com.soft.base.model.vo.AllUserVo;
import com.soft.base.model.vo.DeptUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【users】的数据库操作Mapper
* @createDate 2024-09-30 15:49:52
* @Entity com.soft.entity.Users
*/
public interface SysUsersMapper extends BaseMapper<SysUser> {
    Page<AllUserVo> getAllUsers(@Param("page") IPage<AllUserVo> page,
                                @Param("request") GetAllUsersRequest request);

    List<DeptUserVo> getAllUser();

    Long getManager(@Param("roleCode") String managerRoleCode);

    UserEmailDto getEmailByUsername(@Param("username") String username);

    String getEmail(@Param("username") String username);

    GetUserDto getUser(@Param("id") Long id);

    void lockUser(@Param("username") String username);

    void unlockUser(@Param("username") String username);

    String getUsernameById(@Param("id") Long id);

    Long existsEmail(@Param("id") Long id,
                        @Param("email") String email);

    String getUsername(@Param("id") Long id);
}




