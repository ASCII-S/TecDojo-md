package com.example.demo.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 
 * 拦截器是Spring MVC提供的一种机制，用于在请求处理前后执行一些操作。
 * 拦截器可以拦截Controller方法的执行，但不能拦截静态资源。
 * 
 * 拦截器的工作流程：
 * 1. preHandle：在Controller处理请求前被调用，返回true则继续处理，返回false则中断请求
 * 2. postHandle：在Controller处理请求后，视图渲染前被调用
 * 3. afterCompletion：在整个请求完成后被调用，常用于资源清理
 * 
 * 登录拦截器的主要职责是检查用户是否已登录，未登录则重定向到登录页面。
 */
public class LoginInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行拦截
     * 
     * @param request HTTP请求对象，包含了请求的所有信息
     * @param response HTTP响应对象，可用于重定向或发送错误信息
     * @param handler 请求对应的处理器，通常是Controller中的方法
     * @return 返回true表示继续执行后续的拦截器和Controller，返回false表示中断执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查session中是否存在user属性，不存在则表示用户未登录
            // session是HttpSession对象，是Servlet规范中的一个接口，用于在服务器端存储用户会话信息。
                // 如何理解HttpSession
                    // HttpSession是Servlet规范中的一个接口，用于在服务器端存储用户会话信息。
                        // servlet规范是JavaEE规范之一，用于定义Servlet容器的行为和接口。
                            // servlet容器是JavaEE规范中的一个组件，用于管理Servlet的生命周期和请求处理。
                    // 它提供了一种机制，可以在多个请求之间保持用户的状态信息，例如用户的登录状态、购物车内容等。
        if(request.getSession().getAttribute("user") == null){
            // 未登录时，重定向到登录页面
            response.sendRedirect("/hello");
            System.out.println("未登录");
            return false; // 返回false中断请求处理
        }
        // 如果用户已登录，则放行   
        System.out.println("登录拦截器");
        return true;
    }
    
    /**
     * 在Controller处理完请求后，视图渲染前被调用
     * 可以在这里对ModelAndView进行修改
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器
     * @param modelAndView 模型和视图对象
     */
    /*
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里修改ModelAndView
        // 例如添加通用数据等
    }
    */
    
    /**
     * 在整个请求完成后调用，视图已经渲染完成
     * 主要用于资源清理和异常处理
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器
     * @param ex 处理过程中抛出的异常，如果没有异常则为null
     */
    /*
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 可以在这里进行资源清理
        // 或者记录异常信息等
    }
    */
}
