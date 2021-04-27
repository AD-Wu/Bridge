package com.x.bridge.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Desc 视图控制器（对于没有模型数据和逻辑处理的HTTP GET请求，可以使用视图控制器）
 * @Date 2021/4/27 08:41
 * @Author AD
 */
@Component
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 当请求"/"时转发到home视图上（可以用于代替LoginController）
        registry.addViewController("/").setViewName("login");
        // registry.addViewController("/login");
    }
}
