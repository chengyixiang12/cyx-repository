package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soft.base.dto.UserDto;
import com.soft.base.entity.SysUser;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.service.SysPermissionService;
import com.soft.base.service.SysRoleService;
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

    private final SysRoleService sysRoleService;

    @Autowired
    public UsersDetailServiceImpl(SysUsersMapper sysUsersMapper, SysRoleService sysRoleService) {
        this.sysUsersMapper = sysUsersMapper;
        this.sysRoleService = sysRoleService;
    }

    @Override
    @Cacheable(key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUsersMapper.selectOne(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, username).or().eq(SysUser::getEmail, username));
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // 角色集合
        List<String> roleCodes = sysRoleService.getRoleCodesByUserId(sysUser.getId());

        return new UserDto(sysUser.getId(), sysUser.getUsername(), sysUser.getDeptId(), sysUser.getPhone(),
                sysUser.getNickname(), sysUser.getEmail(), sysUser.getPassword(), sysUser.getEnabled(),
                sysUser.getAccountNonExpired(), sysUser.getCredentialsNonExpired(), sysUser.getAccountNonLocked(),
                roleCodes.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());
    }
}




