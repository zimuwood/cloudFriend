package com.example.cloudFriends.Job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 * @author CloudyW
 * @version 1.0
 */
@Component
@Slf4j
@EnableScheduling
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    List<Long> mainUserList = Arrays.asList(1L);

    // 每天预热
    @Scheduled(cron = "0 20 22 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("cloudFriends:precachejob:docache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainUserList) {
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    String redisKey = String.format("cloudFriend:user:recommend:%s", userId);
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    IPage<User> userPage = userService.page(new Page<>(1, 10), queryWrapper);
                    try {
                        valueOperations.set(redisKey, userPage, 10000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error ", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
