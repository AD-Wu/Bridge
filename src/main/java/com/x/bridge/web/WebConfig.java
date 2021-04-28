package com.x.bridge.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.awt.*;
import java.net.URI;

/**
 * @Desc 视图控制器（对于没有模型数据和逻辑处理的HTTP GET请求，可以使用视图控制器）
 * @Date 2021/4/27 08:41
 * @Author AD
 */
@Log4j2
@Component
public class WebConfig implements WebMvcConfigurer, CommandLineRunner {


    static {
        // Desktop.getDesktop(); 防止报错
        System.setProperty("java.awt.headless", "false");
    }


    @Value("${server.port}")
    private String serverPort;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 当请求"/"时转发到home视图上（可以用于代替LoginController）
        registry.addViewController("/").setViewName("login/login");
        //registry.addViewController("/welcome.html").setViewName("proxy/text");
        // registry.addViewController("/login");
    }

    @Override
    public void run(String... args) throws Exception{
        String url = "http://localhost:"+serverPort;
        try {
            // 创建一个URI实例
            URI uri = URI.create(url);
            // 获取当前系统桌面扩展
            Desktop desktop = Desktop.getDesktop();
            // 判断系统桌面是否支持要执行的功能
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                // 获取系统默认浏览器打开链接
                desktop.browse(uri);
                log.info("打开路径:{}", url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
