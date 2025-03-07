package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
@Deprecated
public class AESUtil {

    // AES 加密
    public String encrypt(String data, String secret) throws Exception {
        // 创建 AES 密钥
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");

        // 创建 AES 加密器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(BaseConstant.AES_OFFSET.getBytes(StandardCharsets.UTF_8))); // IV 为全零

        // 执行加密
        byte[] encrypted = cipher.doFinal(data.getBytes());

        // 返回 Base64 编码的字符串
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // AES 解密
    public String decrypt(String encryptedData, String secret) throws Exception {
        // 创建 AES 密钥
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");

        // 创建 AES 解密器
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(BaseConstant.AES_OFFSET.getBytes(StandardCharsets.UTF_8))); // IV 为全零

        // 执行解密
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));

        // 返回解密后的字符串
        return new String(decrypted);
    }

    public String generate() throws NoSuchAlgorithmException {
        // 生成16字节（128位）AES密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 可以选择128、192、256位
        SecretKey secretKey = keyGen.generateKey();
        byte[] key = secretKey.getEncoded();

        return Base64.getEncoder().encodeToString(key);
    }
}
