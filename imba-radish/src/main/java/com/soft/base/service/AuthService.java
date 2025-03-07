package com.soft.base.service;

import com.soft.base.entity.SysUser;
import com.soft.base.exception.GlobalException;
import com.soft.base.request.LoginRequest;
import com.soft.base.vo.LoginVo;

public interface AuthService {

    void register(SysUser sysUser) throws GlobalException;

    LoginVo authenticate(LoginRequest request) throws Exception;
}
