package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysUser;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.GetUserVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.UsersVo;

import java.io.IOException;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service
* @createDate 2024-09-30 15:49:52
*/
public interface SysUsersService extends IService<SysUser> {
    PageVo<UsersVo> getUsers(GetUsersRequest request);

    void editPassword(String targetPass, Long id);

    void resetPassword(ResetPasswordRequest request);

    String getEmail(String username);

    boolean checkUsernameExist(String username);

    void saveUser(SaveUserRequest request) throws Exception;

    void editUser(EditUserRequest request, String username);

    GetUserVo getUser(Long id);

    Long getManager(String managerRoleCode);

    void lockUser(String username);

    void unlockUser(String username);

    void resetUsername(ResetUsernameRequest request, String username) throws IOException;

    boolean existsUsername(String newUsername);

    boolean existsEmail(String email);

    boolean existsEmail(Long id, String email);

    void deleteUser(Long id, String username) throws IOException;

    String getUsername(Long id);

    void enableUser(String username);

    void forbiddenUser(String username);

    SysUser getUserByEmail(String email);

    String getAvatar(Long id);
}
