package com.soft.base.service;

import com.soft.base.entity.SysUser;
import com.soft.base.model.request.LoginRequest;
import com.soft.base.model.vo.LoginVo;

public interface AuthService {

    void register(SysUser sysUser);

    LoginVo authenticate(LoginRequest request);
}
