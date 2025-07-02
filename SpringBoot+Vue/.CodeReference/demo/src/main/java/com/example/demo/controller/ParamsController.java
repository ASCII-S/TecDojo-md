package com.example.demo.controller;

import com.example.demo.entity.User;
// User 是一个实体类（Entity Class），通常用于映射数据库中的表结构。在Java Web开发中，实体类封装了业务数据，并通过注解（如JPA的@Entity、@Table等）与数据库表关联

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

@RestController
// @RestController 是 Spring 框架提供的一个注解，用于标识一个类是一个 RESTful 控制器。
// 它将 @Controller 和 @ResponseBody 注解合并在一起，使得一个控制器类可以同时处理 HTTP 请求和返回 JSON 或 XML 数据。
public class ParamsController {
    @RequestMapping(value = "/getTest1", method = RequestMethod.GET)
    /** 上面一行代码是Spring MVC 中用于映射 HTTP 请求的核心注解，其作用是将特定的 URL 路径与控制器方法绑定，并定义请求的处理方式
    * ​  **value = "/getTest1" 的含义**​ URL 路径​：指定请求的路径为 /getTest1，客户端需通过该路径访问对应的控制器方法
    *   **method = RequestMethod.GET 的限制**​ 请求方式​：仅允许通过 HTTP GET 方法发起请求。若客户端使用 POST、PUT 等其他方式，会返回 405 Method Not Allowed
    */
    public String getTest1() {
        return "Get Test1";
    }

    @RequestMapping(value = "/getTest2", method = RequestMethod.GET)
    public String getTest2(@RequestParam String name, @RequestParam int age) {
        /**
         * 上面一行代码是Spring MVC中控制器方法的参数定义，用于接收HTTP请求中的查询参数。具体解释如下：

            ​​@RequestParam注解​
            用于将请求参数绑定到方法参数上。Spring会自动将请求中与注解属性匹配的参数值注入到对应方法参数中            。
            ​参数定义​
            @RequestParam String name：绑定请求中名为name的参数值到方法参数name。若请求未传递该参数且未设置required=false，会抛出异常。
            @RequestParam int age：绑定请求中名为age的参数值到方法参数age。由于未显式设置required属性，默认值为true，即age为必传参数            。
            ​类型转换​
            Spring会自动将请求参数的字符串值转换为方法参数的类型（如String到int），若转换失败会抛出异常
         * 
         * 参数传递方法:
         * 通过URL配合查询参数传递参数，例如：/api/foos?name=John&age=30
         */
        System.out.println("name: " + name + ", age: " + age);
        return "Get Test2, name: " + name + ", age: " + age;
    }

    @RequestMapping(value = "/getTest3", method = RequestMethod.GET)
    public String getTest3(@RequestParam(value = "name", defaultValue = "luren") String name) {
        /**
         * 这段代码是Spring MVC框架中控制器方法的参数定义，用于处理HTTP请求中的查询参数。具体解释如下：

            ​​@RequestParam注解​
            用于将HTTP请求中的查询参数（如?name=xxx）绑定到方法参数上。通过value属性指定参数名称（这里是"name"），表示请求中需传递名为name的参数。
            ​defaultValue属性​
            当请求中未包含name参数时，自动使用默认值"luren"。例如，若请求为/test3（无name参数），则name的值为"luren"；若请求为/test3?name=Alice，则name的值为"Alice"。
         */
        System.out.println("name: " + name);
        return "Get Test3, name: " + name;
    }

    @RequestMapping(value = "/postTest1", method = RequestMethod.POST)
    // 对于Post请求,可以使用postman,apipost等工具进行测试
    public String postTest1() {
        return "Post Test1";
    }

    @RequestMapping(value = "/postTest2", method = RequestMethod.POST)
    public String postTest2(String username, String password) {
        System.out.println("username: " + username + ", password: " + password);
        return "Post Test2, username: " + username + ", password: " + password;
    }
    
    @RequestMapping(value = "/postTest3", method = RequestMethod.POST)
    public String postTest3(User user) {
        /**
         * 类作为参数如何在postman中测试
         * 1. 在postman中选择POST请求
         * 2. 在URL中输入请求路径,例如:http://localhost:8080/postTest3
         * 3. 在Body中选择raw,选择JSON格式,输入JSON数据,例如:{"username":"luren","password":"123456"}
         *  i.也可以使用form-data,key为username,value为luren,key为password,value为123456
         * 4. 点击Send按钮发送请求
         * 
         * 
         */
        System.out.println("user: " + user);
        return "Post Test3, user: " + user;
    }

    @RequestMapping(value = "/postTest4", method = RequestMethod.POST)
    public String postTest4(@RequestBody User user) {
        /**
         * 如何在第三方平台调用需要传入RequestBody的接口(以postman为例):
         * 1. 在postman中创建一个post请求
         * 2. 在url中输入http://localhost:8080/postTest4
         * 3. 在请求头中选择String类参数名Content-Type,值为application/json
         * 4. 在body中选择raw,选择json格式,输入json数据
         * 5. 点击send按钮发送请求
         */
        System.out.println("user: " + user);
        return "Post Test4, user: " + user;
    }

    @GetMapping("/test/**")
    /**
     * 使用**可以匹配多级路径,例如:http://localhost:8080/test/123/456
     */
    public String getTest5() {
        return "test";
    }
    
    
}
