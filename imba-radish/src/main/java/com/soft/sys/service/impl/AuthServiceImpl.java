package com.soft.sys.service.impl;

import com.soft.sys.constants.BaseConstant;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.entity.SysUser;
import com.soft.sys.entity.SysUserRole;
import com.soft.sys.enums.SecretKeyEnum;
import com.soft.sys.enums.WebSocketOrderEnum;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.model.request.LoginRequest;
import com.soft.sys.model.vo.LoginVo;
import com.soft.sys.properties.RadishProperty;
import com.soft.sys.service.*;
import com.soft.sys.utils.RSAUtil;
import com.soft.sys.websocket.api.WebSocketConcreteHolder;
import com.soft.sys.websocket.handler.ForceOfflineHandler;
import com.soft.sys.websocket.receive.ForceOfflineRecParam;
import com.soft.sys.websocket.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RadishProperty radishProperty;

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SysUsersService sysUsersService;

    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String,Object> redisTemplate;

    private final SecretKeyService secretKeyService;

    private final SysDeptService sysDeptService;

    private final SysUserRoleService sysUserRoleService;

    private final SysRoleService sysRoleService;

    @Override
    public void register(SysUser sysUser) {
        try {
            // 解密密码
            String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
            String decrypt = rsaUtil.decrypt(sysUser.getPassword(), privateKey);
            // 使用BCrypt 算法加密密码
            String encode = passwordEncoder.encode(decrypt);
            sysUser.setPassword(encode);
            // 设置默认值
            sysUser.setDefault();
            Long deptId = sysDeptService.getRootDept();
            sysUser.setDeptId(deptId);
            sysUsersService.save(sysUser);

            // 赋予注册用户角色
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setRoleId(sysRoleService.getDefaultRole(BaseConstant.Role.DEFAULT_ROLE_FLAG));
            sysUserRoleService.save(sysUserRole);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public LoginVo authenticate(LoginRequest request) {
        Long id;
        try {
            switch (request.getLoginMethod()) {
                case BaseConstant.LOGIN_METHOD_PASSWORD -> {
                    String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            request.getUsername(), rsaUtil.decrypt(request.getPassword(), privateKey)));
                    id = sysUsersService.getPrimaryKeyByUsername(request.getUsername());
                }
                case BaseConstant.LOGIN_METHOD_EMAIL -> {
                    SysUser sysUser = sysUsersService.getUserByEmail(request.getEmail());
                    id = sysUser.getId();
                    request.setUsername(sysUser.getUsername());
                    String emailCaptcha = (String) redisTemplate.opsForValue()
                            .get(RedisConstant.EMAIL_CAPTCHA_KEY + sysUser.getEmail());
                    if (!request.getEmailCaptcha().equals(emailCaptcha)) {
                        throw new BadCredentialsException("验证码错误");
                    }
                    redisTemplate.delete(RedisConstant.EMAIL_CAPTCHA_KEY + sysUser.getEmail());
                }
                default -> throw new GlobalException("无效的登录方式");
            }

            // 清空错误登录次数
            String errorKey = RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername();
            redisTemplate.delete(errorKey);

            // 同一个用户只能有一个客户端登录
            WebSocketSession session = WebSocketSessionManager.getSession(id);
            if (session != null) {
                ForceOfflineHandler handler = (ForceOfflineHandler) WebSocketConcreteHolder
                        .getConcreteHandler(WebSocketOrderEnum.FORCE_OFFLINE.toString());
                ForceOfflineRecParam param = new ForceOfflineRecParam();
                param.setOrder(WebSocketOrderEnum.FORCE_OFFLINE.toString());
                param.setReceiver(id);
                param.setMsg("该账号已在其他地方登录");
                handler.handle(session, new TextMessage(param.toJsonString()));
            }

            // 客户端指纹
            String fingerprint = request.getFingerprint();
            if (StringUtils.isNotBlank(fingerprint)) {
                redisTemplate.opsForValue().set(RedisConstant.FINGERPRINT + request.getUsername(), fingerprint);
            }

            // 生成 token
            LoginVo loginVo = new LoginVo();
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token,
                    request.getUsername(), radishProperty.getToken().getExpireTime(), TimeUnit.SECONDS);
            loginVo.setToken(token);
            loginVo.setUsername(request.getUsername());
            return loginVo;
        } catch (BadCredentialsException e) {
            throw handleBadCredentials(request, e);
        } catch (IOException e) {
            throw new GlobalException(e);
        }
    }

    /**
     * 处理密码错误：递减错误次数，达到上限后锁定用户
     */
    private BadCredentialsException handleBadCredentials(LoginRequest request, BadCredentialsException e) {
        String errorKey = RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername();
        Object cached = redisTemplate.opsForValue().get(errorKey);
        Long remaining;

        if (cached == null) {
            // 首次错误，初始化计数器
            remaining = BaseConstant.MAX_LOGIN_ERROR_TIME;
            redisTemplate.opsForValue().set(errorKey, BaseConstant.MAX_LOGIN_ERROR_TIME);
        } else {
            long current = Long.parseLong(cached.toString());
            if (BaseConstant.LONG_INIT_VAL.equals(current)) {
                sysUsersService.lockUser(request.getUsername());
                throw new LockedException("账号已锁定");
            }
            remaining = redisTemplate.opsForValue().decrement(errorKey);
        }
        return new BadCredentialsException(e.getMessage() + "，您还有" + remaining + "次登录机会");
    }
}
