package com.soft.sys.utils;

import com.soft.sys.constants.BaseConstant;
import com.soft.sys.model.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/10/25 16:37
 **/

@Component
public class SecurityUtil {

    /**
     * 从上下文获取用户信息
     * @return
     */
    public UserDTO getUserInfo() {
        return (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 从上下文获取用户角色编码
     * @return
     */
    public List<String> getRoleCodes() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    /**
     * 是否是管理员
     * @return
     */
    public boolean isAdmin() {
        return getRoleCodes().contains(BaseConstant.Role.MANAGER_ROLE_CODE);
    }
}
