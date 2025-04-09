package com.soft.base.controller;

import com.soft.base.annotation.SysLog;
import com.soft.base.constants.RegexConstant;
import com.soft.base.enums.LogModuleEnum;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.model.request.*;
import com.soft.base.model.vo.AllUserVo;
import com.soft.base.model.vo.GetUserVo;
import com.soft.base.model.vo.PageVo;
import com.soft.base.model.vo.UserInfoVo;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/user")
@Slf4j
@Tag(name = "用户接口")
public class SysUserController {

    private final SysUsersService sysUsersService;

    private final RSAUtil rsaUtil;

    private final SecurityUtil securityUtil;

    private final PasswordEncoder passwordEncoder;

    private final SecretKeyService secretKeyService;

    private final SysPermissionService sysPermissionService;

    @Autowired
    public SysUserController(SysUsersService sysUsersService,
                             RSAUtil rsaUtil,
                             SecurityUtil securityUtil,
                             PasswordEncoder passwordEncoder,
                             SecretKeyService secretKeyService,
                             SysPermissionService sysPermissionService) {
        this.sysUsersService = sysUsersService;
        this.rsaUtil = rsaUtil;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
        this.secretKeyService = secretKeyService;
        this.sysPermissionService = sysPermissionService;
    }

    @PostMapping(value = "/getAllUsers")
    @Operation(summary = "获取所有用户")
    public R<PageVo<AllUserVo>> getAllUsers(@RequestBody GetAllUsersRequest request) {
        try {
            PageVo<AllUserVo> allUsers = sysUsersService.getAllUsers(request);
            return R.ok(allUsers);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "修改密码", module = LogModuleEnum.USER)
    @PutMapping(value = "/editPassword")
    @Operation(summary = "修改密码")
    public R editPassword(@RequestBody EditPasswordRequest request) {
        if (StringUtils.isBlank(request.getOriginalPass())) {
            return R.fail("原密码不能为空");
        }
        if (StringUtils.isBlank(request.getTargetPass())) {
            return R.fail("新密码不能为空");
        }
        try {
            String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
            String originalDecrypt = rsaUtil.decrypt(request.getOriginalPass(), privateKey);
            String password = securityUtil.getUserInfo().getPassword();
            if (!passwordEncoder.matches(originalDecrypt, password)) {
                return R.fail("原密码不正确");
            }
            Long id = securityUtil.getUserInfo().getId();
            sysUsersService.editPassword(request.getTargetPass(), id);
            return R.ok("修改成功", null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "重置密码", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_reset')")
    @PutMapping(value = "/resetPassword")
    @Operation(summary = "重置密码")
    public R resetPassword(@RequestBody ResetPasswordRequest request) {
        if (StringUtils.isBlank(request.getPassword())) {
            return R.fail("新密码不能为空");
        }
        try {
            sysUsersService.resetPassword(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "添加用户", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_add')")
    @PostMapping
    @Operation(summary = "添加用户")
    public R saveUser(@RequestBody SaveUserRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            return R.fail("用户名不能为空");
        }
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名只能包含英文字母或数字");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            return R.fail("密码不能为空");
        }
        if (request.getDeptId() == null) {
            return R.fail("部门不能为空");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            return R.fail("邮箱不能为空");
        }
        if (sysUsersService.existsEmail(request.getEmail())) {
            return R.fail("邮箱已注册");
        }
        try {
            sysUsersService.saveUser(request);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @SysLog(value = "编辑用户", module = LogModuleEnum.USER)
    @PreAuthorize(value = "@cps.hasPermission('sys_user_edit')")
    @PutMapping
    @Operation(summary = "编辑用户")
    public R editUser(@RequestBody EditUserRequest request) {
        if (request.getId() == null) {
            return R.fail("id不能为空");
        }
        if (request.getDeptId() == null) {
            return R.fail("部门不能为空");
        }
        if (StringUtils.isBlank(request.getEmail())) {
            return R.fail("邮箱不能为空");
        }
        if (sysUsersService.existsEmail(request.getId(), request.getEmail())) {
            return R.fail("邮箱已注册");
        }
        try {
            String username = sysUsersService.getUsername(request.getId());
            sysUsersService.editUser(request, username);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/getUserInfo")
    @Operation(summary = "获取登录用户信息")
    public R<UserInfoVo> getUserInfo() {
        try {
            UserInfoVo userInfoVo = new UserInfoVo();
            BeanUtils.copyProperties(securityUtil.getUserInfo(), userInfoVo);
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

    @GetMapping(value = "/getUser/{id}")
    @Operation(summary = "获取用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<GetUserVo> getUser(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("id不能为空");
        }
        try {
            GetUserVo getUserVo = sysUsersService.getUser(id);
            return R.ok(getUserVo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/lockUser/{id}")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_lock')")
    @Operation(summary = "锁定用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R<Object> lockUser(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            String username = sysUsersService.getUsername(id);
            sysUsersService.lockUser(username);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @GetMapping(value = "/unlockUser/{id}")
    @PreAuthorize(value = "@cps.hasPermission('sys_user_unlock')")
    @Operation(summary = "解锁用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.PATH)
    public R unLockUser(@PathVariable(value = "id") Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            String username = sysUsersService.getUsername(id);
            sysUsersService.unlockUser(username);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PutMapping(value = "/resetUsername")
    @Operation(summary = "重置用户名")
    public R resetUsername(@RequestBody ResetUsernameRequest request) {
        if (request.getId() == null) {
            return R.fail("主键不能为空");
        }
        if (StringUtils.isBlank(request.getUsername())) {
            return R.fail("用户名不能为空");
        }
        if (!Pattern.matches(RegexConstant.USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名只能包含英文字母或数字");
        }
        if (sysUsersService.existsUsername(request.getUsername())) {
            return R.fail("用户名重复");
        }
        try {
            String username = sysUsersService.getUsername(request.getId());
            sysUsersService.resetUsername(request, username);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @DeleteMapping
    @PreAuthorize(value = "@cps.hasPermission('sys_user_delete')")
    @Operation(summary = "删除用户")
    @Parameter(name = "id", description = "主键", required = true, in = ParameterIn.QUERY)
    public R<Object> deleteUser(@RequestParam(value = "id", required = false) Long id) {
        if (id == null) {
            return R.fail("主键不能为空");
        }
        try {
            String username = sysUsersService.getUsername(id);
            sysUsersService.deleteUser(id, username);
            return R.ok();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }
}
