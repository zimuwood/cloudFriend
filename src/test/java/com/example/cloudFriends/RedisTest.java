package com.example.cloudFriends;

import com.example.cloudFriends.Models.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author CloudyW
 * @version 1.0
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test () {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 增
        valueOperations.set("redisTest1", "I'm TEST1!!!");
        valueOperations.set("redisTest2", 2);
        valueOperations.set("redisTest3", 3.0);
        User user = new User();
        user.setUsername("aaa");
        valueOperations.set("redisTest4", user);

        // 查

    }
}
