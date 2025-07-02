package com.example.demo.controller; // 定义包名，表示该类属于 com.example.demo.controller 包。使用点（.）连接表示包的层级结构，类似于文件夹的层级关系。

import org.springframework.web.bind.annotation.RestController; // 导入 RestController 注解，表示该类是一个 RESTful 控制器。该注解使得该类能够处理 HTTP 请求并返回 JSON 或其他格式的数据。点（.）连接表示从 org.springframework.web 包中导入 RestController 类。
// org表示组织,springframework表示框架,web表示web应用,bind表示绑定,annotation表示注解,RestController表示RESTful控制器
    // RESTful控制器是一种设计风格，用于创建 RESTful API。它使用 HTTP 方法（如 GET、POST、PUT、DELETE）来操作资源，并使用 URL 来标识资源。
        // HTTP方法是HTTP协议中定义的请求方法，用于操作资源。
            // HTTP协议是用于传输和交换超文本的互联网应用层协议。
            // HTTP方法包括GET、POST、PUT、DELETE等，用于描述对资源的操作。
        // URL是统一资源定位符，用于标识资源。
            // URL是用于标识互联网上资源的字符串，通常包括协议、主机名、端口号和路径等信息。
import org.springframework.web.bind.annotation.GetMapping; // 导入 GetMapping 注解，用于处理 HTTP GET 请求。
// GetMapping注解是Spring框架提供的一个注解，用于处理HTTP GET请求。
    // @GetMapping与@RestController的区别
        // @GetMapping是Spring框架提供的一个注解，用于处理HTTP GET请求。
        // @RestController是Spring框架提供的一个注解，用于标识一个类是一个RESTful控制器。
        // @GetMapping与@RestController的区别在于，@GetMapping用于处理HTTP GET请求，而@RestController用于标识一个类是一个RESTful控制器。
import org.springframework.web.bind.annotation.RequestMapping; // 导入 RequestMapping 注解，用于处理 HTTP 请求。
import org.springframework.web.bind.annotation.RequestMethod; // 导入 RequestMethod 注解，用于处理 HTTP 请求。
import org.springframework.web.bind.annotation.RequestParam; // 导入 RequestParam 注解，用于处理 HTTP 请求。

@RestController // 声明该类为 RESTful 控制器，能够处理 HTTP 请求并返回数据
// 如何理解@RestController注解
    // @RestController注解是Spring框架提供的一个注解，用于标识一个类是一个RESTful控制器。
    // 它将@Controller和@ResponseBody两个注解组合在一起，使得该类可以处理HTTP请求并返回JSON或其他格式的数据。
public class HelloController {

    // @GetMapping("/hello") 注解用于将 HTTP GET 请求映射到 hello() 方法上。
    // 当用户访问 "/hello" 这个 URL 时，Spring 框架会调用 hello() 方法并返回相应的结果。
        // 如何使用/hello这个URL
            // 在浏览器中输入http://localhost:8080/hello，即可访问到hello()方法
                // 为什么这里访问hello需要连接到http://localhost:8080/后面
                    // 因为我们在application.properties文件中配置了spring.application.name=demo，所以这里的URL会连接到http://localhost:8080/后面
    // 这个注解的作用是定义一个 RESTful API 的端点，使得客户端可以通过 GET 请求获取数据。等价于@RequestMapping(value = "/hello", method = RequestMethod.GET)
    // @GetMapping("/hello")
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    // 对方法添加nickname参数传递,前端通过http://localhost:8080/hello?nickname=zhangsan访问
    public String hello(@RequestParam(value = "nickname", defaultValue = "luren") String nickname) {
        return "Hello, " + nickname + "!！！！";
    }
}
