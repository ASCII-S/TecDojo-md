---
jupytext:
  formats: "md:markdown,ipynb/SpringBoot+Vue/05-RESTful\u670D\u52A1+Swagger.ipynb:notebook"
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

# RESTful介绍

REST (Representational State Transfer) 是一种软件架构风格，用于设计网络应用程序。RESTful API 是遵循 REST 架构风格的 Web 服务接口。

## 主要特点

1. **资源导向** - 一切都被视为资源，每个资源由 URI 唯一标识
2. **统一接口** - 使用标准 HTTP 方法操作资源：
   - GET：获取资源
   - POST：创建资源
   - PUT：更新资源
   - DELETE：删除资源
3. **无状态** - 服务器不保存客户端状态，每个请求包含所有必要信息
4. **可缓存** - 响应可被缓存以提高性能
5. **分层系统** - 客户端无需知道是否直接连接到服务器

## RESTful API 设计原则

1. **使用名词而非动词表示资源**：如 `/users` 而非 `/getUsers`
2. **使用复数名词**：如 `/users` 而非 `/user`
3. **使用 HTTP 方法表达操作**
4. **使用嵌套结构表示资源关系**：如 `/users/123/orders`
5. **使用 HTTP 状态码表示操作结果**

# 构建RESTful应用接口

Spring Boot 提供了强大的支持来构建 RESTful API。以下是在 Spring Boot 中构建 RESTful 接口的关键组件和示例。

## 常用注解

- `@RestController` - 表示该类是 REST 控制器，会自动处理 JSON/XML 转换
- `@RequestMapping` - 指定请求的 URL 路径
- `@GetMapping` - 处理 GET 请求（获取资源）
- `@PostMapping` - 处理 POST 请求（创建资源）
- `@PutMapping` - 处理 PUT 请求（更新资源）
- `@DeleteMapping` - 处理 DELETE 请求（删除资源）
- `@PathVariable` - 获取 URL 中的参数
- `@RequestBody` - 获取请求体中的数据

## 示例代码

```
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
```

## 关键注解说明

1. **@PathVariable**：将 URL 中的占位符（如 `{id}`）绑定到方法参数
   ```java
   @GetMapping("/user/{id}")
   public String getUser(@PathVariable String id) { ... }
   ```

2. **@RequestBody**：将 HTTP 请求体中的 JSON 数据转换为 Java 对象
   ```java
   @PostMapping("/user")
   public String createUser(@RequestBody User user) { ... }
   ```

## RESTful API 测试

可以使用以下工具测试 RESTful API：
- Postman
- curl
- Swagger UI（下一部分介绍）

# 使用Swagger构建Web API文档

Swagger（现在是 OpenAPI 规范的一部分）是一个强大的 API 文档工具，可以自动为 RESTful Web 服务生成交互式文档。

## Swagger 优势

1. **自动生成文档** - 根据代码自动创建 API 文档
2. **交互式 UI** - 提供可视化的 API 浏览和测试界面
3. **标准化** - 遵循 OpenAPI 规范
4. **易于维护** - 文档与代码同步更新

## 在 Spring Boot 项目中集成 Swagger

### 1. 添加依赖

在 `pom.xml` 文件中添加 Swagger 相关依赖：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### 2. 配置 Swagger

创建 Swagger 配置类：

```
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API文档").version("1.0"));
    }
}
```

### 3. 配置应用属性

在 `application.properties` 中添加 Swagger 相关配置：

```properties
# 配置 Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.operationsSorter=alpha
```

### 4. 访问 Swagger UI

启动应用后，可以通过以下 URL 访问 Swagger UI：
- http://localhost:8080/swagger-ui.html

### 5. 增强 API 文档

可以使用 Swagger 注解增强 API 文档：

```
@RestController
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        // ...
    }
    
    // 其他方法...
}
```

常用的 Swagger 注解包括：
- `@Tag` - 为控制器添加标签
- `@Operation` - 描述操作/端点
- `@Parameter` - 描述参数
- `@ApiResponse` - 描述响应
- `@Schema` - 描述模型

## 实际使用

1. 通过 Swagger UI 可以：
   - 浏览所有可用的 API 端点
   - 查看每个端点的详细信息
   - 直接在界面上测试 API
   - 查看请求和响应的模型结构

2. 开发优势：
   - 减少前后端沟通成本
   - 方便 API 测试和调试
   - 作为 API 的实时文档
