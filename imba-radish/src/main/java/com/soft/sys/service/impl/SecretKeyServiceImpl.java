package com.soft.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft.sys.constants.RedisConstant;
import com.soft.sys.entity.SysSecretKey;
import com.soft.sys.exception.GlobalException;
import com.soft.sys.mapper.SysSecretKeyMapper;
import com.soft.sys.model.request.GenerateKeyRequest;
import com.soft.sys.model.request.GetSecretKeyListRequest;
import com.soft.sys.model.vo.PageVO;
import com.soft.sys.model.vo.SysSecretKeyVo;
import com.soft.sys.service.SecretKeyService;
import com.soft.sys.utils.AESUtil;
import com.soft.sys.utils.RSAUtil;
import com.soft.sys.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author cyq
* @description 针对表【sys_secret_key】的数据库操作Service实现
* @createDate 2024-11-26 16:19:51
*/
@Service
@RequiredArgsConstructor
public class SecretKeyServiceImpl extends ServiceImpl<SysSecretKeyMapper, SysSecretKey>
    implements SecretKeyService{

    private final SysSecretKeyMapper sysSecretKeyMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RSAUtil rsaUtil;

    private final AESUtil aesUtil;

    private final SecurityUtil securityUtil;

    @Cacheable(cacheNames = "cyx:rsa:public", key = "#type")
    @Override
    public String getPublicKey(Integer type) {
        return sysSecretKeyMapper.getPublicKey(type);
    }

    @Cacheable(cacheNames = "cyx:rsa:private", key = "#type")
    @Override
    public String getPrivateKey(Integer type) {
        return sysSecretKeyMapper.getPrivateKey(type);
    }

    @Override
    public void generateKey(GenerateKeyRequest request) throws NoSuchAlgorithmException {
        String privateKey = null;
        String publicKey = null;
        switch (request.getSecretType()) {
            case "1": {
                Map<String, String> generate = rsaUtil.generate();
                privateKey = generate.get("privateKey");
                publicKey = generate.get("publicKey");
            }
            break;
            case "2": {
                publicKey = aesUtil.generate();
            }
            break;
            default:
                throw new GlobalException("未知的密钥类型");
        }
        Integer type = request.getType();
        SysSecretKey sysSecretKey = new SysSecretKey();
        sysSecretKey.setPrivateKey(privateKey);
        sysSecretKey.setSecretType(request.getSecretType());
        sysSecretKey.setPublicKey(publicKey);
        sysSecretKey.setType(request.getType());
        sysSecretKey.setDescription(request.getDescription());
        sysSecretKeyMapper.insert(sysSecretKey);

        // 清空redis中缓存的密钥
        Set<String> keys = new HashSet<>();
        keys.add(RedisConstant.RSA_PUBLIC_KEY + type);
        keys.add(RedisConstant.RSA_PRIVATE_KEY + type);
        redisTemplate.delete(keys);
    }

    @Override
    public PageVO<SysSecretKeyVo> getSecretKeyList(GetSecretKeyListRequest request) {
        Page<SysSecretKey> page = new Page<>(request.getPageNum(), request.getPageSize());

        LambdaQueryWrapper<SysSecretKey> wrapper = new LambdaQueryWrapper<SysSecretKey>()
            .eq(request.getType() != null, SysSecretKey::getType, request.getType())
            .orderByAsc(SysSecretKey::getType);

        page = baseMapper.selectPage(page, wrapper);

        PageVO<SysSecretKeyVo> pageVo = new PageVO<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setRecords(page.getRecords().stream().map(entity -> {
            SysSecretKeyVo vo = new SysSecretKeyVo();
            BeanUtils.copyProperties(entity, vo);
            // 公钥脱敏：只显示前后20个字符
            if (entity.getPublicKey() != null && entity.getPublicKey().length() > 40) {
                vo.setPublicKey(entity.getPublicKey().substring(0, 20) + "..." + entity.getPublicKey().substring(entity.getPublicKey().length() - 20));
            }
            return vo;
        }).collect(Collectors.toList()));

        return pageVo;
    }
}




