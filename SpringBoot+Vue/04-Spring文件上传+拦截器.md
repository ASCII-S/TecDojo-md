---
jupytext:
  formats: "md:markdown,ipynb/SpringBoot+Vue/04-Spring\u6587\u4EF6\u4E0A\u4F20+\u62E6\
    \u622A\u5668.ipynb:notebook"
  text_representation:
    extension: .md
    format_name: markdown
    format_version: '1.3'
    jupytext_version: 1.13.8
kernelspec:
  display_name: C++
  language: C++
  name: xcpp
language_info:
  codemirror_mode: text/x-c++src
  file_extension: .cpp
  mimetype: text/x-c++src
  name: c++
  version: '11'
---

# Spring Boot文件上传与拦截器

## 1. 如何访问静态资源

Spring Boot默认提供了多个静态资源目录：

- `classpath:/static/`
- `classpath:/public/`
- `classpath:/resources/`
- `classpath:/META-INF/resources/`

这些目录下的文件可以直接通过URL访问，无需额外配置。例如，`classpath:/static/css/style.css`可以通过`http://localhost:8080/css/style.css`访问。

### 自定义静态资源位置

如果需要自定义静态资源位置，可以在`WebConfig`配置类中覆盖`addResourceHandlers`方法：

```
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/custom-static/");
}
```

这样配置后，`classpath:/custom-static/`目录下的资源可以通过`/static/`路径访问。

## 2. 如何实现文件上传

### 步骤1：配置文件上传

Spring Boot默认已经配置了文件上传支持，可以在`application.properties`中设置相关参数：

```properties
# 上传文件大小限制
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 步骤2：创建文件上传控制器

```
@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件类型
        String contentType = file.getContentType();
        // 获取文件大小
        long size = file.getSize();
        // 设置本地保存路径
        String savePath = request.getServletContext().getRealPath("/upload");
        // 调用方法
        saveFile(file, savePath);

        // 打印文件信息
        System.out.println("文件名：" + fileName);
        System.out.println("文件类型：" + contentType);
        System.out.println("文件大小：" + size);
        System.out.println("保存路径：" + savePath);

        return "上传成功";
    }

    // 保存文件
    public void saveFile(MultipartFile file, String savePath) throws IOException {
        // 判断文件夹是否存在
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 创建文件
        File saveFile = new File(savePath, file.getOriginalFilename());
        // 保存文件
        file.transferTo(saveFile);
    }
}
```

### 步骤3：创建前端表单

```
%%html
<!DOCTYPE html>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
    <h2>文件上传</h2>
    <form action="/upload" method="post" enctype="multipart/form-data">
        <input type="file" name="file">
        <button type="submit">上传</button>
    </form>
</body>
</html>
```

### 关键点解析

1. `@RequestParam("file") MultipartFile file`：接收上传的文件
2. `HttpServletRequest request`：获取请求信息，用于获取保存路径
3. `file.transferTo(saveFile)`：将上传的文件保存到指定位置
4. 前端表单必须设置`enctype="multipart/form-data"`属性

---
## 3. 拦截器

拦截器是Spring MVC提供的一种机制，用于在请求处理前后执行特定操作。

### 步骤1：创建拦截器

创建一个类实现`HandlerInterceptor`接口：

```
// HandlerInterceptor 是 Spring MVC 提供的一个接口，用于实现拦截器功能。
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查session中是否存在user属性，不存在则表示用户未登录
            // request.getSession() 是 HttpServletRequest 提供的一个方法，用于获取 HttpSession 对象。
                // HttpSession 是 Servlet 提供的一个接口，用于在服务器端存储用户会话信息。
                    // 会话信息是用户在服务器端存储的信息，例如用户的登录状态、购物车内容等。
        if(request.getSession().getAttribute("user") == null){
            // 未登录时，重定向到登录页面
            response.sendRedirect("/hello");
            System.out.println("未登录");
            return false; // 返回false中断请求处理
        }
        // 如果用户已登录，则放行   
        return true;
    }
}
```

> - **HttpServletRequest** 是Java Servlet API中的一个接口，它代表客户端的HTTP请求。在Web应用中，当客户端（如浏览器）向服务器发送一个HTTP请求时，服务器会创建一个 HttpServletRequest 对象来封装这个请求的相关信息，像请求的URL、请求方法（GET、POST等）、请求参数等。
> - **HttpSession** 是Servlet提供的一个接口，它用于在服务器端存储用户会话信息。通过 getSession() 方法，服务器可以获取到当前用户对应的 session 对象，进而对这个 session 对象进行操作，例如设置或获取会话属性。
> - **Servlet** 是 ​Java 平台​（Java EE / Jakarta EE）提供的一种 ​服务器端编程技术，用于处理 ​HTTP 请求​ 并生成 ​动态 Web 内容。简单来说，Servlet 是运行在 Web 服务器（如 Tomcat、Jetty）上的 Java 程序，用于接收来自客户端的请求（如浏览器），处理数据，并返回响应（如 HTML、JSON 等）。

拦截器生命周期：
1. `preHandle`：在Controller处理请求前被调用
2. `postHandle`：在Controller处理请求后，视图渲染前被调用
3. `afterCompletion`：在整个请求完成后被调用，用于资源清理

### 步骤2：注册拦截器

注册拦截器的主要目的是让Spring MVC框架能够识别并使用我们自定义的拦截器。

在配置类中实现`WebMvcConfigurer`接口，重写`addInterceptors`方法：

```
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器
            // LoginInterceptor 是自定义的拦截器类，用于实现登录拦截功能。
        registry.addInterceptor(new LoginInterceptor())
                // 设置拦截路径，"/**"表示拦截所有请求
                .addPathPatterns("/**")
                // 设置排除路径，某些请求不会被拦截
                .excludePathPatterns("/hello");
    }
}
```

### 拦截器配置说明

1. `addPathPatterns`：设置拦截路径
   - `/**`：拦截所有请求
   - `/user/**`：拦截以/user/开头的所有请求

2. `excludePathPatterns`：设置排除路径
   - `/login`：不拦截登录请求
   - `/static/**`：不拦截静态资源

3. 多个拦截器的执行顺序与注册顺序相同，先注册的先执行

### 注意事项

1. 拦截器不会拦截静态资源，如果需要拦截静态资源，需要额外配置
2. 拦截器与过滤器的区别：
   - 拦截器是Spring MVC的组件，依赖于Spring容器
   - 过滤器是Servlet的组件，不依赖于Spring容器
   - 拦截器可以访问Spring上下文，过滤器不能
