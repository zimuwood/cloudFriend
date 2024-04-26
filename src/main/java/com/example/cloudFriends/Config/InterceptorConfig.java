package com.example.cloudFriends.Config;

import com.example.cloudFriends.Interceptor.GlobalInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author CloudyW
 * @version 1.0
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration registration = registry.addInterceptor(new GlobalInterceptor());
//        registration.addPathPatterns("/**");
//        registration.excludePathPatterns("/api/login", "/api/logout");
    }
}
