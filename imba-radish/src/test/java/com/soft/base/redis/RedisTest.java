package com.soft.base.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author: cyx
 * @Description: TODO
 * @DateTime: 2024/11/21 17:22
 **/

@SpringBootTest
@Slf4j
public class RedisTest {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTest(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Test
    public void redisTest() {
        UserDetails user = (User) redisTemplate.opsForValue().get("users::admin");
        System.out.println(user);
    }
}
