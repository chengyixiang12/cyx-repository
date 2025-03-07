package com.soft.base.utils;

import com.soft.ManageSystemApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/4 22:50
 **/

@SpringBootTest(classes = ManageSystemApplication.class)
@Slf4j
public class AESUtilTest {

    private final AESUtil aesUtil;

    @Autowired
    public AESUtilTest(AESUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    @Test
    public void generateAesKey() {
        try {
            // 生成16字节（128位）AES密钥
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // 可以选择128、192、256位
            SecretKey secretKey = keyGen.generateKey();
            byte[] key = secretKey.getEncoded();

            String base64Key = Base64.getEncoder().encodeToString(key);
            System.out.println("Base64 编码后的密钥: " + base64Key);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void encode() {
        try {
//            log.info("加密后的密码为：{}", aesUtil.encrypt("123456"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
