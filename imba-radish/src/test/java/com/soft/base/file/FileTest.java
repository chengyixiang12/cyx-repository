package com.soft.base.file;

import com.soft.base.rabbitmq.consumer.EmailConsume;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: cyx
 * @Description: 
 * @DateTime: 2024/11/20 15:45
 **/

@SpringBootTest
@Slf4j
public class FileTest {

    private final EmailConsume emailConsume;

    @Autowired
    public FileTest(EmailConsume emailConsume) {
        this.emailConsume = emailConsume;
    }

    @Test
    public void test() {

    }
}
