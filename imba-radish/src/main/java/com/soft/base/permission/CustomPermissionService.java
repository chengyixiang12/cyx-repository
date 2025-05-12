package com.soft.base.permission;

import com.soft.base.service.SysPermissionService;
import com.soft.base.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: cyx
 * @Description: 权限校验
 * @DateTime: 2024/11/20 19:23
 **/
@Component(value = "cps")
public class CustomPermissionService {

    private final SecurityUtil securityUtil;

    private final SysPermissionService sysPermissionService;

    @Autowired
    public CustomPermissionService(SecurityUtil securityUtil,
                                   SysPermissionService sysPermissionService) {
        this.securityUtil = securityUtil;
        this.sysPermissionService = sysPermissionService;
    }

    public boolean hasPermission(String... permissions) {
        if (permissions == null) {
            return false;
        }
        List<String> roleCodes = securityUtil.getRoleCodes();
        if (roleCodes == null) {
            return false;
        }

        List<String> permissionsData = sysPermissionService.getPermissionsByRoleCodes(roleCodes);

        return permissionsData
                .stream()
                .filter(StringUtils::hasText)
                .anyMatch(item -> PatternMatchUtils.simpleMatch(permissions, item));
    }
}
