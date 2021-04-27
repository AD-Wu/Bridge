package com.x.bridge.web;

import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @Desc
 * @Date 2020/10/26 23:43
 * @Author AD
 */
@Controller
@RequestMapping("login")
public class HelloController {
    
    
    @GetMapping("hello")
    public @ResponseBody  Mono<A> hello(){
        A ad = new A("AD", 1, LocalDateTime.now());
        return Mono.just(ad);
    }
    
    @Data
    private class A {
        private String name;
        private int age;
        private LocalDateTime birthday;
    
        public A(String name, int age, LocalDateTime birthday) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }
    
    }
}
