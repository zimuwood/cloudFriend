package com.example.cloudFriends.Once;

import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author CloudyW
 * @version 1.0
 */
@Component
public class InsertUsers {

    @Resource
    private UserService userService;

    /**
     * 批量插入用户
     */
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
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
}
