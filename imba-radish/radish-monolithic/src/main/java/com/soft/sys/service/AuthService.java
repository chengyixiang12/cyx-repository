package com.soft.sys.service;

import com.soft.sys.entity.SysUser;
import com.soft.sys.model.request.LoginDTO;
import com.soft.sys.model.vo.LoginVO;

public interface AuthService {

    void register(SysUser sysUser);

    LoginVO authenticate(LoginDTO request);
}
