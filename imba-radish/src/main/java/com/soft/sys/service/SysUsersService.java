package com.soft.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.EditUserDTO;
import com.soft.sys.model.request.GetUsersDTO;
import com.soft.sys.model.request.ResetUsernameDTO;
import com.soft.sys.model.request.SaveUserDTO;
import com.soft.sys.model.vo.GetUserVO;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.UsersVO;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service
* @createDate 2024-09-30 15:49:52
*/
public interface SysUsersService extends IService<SysUser> {
    PageVO<UsersVO> getUsers(GetUsersDTO request);

    void editPassword(String targetPass, Long id);

    void resetPassword(Long id);

    void saveUser(SaveUserDTO request);

    void editUser(EditUserDTO request, String username);

    GetUserVO getUser(Long id);

    void lockUser(String username);

    void unlockUser(String username);

    void resetUsername(ResetUsernameDTO request, String username);

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
