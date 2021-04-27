package com.x.bridge.web.page;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

/**
 * @Desc TODO
 * @Date 2021/4/26 21:17
 * @Author AD
 */
@Log4j2
@Component
public class HomePage implements CommandLineRunner {

    @Value("${server.port}")
    private String serverPort;

    static {
        // Desktop.getDesktop(); 防止报错
        System.setProperty("java.awt.headless", "false");
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

