package com.soft.base.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.entity.SysUser;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.enums.WebSocketOrderEnum;
import com.soft.base.exception.GlobalException;
import com.soft.base.model.request.LoginRequest;
import com.soft.base.model.vo.LoginVo;
import com.soft.base.service.AuthService;
import com.soft.base.service.SecretKeyService;
import com.soft.base.service.SysDeptService;
import com.soft.base.service.SysUsersService;
import com.soft.base.utils.RSAUtil;
import com.soft.base.websocket.WebSocketConcreteHolder;
import com.soft.base.websocket.WebSocketSessionManager;
import com.soft.base.websocket.handle.message.WebSocketConcreteHandler;
import com.soft.base.websocket.handle.message.concrete.ForceOfflineHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value(value = "${radish.token.expire-time}")
    private Long authorizationExpire;

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SysUsersService sysUsersService;

    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String,Object> redisTemplate;

    private final SecretKeyService secretKeyService;

    private final StringRedisTemplate stringRedisTemplate;

    private final SysDeptService sysDeptService;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder,
                           RSAUtil rsaUtil,
                           SysUsersService sysUsersService,
                           AuthenticationManager authenticationManager,
                           RedisTemplate<String,Object> redisTemplate,
                           SecretKeyService secretKeyService,
                           StringRedisTemplate stringRedisTemplate,
                           SysDeptService sysDeptService) {
        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;
        this.sysUsersService = sysUsersService;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.secretKeyService = secretKeyService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.sysDeptService = sysDeptService;
    }

    @Override
    public void register(SysUser sysUser) {
        try {
            Long userId = sysUsersService.getManager(BaseConstant.MANAGER_ROLE_CODE);
            sysUser.setCreateBy(userId);
            sysUser.setUpdateBy(userId);
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
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public LoginVo authenticate(LoginRequest request) {
        Long id;
        try {
            switch (request.getLoginMethod()) {
                case BaseConstant.LOGIN_METHOD_PASSWORD: {
                    String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername()
                            , rsaUtil.decrypt(request.getPassword(), privateKey)));
                    id = sysUsersService.getPrimaryKeyByUsername(request.getUsername());
                    break;
                }
                case BaseConstant.LOGIN_METHOD_EMAIL: {
                    SysUser sysUser = sysUsersService.getUserByEmail(request.getEmail());
                    id = sysUser.getId();
                    request.setUsername(sysUser.getUsername());
                    String emailCaptCha = (String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + sysUser.getUsername());
                    if (!request.getEmailCaptcha().equals(emailCaptCha)) {
                        throw new GlobalException("验证码错误");
                    }
                    redisTemplate.delete(RedisConstant.EMAIL_CAPTCHA_KEY + request.getUsername());
                    break;
                }
                default: {
                    throw new GlobalException("无效的登录方式");
                }
            }

            // 清空错误登录次数
            redisTemplate.delete(RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername());

            // 同一个用户只能有一个客户端登录
            WebSocketSession session = WebSocketSessionManager.getSession(id);
            ForceOfflineHandler concreteHandler = (ForceOfflineHandler) WebSocketConcreteHolder.getConcreteHandler(WebSocketOrderEnum.FORCE_OFFLINE.toString());
            JSONObject forceOfflineParam = new JSONObject();
            forceOfflineParam.put("order", WebSocketOrderEnum.FORCE_OFFLINE.toString());
            forceOfflineParam.put("receiver", id);
            TextMessage textMessage = new TextMessage(forceOfflineParam.toJSONString());
            concreteHandler.handle(session, textMessage);

            LoginVo loginVo = new LoginVo();
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token, request.getUsername(), authorizationExpire, TimeUnit.SECONDS);
            loginVo.setToken(token);
            loginVo.setUsername(request.getUsername());
            return loginVo;
        } catch (BadCredentialsException e) {
            Long errorTime;
            try {
                errorTime = Long.parseLong(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername())));
                if (BaseConstant.LONG_INIT_VAL.equals(errorTime)) {
                    sysUsersService.lockUser(request.getUsername());
                    throw new LockedException("登录次数用完，您的账号已锁定");
                }
                errorTime = redisTemplate.opsForValue().decrement(RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername());
            } catch (NullPointerException en) {
                errorTime = BaseConstant.MAX_LOGIN_ERROR_TIME;
                redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_ERROR_TIME + request.getUsername(), BaseConstant.MAX_LOGIN_ERROR_TIME);
            }
            throw new BadCredentialsException(e.getMessage() + "，您还有" + errorTime + "次登录机会");
        } catch (DisabledException e) {
            throw new DisabledException(e.getMessage());
        } catch (LockedException e) {
            throw new LockedException(e.getMessage());
        } catch (CredentialsExpiredException e) {
            throw new CredentialsExpiredException(e.getMessage());
        } catch (AccountExpiredException e) {
            throw new AccountExpiredException(e.getMessage());
        } catch (GlobalException | IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
