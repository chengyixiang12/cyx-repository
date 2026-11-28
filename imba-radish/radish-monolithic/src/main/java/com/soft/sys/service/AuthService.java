package com.soft.sys.service;

import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.ForgotPasswordDTO;
import com.soft.sys.model.request.LoginDTO;
import com.soft.sys.model.vo.LoginVO;

public interface AuthService {

    void register(SysUser sysUser);

    LoginVO authenticate(LoginDTO request);

    /**
     * 忘记密码：校验邮箱验证码后重置密码，并强制该账号所有设备下线
     */
    void forgotPassword(ForgotPasswordDTO request);
}
