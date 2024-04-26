package com.example.cloudFriends.Interceptor;

import com.example.cloudFriends.Constant.ErrorCode;
import com.example.cloudFriends.Models.pojo.User;
import com.example.cloudFriends.exception.BusinessException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.cloudFriends.Constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author CloudyW
 * @version 1.0
 */
public class GlobalInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户未登录");
        }
        return true;
    }
}
