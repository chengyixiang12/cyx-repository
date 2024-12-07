package com.soft.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft.base.entity.SysSecretKey;

import java.security.NoSuchAlgorithmException;

/**
* @author cyq
* @description 针对表【sys_secret_key】的数据库操作Service
* @createDate 2024-11-26 16:19:51
*/
public interface SecretKeyService extends IService<SysSecretKey> {

    String getPublicKey(Integer type);

    String getPrivateKey(Integer type);

    void generateKey(Integer type) throws NoSuchAlgorithmException;
}
