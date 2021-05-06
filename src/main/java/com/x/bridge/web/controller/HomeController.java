package com.x.bridge.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Desc
 * @Date 2021/4/28 20:51
 * @Author AD
 */
@Controller
@RequestMapping("home")
public class HomeController {
    
    public String homePage(){
        return "login";
    }
}
