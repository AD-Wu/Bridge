package com.x.bridge.web.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Desc
 * @Date 2021/4/26 22:11
 * @Author AD
 */
// @Controller
// @RequestMapping("login")
public class LoginController {
    
    @GetMapping
    public String loginPage(){
        return "login";
    }
}
