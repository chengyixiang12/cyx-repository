package com.soft.base.file;

import com.soft.base.rabbitmq.consumer.CaptchaConsume;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.soft.base.constants.BaseConstant.EMAIL_CONTENT_PATH;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/20 15:45
 **/

@SpringBootTest
@Slf4j
public class FileTest {

    private final CaptchaConsume captchaConsume;

    @Autowired
    public FileTest(CaptchaConsume captchaConsume) {
        this.captchaConsume = captchaConsume;
    }

    @Test
    public void test() {
        ClassPathResource resource = new ClassPathResource(EMAIL_CONTENT_PATH);
        try {
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            log.info("第一行：{}", reader.readLine());
            log.info("第二行：{}", reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
