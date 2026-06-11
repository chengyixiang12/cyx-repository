package com.soft.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soft.sys.entity.SysUser;
import com.soft.sys.model.dto.GetUsersDto;
import com.soft.sys.model.vo.GetUserVo;
import com.soft.sys.model.vo.UsersVo;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【users】的数据库操作Mapper
* @createDate 2024-09-30 15:49:52
* @Entity com.soft.entity.Users
*/
public interface SysUsersMapper extends BaseMapper<SysUser> {
    Page<UsersVo> getUsers(@Param("page") IPage<UsersVo> page,
                           @Param("getUsersDto") GetUsersDto getUsersDto);

    GetUserVo getUser(@Param("id") Long id);

    void lockUser(@Param("username") String username);

    void unlockUser(@Param("username") String username);

    Boolean existsEmail(@Param("id") Long id,
                        @Param("email") String email);

    String getUsername(@Param("id") Long id);

    SysUser loadUser(@Param("param") String param);

    void enableUser(@Param("username") String username);

    void forbiddenUser(@Param("username") String username);

    SysUser getUserByEmail(@Param("email") String email);

    Long getPrimaryKeyByUsername(@Param("username") String username);
}




