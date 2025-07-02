package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
// 上面一行代码是Spring Boot提供的一个注解，用于标识一个类是一个配置类。
    // 配置类是Spring Boot中用于配置Spring MVC的类，可以替代传统的XML配置文件。
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.interceptor.LoginInterceptor;

/**
 * Web配置类
 * 
 * 这个类负责配置Spring MVC的各种组件，包括拦截器、视图解析器、消息转换器等。
 * 通过实现WebMvcConfigurer接口，我们可以自定义Spring MVC的配置，而不需要完全覆盖Spring Boot的自动配置。
 * 
 * @Configuration注解表明这是一个配置类，Spring容器会在启动时加载它。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 添加拦截器
     * 
     * 此方法用于向Spring MVC注册自定义拦截器，并配置拦截器的拦截路径和排除路径。
     * 拦截器执行顺序与注册顺序相同，先注册的先执行。
     * 
     * @param registry 拦截器注册表，用于添加和配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                // 设置拦截路径，"/**"表示拦截所有请求
                .addPathPatterns("/private/**")
                // 设置排除路径，"/login"请求不会被拦截
                // 可以添加多个排除路径，如.excludePathPatterns("/login", "/register", "/static/**")
                .excludePathPatterns("/**");
    }
    
    /**
     * 其他可配置的方法：
     * 
     * 1. addResourceHandlers：添加资源处理器，用于处理静态资源
     * 2. addCorsMappings：配置跨域请求处理
     * 3. addViewControllers：添加视图控制器
     * 4. configureMessageConverters：配置消息转换器
     * 5. configureViewResolvers：配置视图解析器
     * 等等
     */
}
