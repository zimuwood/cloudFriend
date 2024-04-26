package com.example.cloudFriends.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.Models.request.UserUpdateInfoRequest;
//import jakarta.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author cloudyW
* @description 针对表【user】的数据库操作Service
* @createDate 2024-03-03 16:13:54
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userName
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    User userRegister(String userName, String userAccount, String userPassword, String checkPassword, Integer gender);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originalUser
     * @return
     */
    User getSafetyUser(User originalUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param updateInfoRequest
     * @return
     */
    User updateUserInfo(UserUpdateInfoRequest updateInfoRequest);

    /**
     * 重置密码
     */
    void resetPassword();

    /**
     * 按标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList, String userAccount);

    /**
     * 按信息搜索用户
     * @param userInfo
     * @return
     */
    List<User> searchUsersByInfo(String userInfo, String userAccount);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改用户信息
     * @param updateUser
     * @param loginUser
     * @param isAdmin
     * @return
     */
    boolean updateUser(User updateUser, User loginUser, boolean isAdmin, String updateKey);

    /**
     * 推荐用户页
     * @param user
     * @return
     */
    IPage<User> getRecommendUsers (User user);

    /**
     * 算法匹配用户列表
     * @param size
     * @param loginUser
     * @return
     */
    IPage<User> matchUsers (long size, User loginUser);
}
