package com.example.demo;

import org.springframework.boot.SpringApplication; 
// SpringApplication类负责引导应用程序启动，通过调用其run()方法初始化Spring应用上下文、加载配置、启动嵌入式服务器（如Tomcat）等
import org.springframework.boot.autoconfigure.SpringBootApplication; 
// 这是一个组合注解，包含三个核心功能：
// **@SpringBootConfiguration**​：标记当前类为配置类，允许定义@Bean方法并将其实例纳入Spring容器。
// **@EnableAutoConfiguration**​：启用自动配置机制，根据项目依赖自动装配组件（如数据库连接池、Web框架等），减少手动配置。
// **@ComponentScan**​：扫描当前包及其子包下被@Component、@Service等注解标记的类，并注册为Spring组件。
// 整体作用​：这两行代码共同定义了Spring Boot应用的入口和基础配置。@SpringBootApplication注解简化了传统Spring项目的配置流程，通过自动配置和组件扫描快速搭建应用框架，而SpringApplication.run()则是启动整个应用生命周期的起点。

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
