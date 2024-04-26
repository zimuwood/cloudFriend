package com.example.cloudFriends;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CloudyW
 * @version 1.0
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        // list
        List<String> list = new ArrayList<>();
        list.add("cloudyW");

        RList<Object> rlist = redissonClient.getList("test-list");
        rlist.add("cloudyW");

    }
}
