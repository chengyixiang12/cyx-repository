package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soft.base.dto.UserDto;
import com.soft.base.entity.SysUser;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.service.SysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author cyq
* @description 针对表【users】的数据库操作Service实现
* @createDate 2024-09-30 15:49:52
*/
@Service
@Slf4j
@CacheConfig(cacheNames = "cyx::users")
public class UsersDetailServiceImpl implements UserDetailsService{

    private final SysUsersMapper sysUsersMapper;

    private final SysPermissionService sysPermissionService;

    @Autowired
    public UsersDetailServiceImpl(SysUsersMapper sysUsersMapper, SysPermissionService sysPermissionService) {
        this.sysUsersMapper = sysUsersMapper;
        this.sysPermissionService = sysPermissionService;
    }

    @Override
    @Cacheable(key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUsersMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username).or().eq(SysUser::getEmail, username));
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 权限集合
        List<String> permissions = sysPermissionService.getPermissionsByUserId(sysUser.getId());

        return new UserDto(sysUser.getId(), sysUser.getUsername(), sysUser.getDeptId(), sysUser.getPhone(),
                sysUser.getNickname(), sysUser.getEmail(), sysUser.getPassword(), sysUser.getEnabled(),
                sysUser.getAccountNonExpired(), sysUser.getCredentialsNonExpired(), sysUser.getAccountNonLocked(),
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }
}




