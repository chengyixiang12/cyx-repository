package com.test;

import com.soft.ImbaRadishApplication;
import com.soft.sys.core.handle.EnvLoaderHandler;
import com.soft.sys.utils.AESUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 *
 * @author cyx
 * @date 2026-07-20
 */
@SpringBootTest(classes = ImbaRadishApplication.class)
@ContextConfiguration(initializers = EnvLoaderHandler.class)
public class AESTest {

    @Autowired
    private AESUtil aesUtil;

    @Test
    public void test() throws Exception {
        AESUtil aesUtil = new AESUtil();
        String encrypted = aesUtil.encrypt("20270131235959", "aF04kx0MtzBhkHmtGqLcCw==");
        System.out.println("加密后：" + encrypted);
        System.out.println("解密后：" + aesUtil.decrypt(encrypted, "aF04kx0MtzBhkHmtGqLcCw=="));
    }
}
