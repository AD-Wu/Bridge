package com.x.bridge.web.page;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Desc TODO
 * @Date 2021/4/26 21:17
 * @Author AD
 */
@Log4j2
@Component
public class HomePage implements CommandLineRunner {
    
    @Value("${start.web.url}")
    private String openUrl;
    @Value("${start.open}")
    private boolean open;
    @Value("${start.cmd}")
    private String cmd;
    @Value("${start.web.browser}")
    private String browser;
    
    @Override
    public void run(String... args) throws Exception {
        if (open) {
            String runCmd = cmd + " " + browser + " " + openUrl;
            log.info("运行的命令:{}", runCmd);
            try {
                Runtime.getRuntime().exec(runCmd);
                log.debug("启动浏览器打开项目成功");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("启动项目自动打开浏览器失败:{}", e.getMessage());
            }
        }
    }
    
}

