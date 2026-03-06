package com.soft.base.utils;

import com.soft.base.constants.BaseConstant;
import com.soft.base.exception.GlobalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @Author: cyx
 * @Description: 通用工具类
 * @DateTime: 2024/11/7 17:49
 **/

@Component
@Valid
public class UniversalUtil {

    // 定义各类字符池
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    // 合并所有字符池
    private static final String ALL_CHARACTERS = UPPER_CASE + LOWER_CASE + DIGITS + SYMBOLS;

    // 安全随机数生成器
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成随机数
     *
     * @param length
     * @return
     */
    public String generate(Integer length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double random = Math.random() * 10;
            sb.append((int) random);
        }
        return sb.toString();
    }

    /**
     * 生成12位随机密码
     * @return 符合要求的12位随机密码
     */
    public String generatePassword() {
        int passwordLength = 12;
        List<Character> passwordChars = new ArrayList<>(passwordLength);

        // 1. 保底分配：每类字符至少1位，确保包含所有类型
        passwordChars.add(getRandomChar(UPPER_CASE));  // 大写字母
        passwordChars.add(getRandomChar(LOWER_CASE));  // 小写字母
        passwordChars.add(getRandomChar(DIGITS));      // 数字
        passwordChars.add(getRandomChar(SYMBOLS));     // 符号

        // 2. 填充剩余8位：从所有字符中随机选取
        for (int i = 4; i < passwordLength; i++) {
            passwordChars.add(getRandomChar(ALL_CHARACTERS));
        }

        // 3. 打乱字符顺序，避免固定位置的字符类型可被猜测
        Collections.shuffle(passwordChars, SECURE_RANDOM);

        // 4. 将字符列表转为字符串
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    /**
     * 从指定字符池中随机获取一个字符
     * @param charPool 字符池
     * @return 随机字符
     */
    private char getRandomChar(String charPool) {
        int randomIndex = SECURE_RANDOM.nextInt(charPool.length());
        return charPool.charAt(randomIndex);
    }

    /**
     * 获取文件hash
     * @param is
     * @return
     */
    public String generateFileHash(InputStream is) {
        try {
            MessageDigest digest = MessageDigest.getInstance(BaseConstant.TYPE_ALGORITHM);
            try (DigestInputStream dis = new DigestInputStream(is, digest)) {
                byte[] buffer = new byte[BaseConstant.BUFFER_SIZE];
                int length = BaseConstant.BUFFER_SIZE;
                while (length != BaseConstant.FILE_OVER_SIGN) {
                    length = dis.read(buffer);
                }
            }
            byte[] hashBytes = digest.digest();
            return new BigInteger(BaseConstant.SIGN_NUM_POSITIVE, hashBytes).toString(BaseConstant.SCALE_SIXTEEN);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new GlobalException(e);
        }
    }
}
