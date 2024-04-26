package com.example.cloudFriends;

import com.example.cloudFriends.Utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author CloudyW
 * @version 1.0
 */
public class AlgorithmUtilsTest {

    @Test
    void testStringCompare() {
        String str1 = "原神启动!";
        String str2 = "崩铁启动!";
        String str3 = "原神~启动!";
        int res1 = AlgorithmUtils.minDistance(str1, str2);
        int res2 = AlgorithmUtils.minDistance(str1, str3);
        System.out.println("res1: " + res1);
        System.out.println("res2: " + res2);
    }

    @Test
    void testTagsCompare() {
        List<String> user1 = Arrays.asList("Java", "大一", "男");
        List<String> user2 = Arrays.asList("Java", "大二", "女");
        List<String> user3 = Arrays.asList("Python", "大三", "女");
        int res1 = AlgorithmUtils.minDistance(user1, user2);
        int res2 = AlgorithmUtils.minDistance(user1, user3);
        System.out.println("res1: " + res1);
        System.out.println("res2: " + res2);
    }
}
