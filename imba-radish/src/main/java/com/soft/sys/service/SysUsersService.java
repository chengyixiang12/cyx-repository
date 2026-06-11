package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.EditUserRequest;
import com.soft.sys.model.request.GetUsersRequest;
import com.soft.sys.model.request.ResetUsernameRequest;
import com.soft.sys.model.request.SaveUserRequest;
import com.soft.sys.model.vo.GetUserVo;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.UsersVo;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service
* @createDate 2024-09-30 15:49:52
*/
public interface SysUsersService extends IService<SysUser> {
    PageVO<UsersVo> getUsers(GetUsersRequest request);

    void editPassword(String targetPass, Long id);

    void resetPassword(Long id);

    void saveUser(SaveUserRequest request);

    void editUser(EditUserRequest request, String username);

    GetUserVo getUser(Long id);

    void lockUser(String username);

    void unlockUser(String username);

    void resetUsername(ResetUsernameRequest request, String username);

    boolean existsUsername(String newUsername);

    boolean existsEmail(String email);

    boolean existsEmail(Long id, String email);

    void deleteUser(Long id, String username);

    String getUsername(Long id);

    void enableUser(String username);

    void forbiddenUser(String username);

    SysUser getUserByEmail(String email);

    Long getPrimaryKeyByUsername(String username);
}
