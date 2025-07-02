package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
/**
 * @RequestBody 是 Spring MVC 框架提供的一个注解，用于将 HTTP 请求体中的 JSON 数据转换为 Java 对象。
 * 请求体中的数据需要与 Java 对象的属性一一对应，否则会报错。
 */
import org.springframework.web.bind.annotation.RequestBody;
/*
 * PathVariable 是 Spring MVC 框架提供的一个注解，用于将 URL 中的占位符（如 {id}）与方法参数绑定。
 */
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.entity.User;

@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        System.out.println("Url:/user/" + id);
        return "User ID: " + id;
    }

    @PostMapping("/user")
    public String createUser(@RequestBody User user) {
        System.out.println("User: " + user);
        return "User created";
    }

    @PutMapping("/user/{id}")
    public String updateUser(@PathVariable String id, @RequestBody User user) {
        System.out.println("User: " + user);
        String response = "User updated from " + user.getUsername() + " to " + id;
        return response;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable String id) {
        System.out.println("Url:/user/" + id);
        return "User deleted";
    }
    
}
