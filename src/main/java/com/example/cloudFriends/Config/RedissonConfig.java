package com.example.cloudFriends.Config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 * @author CloudyW
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    private int redissonDatabase;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);

        // 开发
        config.useSingleServer().setAddress(redisAddress).setDatabase(redissonDatabase);

        // 生产
//        config.useSingleServer().setAddress(redisAddress).setDatabase(redissonDatabase).setPassword(password);

        // 2. 创建实例
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;


//        // Reactive API
//        RedissonReactiveClient redissonReactive = redisson.reactive();

//        // RxJava3 API
//        RedissonRxClient redissonRx = redisson.rxJava();
    }
}
