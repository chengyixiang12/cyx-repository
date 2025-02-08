package com.soft.base.utils;

import com.soft.base.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/10/25 16:37
 **/

@Component
public class SecurityUtil {

    /**
     * 从上下文获取用户信息
     * @return
     */
    public UserDto getUserInfo() {
        return (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}
