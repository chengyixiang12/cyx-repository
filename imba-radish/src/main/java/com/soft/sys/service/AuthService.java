package com.soft.sys.service;

import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.LoginRequest;
import com.soft.sys.model.vo.LoginVo;

public interface AuthService {

    void register(SysUser sysUser);

    LoginVo authenticate(LoginRequest request);
}
