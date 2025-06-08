package com.soft.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.entity.SysSecretKey;
import org.apache.ibatis.annotations.Param;

/**
* @author cyq
* @description 针对表【sys_secret_key】的数据库操作Mapper
* @createDate 2024-11-26 16:19:51
* @Entity com.soft.base.entity.SecretKey
*/
public interface SysSecretKeyMapper extends BaseMapper<SysSecretKey> {

    String getPublicKey(@Param("type") String type);

    String getPrivateKey(@Param("type") Integer type);

    void generateKey(@Param("type") String type,
                     @Param("privateKey") String privateKey,
                     @Param("publicKey") String publicKey,
                     @Param("username") String username);
}




