package com.example.cloudFriends;

import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CloudyW
 * @version 1.0
 */
@SpringBootTest
class InsertUsersTest {

    @Resource
    private UserService userService;

    ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(60, 1000, 10000,TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fakeCloudyW");
            user.setUserAccount("1000000000");
            user.setAvatarUrl("https://pic.imgdb.cn/item/65ed571f9f345e8d0394c4bb.png");
            user.setGender(0);
            user.setUserPassword("b9cd8fa3bb340fe02d8b476512fc0691");
            user.setPhone("12345678911");
            user.setEmail("123@qq.com");
            user.setUserStatus(0);
            user.setCreateTime(new Date());
            user.setUserRole(0);
            user.setTags("[\"" + "男\"]");
            user.setProfile("阿巴阿巴");
            userList.add(user);
        }
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        final int BATH_SIZE = 5000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < INSERT_NUM / BATH_SIZE; i++) {
            List<User> userList = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("fakeCloudyW");
                user.setUserAccount("1000000000");
                user.setAvatarUrl("https://pic.imgdb.cn/item/65ed571f9f345e8d0394c4bb.png");
                user.setGender(0);
                user.setUserPassword("b9cd8fa3bb340fe02d8b476512fc0691");
                user.setPhone("12345678911");
                user.setEmail("123@qq.com");
                user.setUserStatus(0);
                user.setCreateTime(new Date());
                user.setUserRole(0);
                user.setTags("[\"" + "男\"]");
                user.setProfile("阿巴阿巴");
                userList.add(user);
                if (j % BATH_SIZE == 0) {
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, 10000);
            }, myThreadPool);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
