package com.example.cloudFriends.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Mapper.UserMapper;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.request.UserUpdateInfoRequest;
import com.example.cloudFriends.Models.tools.UserId_Distance;
import com.example.cloudFriends.Service.UserService;
import com.example.cloudFriends.Utils.AlgorithmUtils;
import com.example.cloudFriends.Utils.TagsToJSONUtil;
import com.example.cloudFriends.exception.BusinessException;
//import jakarta.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.cloudFriends.Constant.UserConstant.*;

/**
* @author cloudyW
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-03-03 16:13:54
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "cloudyW";

    @Override
    public User userRegister(String username, String userAccount, String userPassword, String checkPassword, Integer gender) {
        //1. 校验
        if (StringUtils.isAnyBlank(username, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "用户名或密码有空格");
        }
        //用户名不能包含特殊字符，4-16位
        String usernameLegalPattern = "^\\w{4,16}$";//"^[_a-zA-Z0-9]{4,16}$"
        if (!Pattern.matches(usernameLegalPattern, username)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "用户名格式错误");
        }
        //密码不能包含特殊字符，6-16位
        String passwordLegalPattern = "^\\w{6,16}$";
        if (!Pattern.matches(passwordLegalPattern, userPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "密码格式错误");
        }
        //两次输入密码不一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "两次输入密码不一致");
        }
        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_DUPLICATE, "账号重复");
        }

        //2. 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3. 数据库插入数据
        User user = new User();
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setGender(gender);
        user.setAvatarUrl(USER_DEFAULT_AVATAR);
        user.setCreateTime(new Date());
        user.setProfile("这个人很懒，还没有写简介-_-!");
        String genderTag = gender == 0 ? "男" : "女";
        user.setTags("[\"" + genderTag + "\"]");
        boolean savaResult = this.save(user);
        if (!savaResult) {
            throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "数据库存储错误");
        }

        return getSafetyUser(user);
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "用户名或密码有空格");
        }
        //账号不能包含特殊字符，10位
        String accountLegalPattern = "^\\d{10}$";
        if (!Pattern.matches(accountLegalPattern, userAccount)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "账号格式错误");
        }
        //密码不能包含特殊字符，6-16位
        String passwordLegalPattern = "^\\w{6,16}$";
        if (!Pattern.matches(passwordLegalPattern, userPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "密码格式错误");
        }
        //2. 对密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //账号密码不匹配或用户不存在
        if (user == null) {
            log.info("user login failed, user not exist or userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "账号密码不匹配或用户不存在");
        }
        //4. 用户脱敏
        User safetyUser = getSafetyUser(user);
        //5. 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        return safetyUser;
    }

    @Override
    public User getSafetyUser(User originalUser) {
        if (originalUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户为空");
        }
        User safetyUser = new User();
        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setUserStatus(originalUser.getUserStatus());
        safetyUser.setCreateTime(originalUser.getCreateTime());
        safetyUser.setTags(originalUser.getTags());
        safetyUser.setProfile(originalUser.getProfile());
        return safetyUser;
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User updateUserInfo(UserUpdateInfoRequest updateInfoRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", updateInfoRequest.getUserAccount());
        User user = userMapper.selectOne(queryWrapper);
        if (updateInfoRequest.getUsername() != null && !"".equals(updateInfoRequest.getUsername())) {
            user.setUsername(updateInfoRequest.getUsername());
        } else {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "用户名不能为空");
        }
        if (updateInfoRequest.getAvatar() != null && !"".equals(updateInfoRequest.getAvatar())) {
            user.setAvatarUrl(updateInfoRequest.getAvatar());
        }
        user.setGender(updateInfoRequest.getGender());
        user.setPhone(updateInfoRequest.getPhone());
        user.setEmail(updateInfoRequest.getEmail());
        return getSafetyUser(user);
    }

    @Override
    public void resetPassword() {
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "123456").getBytes());
        User user = new User();
        user.setUserPassword(encryptPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        int id = userMapper.update(user, queryWrapper);
        if (id <= 0) {
            throw new BusinessException(ErrorCode.DATA_BASE_ERROR, "用户重置密码失败");
        }
    }

    @Override
    public List<User> searchUsersByTags(List<String> tagNameList, String userAccount) {
        if (tagNameList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表为空");
        }

        //1. SQL查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        for (String tag : tagNameList) {
            userQueryWrapper = userQueryWrapper.like("tags", tag);
            userQueryWrapper.last("limit 100");
        }
        List<User> userList = userMapper.selectList(userQueryWrapper);

        return userList.stream().map(this::getSafetyUser).toList();

//        //2. 内存查询
//        Gson gson = new Gson();
//        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.last("limit 500");
//        List<User> users = userMapper.selectList(userQueryWrapper);
//        //stream 可改为 parallelStream，并行流查询，但由于使用公共线程池，如果有其他复杂查询也使用会出现线程资源紧张
//        return users.stream().filter(user -> {
//            String tagsStr = user.getTags();
//            if (StringUtils.isBlank(tagsStr)) {
//                return false;
//            }
//            if (user.getUserAccount().equals(userAccount)) {
//                return false;
//            }
//            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
//            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
//            for (String tagName: tagNameList) {
//                if (!tempTagNameSet.contains(tagName)) {
//                    return false;
//                }
//            }
//            return true;
//        }).map(this::getSafetyUser).toList();
    }

    @Override
    public List<User> searchUsersByInfo(String userInfo, String userAccount) {
        if (userInfo.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索信息为空");
        }
        List<User> userList;
        String pattern = "^\\d*$";
        if (Pattern.matches(pattern, userInfo)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("user_account", pattern);
            userList = userMapper.selectList(queryWrapper);
        }
        else {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("username", userInfo).notIn("user_account", userAccount).last("limit 20");
            userList = userMapper.selectList(queryWrapper);
        }
        return userList;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null) {
            return null;
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        return user;
    }

    @Override
    public boolean updateUser(User updateUser, User loginUser, boolean isAdmin, String updateKey) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", updateUser.getUserAccount());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST, "用户不存在");
        }
        if (isAdmin || loginUser.getUserAccount().equals(user.getUserAccount())) {
            switch (updateKey) {
                case "username" -> user.setUsername(updateUser.getUsername());
                case "avatarUrl" -> user.setAvatarUrl(updateUser.getAvatarUrl());
                case "gender" -> user.setGender(updateUser.getGender());
                case "tags" -> user.setTags(TagsToJSONUtil.tagsToJSON(updateUser.getTags()));
                case "profile" -> user.setProfile(updateUser.getProfile());
                case "phone" -> user.setPhone(updateUser.getPhone());
                case "email" -> user.setEmail(updateUser.getEmail());
                default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
            }
            userMapper.updateById(user);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public IPage<User> getRecommendUsers(User user) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        String redisKey = String.format("cloudFriends:user:recommend:%s", user.getId());
        IPage<User> userPage = (IPage<User>) valueOperations.get(redisKey);

        if (userPage != null) {
            return userPage;
        }

        // 无缓存，查询并放入 redis 中
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("id", user.getId());
        userPage = this.page(new Page<>(1, 10), queryWrapper);
        try {
            valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return userPage;
    }

    @Override
    public IPage<User> matchUsers(long size, User loginUser) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        String redisKey = String.format("cloudFriends:user:match:%s", loginUser.getId());
        IPage<User> userPage = (IPage<User>) valueOperations.get(redisKey);

        if (userPage != null) {
            return userPage;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags").last("limit 100");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        Set<String> tagSet = gson.fromJson(tags, new TypeToken<Set<String>>(){}.getType());
        List<String> tagList = tagSet.stream().toList();
        // 用户列表的下表 => 相似度
        SortedSet<UserId_Distance> userId_distances = new TreeSet<>(Comparator.comparingLong(UserId_Distance::getDistance).thenComparingLong(UserId_Distance::getUserId));
        for (User user : userList) {
            String userTags = user.getTags();
            Long userId = user.getId();
            if (loginUser.getId().equals(userId) || StringUtils.isBlank(userTags)) {
                continue;
            }
            Set<String> userTagSet = gson.fromJson(userTags, new TypeToken<Set<String>>() {
            }.getType());
            long distance = AlgorithmUtils.minDistance(tagList, userTagSet.stream().toList());
            userId_distances.add(new UserId_Distance(userId, distance));
        }
        List<Long> matchUserId = userId_distances.stream().map(UserId_Distance::getUserId).limit(size).collect(Collectors.toList());
        HashMap<Long, Integer> userIdIndex = new HashMap<>();
        for (int i = 0; i < matchUserId.size(); i++) {
            userIdIndex.put(matchUserId.get(i), i);
        }
        QueryWrapper<User> matchUserQueryWrapper = new QueryWrapper<>();
        matchUserQueryWrapper.in("id", matchUserId);
        User[] users = new User[(int) size];
        this.list(matchUserQueryWrapper).forEach(user -> users[userIdIndex.get(user.getId())] = this.getSafetyUser(user));
        List<User> usersRes = new ArrayList<>();
        Collections.addAll(usersRes, users);

        // 无缓存，查询并放入 redis 中
        userPage = new Page<User>(1, size).setRecords(usersRes);
        try {
            valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return userPage;
    }
}




