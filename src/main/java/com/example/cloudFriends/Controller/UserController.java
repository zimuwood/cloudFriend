package com.example.cloudFriends.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.pojo.Tag;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.request.AdminSearchRequest;
import com.example.cloudFriends.Models.request.UserLoginRequest;
import com.example.cloudFriends.Models.request.UserRegisterRequest;
import com.example.cloudFriends.Models.request.UserUpdateInfoRequest;
import com.example.cloudFriends.Service.TagService;
import com.example.cloudFriends.Service.UserService;
import com.example.cloudFriends.Models.Result;
import com.example.cloudFriends.Utils.AccountGenerator;
import com.example.cloudFriends.Utils.AliOSSUtils;
import com.example.cloudFriends.exception.BusinessException;
//import jakarta.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import static com.example.cloudFriends.Constant.UserConstant.ADMIN_ROLE;
import static com.example.cloudFriends.Constant.UserConstant.USER_LOGIN_STATE;
import static com.example.cloudFriends.Utils.UpLoadAvatarUtil.getResult;

/**
 * 用户接口
 * @author CloudyW
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = { "http://localhost:3000/", "http://localhost:80/" }, allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private TagService tagService;

    @Resource
    private AccountGenerator accountGenerator;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/register")
    public Result userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return Result.error(ErrorCode.PARAMS_ERROR);
        }
        String userName = userRegisterRequest.getUserName();
        String userAccount = accountGenerator.generateAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        Integer gender = userRegisterRequest.getGender();
        if (StringUtils.isAnyBlank(userName, userPassword, checkPassword)) {
            return Result.error(ErrorCode.USER_INFO_ERROR);
        }
        User result = userService.userRegister(userName, userAccount, userPassword, checkPassword, gender);
        return Result.success(result, "用户插入成功，id:" + result);
    }

    @PostMapping("/login")
    public Result userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录信息为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.USER_INFO_ERROR, "账号或密码为空");
        }
        return Result.success(userService.userLogin(userAccount, userPassword, request), userAccount + "登录成功");
    }

    @PostMapping("/logout")
    public Result userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");
        }
        Boolean logout = userService.userLogout(request);
        if (!logout) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败");
        }
        return Result.success("退出登录成功");
    }

    @GetMapping("/current")
    public Result getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户未登录");
        }
        User currentUser = userService.getById(user.getId());
        if (currentUser.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        return Result.success(userService.getSafetyUser(currentUser));
    }

    @PostMapping("/search")
    public Result searchUsers(@RequestBody AdminSearchRequest adminSearchRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(adminSearchRequest != null) {
            String username = adminSearchRequest.getUserName();
            String userAccount = adminSearchRequest.getUserAccount();
            Integer gender = adminSearchRequest.getGender();
            String phone = adminSearchRequest.getPhone();
            String email = adminSearchRequest.getEmail();
            if (username != null) {
                queryWrapper.like("username", username);
            }
            if (userAccount != null) {
                queryWrapper.like("user_account", userAccount);
            }
            if (gender != null) {
                queryWrapper.eq("gender", gender);
            }
            if (phone != null) {
                queryWrapper.like("phone", phone);
            }
            if (email != null) {
                queryWrapper.like("email", email);
            }
        }
        List<User> userList = userService.list(queryWrapper);
        return Result.success(userList.stream().map(user -> userService.getSafetyUser(user)).toList());
    }

    @PostMapping("/getAvatar")
    public Result uploadAvatar(@RequestParam("image") MultipartFile avatar) {
        return getResult(avatar, aliOSSUtils, log);
    }

    @PostMapping("/updateInfo")
    public Result updateUserInfo(@RequestBody UserUpdateInfoRequest updateInfoRequest) {
        if (updateInfoRequest == null) {
            return Result.error(ErrorCode.NULL_ERROR, "请求发生错误");
        }
        User user = userService.updateUserInfo(updateInfoRequest);
        return Result.success(user);
    }

    @PostMapping("/delete")
    public Result deleteUser(long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            return Result.error(ErrorCode.USER_NOT_EXIST);
        }
        return userService.removeById(id) ? Result.success() : Result.error(ErrorCode.USER_NOT_EXIST);
    }

    @GetMapping("/search/tags")
    public Result searchUserByTags(@RequestParam List<String> tagNameList, @RequestParam String userAccount){
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagNameList, userAccount);
        return Result.success(users);
    }

    @GetMapping("/search/info")
    public Result searchUserByInfo(@RequestParam String userInfo, @RequestParam String userAccount){
        if (StringUtils.isBlank(userInfo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUsersByInfo(userInfo, userAccount);
        return Result.success(users);
    }

    @GetMapping("/get")
    public Result getUserById(@RequestParam long userId) {
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        User safetyUser = userService.getSafetyUser(userService.getById(userId));
        if (safetyUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未查询到该用户信息");
        }
        return Result.success(safetyUser);
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user, @RequestParam String updateKey, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        boolean updateFlag = userService.updateUser(user, userService.getLoginUser(request), isAdmin(request), updateKey);
        return updateFlag ? Result.success("信息修改成功") : Result.error(ErrorCode.SYSTEM_ERROR, "信息修改失败");
    }

    @GetMapping("/recommend")
    public Result recommendUsers(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录信息为空");
        }
        User loginUser = userService.getLoginUser(request);
        return Result.success(userService.getRecommendUsers(loginUser));
    }

    @GetMapping("/match")
    public Result matchUsers(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录信息为空");
        }
        User loginUser = userService.getLoginUser(request);
        return Result.success(userService.matchUsers(10, loginUser));
    }

    @GetMapping("/getTags")
    public Result getTags(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录信息为空");
        }
        User loginUser = userService.getLoginUser(request);
        return Result.success(tagService.getTags());
    }

    @PostMapping("/createTag")
    public Result createTag(@RequestBody Tag tag, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录信息为空");
        }
        if (tag == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "标签信息为空");
        }
        User loginUser = userService.getLoginUser(request);
        Boolean create = tagService.createTag(tag, loginUser);
        if (!create) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加标签失败");
        }
        return Result.success("添加标签成功");
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
