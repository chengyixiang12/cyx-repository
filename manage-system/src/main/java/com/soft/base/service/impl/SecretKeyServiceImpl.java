package com.soft.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.base.constants.RedisConstant;
import com.soft.base.entity.SysSecretKey;
import com.soft.base.service.SecretKeyService;
import com.soft.base.mapper.SysSecretKeyMapper;
import com.soft.base.utils.RSAUtil;
import com.soft.base.utils.SecurityUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* @author cyq
* @description 针对表【sys_secret_key】的数据库操作Service实现
* @createDate 2024-11-26 16:19:51
*/
@Service
public class SecretKeyServiceImpl extends ServiceImpl<SysSecretKeyMapper, SysSecretKey>
    implements SecretKeyService{

    private final SysSecretKeyMapper sysSecretKeyMapper;

    private final RedisTemplate<String,Object> redisTemplate;

    private final RSAUtil rsaUtil;

    private final SecurityUtil securityUtil;

    public SecretKeyServiceImpl(SysSecretKeyMapper sysSecretKeyMapper,
                                RedisTemplate<String,Object> redisTemplate,
                                RSAUtil rsaUtil,
                                SecurityUtil securityUtil) {
        this.sysSecretKeyMapper = sysSecretKeyMapper;
        this.redisTemplate = redisTemplate;
        this.rsaUtil = rsaUtil;
        this.securityUtil = securityUtil;
    }

    @Cacheable(cacheNames = "cyx::rsa::public", key = "#type")
    @Override
    public String getPublicKey(Integer type) {
        return sysSecretKeyMapper.getPublicKey(type);
    }

    @Cacheable(cacheNames = "cyx::rsa::private", key = "#type")
    @Override
    public String getPrivateKey(Integer type) {
        return sysSecretKeyMapper.getPrivateKey(type);
    }

    @Override
    public void generateKey(Integer type) throws NoSuchAlgorithmException {
        Map<String, String> generate = rsaUtil.generate();
        String privateKey = generate.get("privateKey");
        String publicKey = generate.get("publicKey");
        String username = securityUtil.getUserInfo().getUsername();
        sysSecretKeyMapper.generateKey(type, privateKey, publicKey, username);

        Set<String> keys = new HashSet<>();
        keys.add(RedisConstant.RSA_PUBLIC_KEY + type);
        keys.add(RedisConstant.RSA_PRIVATE_KEY + type);
        redisTemplate.delete(keys);
    }
}




