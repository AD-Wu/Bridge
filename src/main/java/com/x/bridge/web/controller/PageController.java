package com.x.bridge.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Desc
 * @Date 2021/5/13 21:45
 * @Author AD
 */
@Controller
@RequestMapping("page")
public class PageController {
    
    @RequestMapping("text")
    public String test(){
        return "proxy/text";
    }
    
    @RequestMapping("eleHome")
    public String eleHome(){
        return "eleHome";
    }
}
