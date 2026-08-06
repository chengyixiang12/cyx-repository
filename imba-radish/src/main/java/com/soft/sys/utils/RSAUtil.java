package com.soft.sys.utils;

import com.soft.sys.exception.GlobalException;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * @Author: cyx
 * @Description: RSA非对称加密工具类
 * @DateTime: 2024/11/26 14:21
 **/

@Component
public class RSAUtil {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

    /**
     * 生成公钥私钥
     * @return map containing "publicKey" and "privateKey"
     */
    public Map<String, String> generate() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyGen.initialize(2048);

            KeyPair keyPair = keyGen.generateKeyPair();

            String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            return Map.of("privateKey", privateKeyString, "publicKey", publicKeyString);
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException(e.getLocalizedMessage());
        }
    }

    /**
     * RSA解密
     * @param ciphertext  Base64编码的密文
     * @param privateKey  Base64编码的私钥
     * @return 解密后的明文字符串
     */
    public String decrypt(String ciphertext, String privateKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(keySpec));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException
                 | InvalidKeySpecException
                 | NoSuchPaddingException
                 | InvalidKeyException
                 | IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new GlobalException(e.getLocalizedMessage());
        }
    }

    /**
     * RSA加密
     * @param plaintext  明文字符串
     * @param publicKey  Base64编码的公钥
     * @return Base64编码的密文
     */
    public String encrypt(String plaintext, String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(spec));
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException
                 | InvalidKeySpecException
                 | NoSuchPaddingException
                 | InvalidKeyException
                 | IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new GlobalException(e.getLocalizedMessage());
        }
    }
}
