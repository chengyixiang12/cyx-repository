package com.soft.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soft.base.constants.BaseConstant;
import com.soft.base.constants.RedisConstant;
import com.soft.base.constants.TokenConstant;
import com.soft.base.entity.SysUser;
import com.soft.base.enums.SecretKeyEnum;
import com.soft.base.exception.CaptChaErrorException;
import com.soft.base.exception.GlobalException;
import com.soft.base.exception.InvalidLoginMethodException;
import com.soft.base.mapper.SysUsersMapper;
import com.soft.base.request.LoginRequest;
import com.soft.base.service.AuthService;
import com.soft.base.service.SecretKeyService;
import com.soft.base.utils.RSAUtil;
import com.soft.base.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final RSAUtil rsaUtil;

    private final SysUsersMapper sysUsersMapper;

    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String,Object> redisTemplate;

    private final SecretKeyService secretKeyService;

    @Autowired
    public AuthServiceImpl(PasswordEncoder passwordEncoder,
                           RSAUtil rsaUtil,
                           SysUsersMapper sysUsersMapper,
                           AuthenticationManager authenticationManager,
                           RedisTemplate<String,Object> redisTemplate,
                           SecretKeyService secretKeyService) {
        this.passwordEncoder = passwordEncoder;
        this.rsaUtil = rsaUtil;
        this.sysUsersMapper = sysUsersMapper;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.secretKeyService = secretKeyService;
    }

    @Override
    public void register(SysUser sysUser) throws GlobalException {
        try {
            if (sysUsersMapper.exists(Wrappers.lambdaQuery(SysUser.class).eq(SysUser::getUsername, sysUser.getUsername()))) {
                throw new GlobalException("用户已存在");
            }

            String username = sysUsersMapper.getManager(BaseConstant.MANAGER_ROLE_CODE);
            sysUser.setCreateBy(username);
            sysUser.setUpdateBy(username);
            // 解密密码
            String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
            String decrypt = rsaUtil.decrypt(sysUser.getPassword(), privateKey);
            // 使用BCrypt 算法加密密码
            String encode = passwordEncoder.encode(decrypt);
            sysUser.setPassword(encode);
            // 设置默认值
            sysUser.setDefault();
            sysUsersMapper.insert(sysUser);
        } catch (Exception e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Override
    public LoginVo authenticate(LoginRequest request) throws Exception {
        try {
            if (BaseConstant.LOGIN_METHOD_PASSWORD.equals(request.getLoginMethod())) {
                String privateKey = secretKeyService.getPrivateKey(SecretKeyEnum.USER_PASSWORD_KEY.getType());
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername()
                        , rsaUtil.decrypt(request.getPassword(), privateKey)));
            } else if (BaseConstant.LOGIN_METHOD_EMAIL.equals(request.getLoginMethod())) {
                String captCha = (String) redisTemplate.opsForValue().get(RedisConstant.EMAIL_CAPTCHA_KEY + request.getUsername());
                if (!request.getPassword().equals(captCha)) {
                    throw new CaptChaErrorException("验证码错误");
                }
                redisTemplate.delete(RedisConstant.EMAIL_CAPTCHA_KEY + request.getUsername());
            } else {
                throw new InvalidLoginMethodException("无效的登录方式");
            }
            LoginVo loginVo = new LoginVo();
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisConstant.AUTHORIZATION_USERNAME + token, request.getUsername(), RedisConstant.AUTHORIZATION_EXPIRE, TimeUnit.SECONDS);
            loginVo.setToken(TokenConstant.TOKEN_PREFIX + token);
            loginVo.setUsername(request.getUsername());
            return loginVo;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DisabledException e) {
            throw new DisabledException(e.getMessage());
        } catch (LockedException e) {
            throw new LockedException(e.getMessage());
        } catch (CredentialsExpiredException e) {
            throw new CredentialsExpiredException(e.getMessage());
        } catch (AccountExpiredException e) {
            throw new AccountExpiredException(e.getMessage());
        } catch (CaptChaErrorException e) {
            throw new CaptChaErrorException(e.getMessage());
        } catch (InvalidLoginMethodException e) {
            throw new InvalidLoginMethodException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
