package com.soft.base.controller;

import com.soft.base.constants.RegexConstant;
import com.soft.base.core.annotation.SysLock;
import com.soft.base.core.annotation.SysLog;
import com.soft.base.entity.SysUser;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.model.dto.UserDto;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.GetUserVo;
import com.soft.base.model.vo.PageVO;
import com.soft.base.model.vo.UserInfoVo;
import com.soft.base.model.vo.UsersVo;
import com.soft.base.resultapi.R;
import com.soft.base.service.SecretKeyService;
import com.soft.base.service.SysPermissionService;
import com.soft.base.service.SysUsersService;
import com.soft.base.utils.RSAUtil;
import com.soft.base.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/user")
@Slf4j
@Tag(name = "用户")
@Validated
@RequiredArgsConstructor
public class SysUserController {

    private final SysUsersService sysUsersService;

    private final RSAUtil rsaUtil;

    private final SecurityUtil securityUtil;

    private final PasswordEncoder passwordEncoder;

    private final SecretKeyService secretKeyService;

    private final SysPermissionService sysPermissionService;

    @PostMapping(value = "/getUsers")
    @Operation(summary = "获取用户列表")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_page')")
    public R<PageVO<UsersVo>> getUsers(@RequestBody GetUsersRequest request) {
        PageVO<UsersVo> allUsers = sysUsersService.getUsers(request);
        return R.ok(allUsers);
    }

    @SysLog(value = "修改密码", module = LogModuleEnum.USER)
    @PutMapping(value = "/editPassword")
    @Operation(summary = "修改密码")
    public R<Object> editPassword(@RequestBody @Valid EditPasswordRequest request) {
        String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
        String originalDecrypt = rsaUtil.decrypt(request.getOriginalPass(), privateKey);
        String password = securityUtil.getUserInfo().getPassword();
        if (!passwordEncoder.matches(originalDecrypt, password)) {
            return R.fail("原密码不正确");
        }
        Long id = securityUtil.getUserInfo().getId();
        sysUsersService.editPassword(request.getTargetPass(), id);
        return R.ok("修改成功", null);
    }

    @SysLog(value = "重置密码", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_reset')")
    @GetMapping(value = "/resetPassword")
    @Operation(summary = "重置密码")
    public R<Object> resetPassword(@RequestParam(value = "id", required = false) @NotNull(message = "id不能为空") Long id) {
        sysUsersService.resetPassword(id);
        return R.ok("密码重置成功", null);
    }

    @SysLock(name = "user")
    @SysLog(value = "添加用户", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_add')")
    @PostMapping
    @Operation(summary = "添加用户")
    public R<Object> saveUser(@RequestBody @Valid SaveUserRequest request) {
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名只能包含英文字母或数字");
        }
        if (sysUsersService.existsEmail(request.getEmail())) {
            return R.fail("邮箱已注册");
        }
        sysUsersService.saveUser(request);
        return R.ok("添加成功", null);
    }

    @SysLock(name = "user")
    @SysLog(value = "编辑用户", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_edit')")
    @PutMapping
    @Operation(summary = "编辑用户")
    public R<Object> editUser(@RequestBody @Valid EditUserRequest request) {
        if (StringUtils.isNotBlank(request.getEmail()) && sysUsersService.existsEmail(request.getId(), request.getEmail())) {
            return R.fail("邮箱已注册");
        }
        String username = sysUsersService.getUsername(request.getId());
        sysUsersService.editUser(request, username);
        return R.ok("修改成功", null);
    }

    @GetMapping(value = "/getUserInfo")
    @Operation(summary = "获取登录用户信息")
    public R<UserInfoVo> getUserInfo() {
        try {
            UserInfoVo userInfoVo = new UserInfoVo();
            UserDto userInfo = securityUtil.getUserInfo();
            SysUser sysUser = sysUsersService.getById(userInfo.getId());
            userInfoVo.setId(String.valueOf(sysUser.getId()));
            userInfoVo.setUsername(sysUser.getUsername());
            userInfoVo.setEmail(sysUser.getEmail());
            userInfoVo.setPhone(sysUser.getPhone());
            userInfoVo.setAvatar(String.valueOf(sysUser.getAvatar()));
            userInfoVo.setNickname(sysUser.getNickname());
            userInfoVo.setDeptId(String.valueOf(sysUser.getDeptId()));

            List<String> roleCodes = securityUtil.getRoleCodes();
            if (roleCodes != null && !roleCodes.isEmpty()) {
                userInfoVo.setPermissions(sysPermissionService.getPermissionsByRoleCodes(roleCodes));
            }
            return R.ok(userInfoVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getUser")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_get_user_info')")
    @Operation(summary = "获取用户详情")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<GetUserVo> getUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        GetUserVo getUserVo = sysUsersService.getUser(id);
        return R.ok(getUserVo);
    }

    @SysLog(value = "锁定用户", module = LogModuleEnum.USER)
    @GetMapping(value = "/lockUser")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_lock')")
    @Operation(summary = "锁定用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> lockUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        String username = sysUsersService.getUsername(id);
        sysUsersService.lockUser(username);
        return R.ok("用户已锁定", null);
    }

    @SysLog(value = "解锁用户", module = LogModuleEnum.USER)
    @GetMapping(value = "/unlockUser")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_unlock')")
    @Operation(summary = "解锁用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> unLockUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        String username = sysUsersService.getUsername(id);
        sysUsersService.unlockUser(username);
        return R.ok("用户已解锁", null);
    }

    @SysLog(value = "重置用户名", module = LogModuleEnum.USER)
    @SysLock(name = "user")
    @PutMapping(value = "/resetUsername")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_reset_username')")
    @Operation(summary = "重置用户名")
    public R<Object> resetUsername(@RequestBody @Valid ResetUsernameRequest request) {
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名只能包含英文字母或数字");
        }
        if (sysUsersService.existsUsername(request.getUsername())) {
            return R.fail("用户名重复");
        }
        String username = sysUsersService.getUsername(request.getId());
        sysUsersService.resetUsername(request, username);
        return R.ok();
    }

    @SysLog(value = "删除用户", module = LogModuleEnum.USER)
    @DeleteMapping
    @PreAuthorize(value = "@cps.hasPermission('sys_user_delete')")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        String username = sysUsersService.getUsername(id);
        sysUsersService.deleteUser(id, username);
        return R.ok("删除成功", null);
    }

    @SysLog(value = "启用用户", module = LogModuleEnum.USER)
    @GetMapping(value = "/enableUser")
    @Operation(summary = "启用用户")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_enable')")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> enableUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        String username = sysUsersService.getUsername(id);
        sysUsersService.enableUser(username);
        return R.ok("用户已启用", null);
    }

    @SysLog(value = "禁用用户", module = LogModuleEnum.USER)
    @GetMapping(value = "/forbiddenUser")
    @Operation(summary = "禁用用户")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_forbidden')")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> forbiddenUser(@RequestParam(value = "id", required = false) @NotNull(message = "主键不能为空") Long id) {
        String username = sysUsersService.getUsername(id);
        sysUsersService.forbiddenUser(username);
        return R.ok("用户已禁用", null);
    }

    @PutMapping(value = "/editSelf")
    @Operation(summary = "修改个人信息")
    public R<Object> editSelf(@RequestBody EditSelfRequest request) {
        Long id = securityUtil.getUserInfo().getId();
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setNickname(request.getNickname());
        sysUser.setEmail(request.getEmail());
        sysUser.setPhone(request.getPhone());
        sysUser.setAvatar(request.getAvatar());

        sysUsersService.updateById(sysUser);
        return R.ok();
    }
}
