package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysUser;
import com.soft.base.request.*;
import com.soft.base.vo.AllUserVo;
import com.soft.base.vo.GetUserVo;
import com.soft.base.vo.PageVo;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service
* @createDate 2024-09-30 15:49:52
*/
public interface SysUsersService extends IService<SysUser> {
    PageVo<AllUserVo> getAllUsers(PageRequest request);

    void editPassword(String targetPass, Long id);

    void resetPassword(ResetPasswordRequest request);

    String getEmail(String username);

    boolean checkUsernameExist(String username);

    void saveUser(SaveUserRequest request) throws Exception;

    void editUser(EditUserRequest request);

    GetUserVo getUser(Long id);

    Long getManager(String managerRoleCode);

    void lockUser(String username);

    void unlockUser(String username);

    void resetUsername(ResetUsernameRequest request);
}
