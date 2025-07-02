---
jupytext:
  formats: md:markdown,ipynb/SpringBoot+Vue/03-SpringController.ipynb:notebook
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

# Spring Controller 学习笔记

---
## Web入门：为什么需要Controller

在Web应用程序开发中，Controller（控制器）是MVC（Model-View-Controller）架构中的核心组件，负责处理用户请求并返回响应。

### Controller的核心作用

1. **请求处理**：接收并处理来自客户端的HTTP请求（GET、POST、PUT、DELETE等）
2. **业务逻辑协调**：调用相应的服务层（Service）进行业务处理
3. **数据传递**：从客户端获取数据，传递给服务层；从服务层获取结果，返回给客户端
4. **视图选择**：在传统MVC模式中，选择合适的视图进行渲染（在RESTful API中直接返回数据）

### Controller在Web架构中的位置

```
客户端(浏览器/移动应用) → HTTP请求 → Controller → Service → Repository → 数据库
                                     ↑                            ↓
                                     └───────── 响应 ◄────────────┘
```

### 不使用Controller会怎样？

如果没有Controller：
- 请求处理逻辑会分散在应用的各个部分
- 难以实现统一的请求验证和响应格式
- 代码结构混乱，业务逻辑与请求处理逻辑耦合
- 难以实现安全控制和权限管理

---
## 路由映射：Controller做了什么？

在Spring Boot中，Controller通过特定的注解将HTTP请求映射到特定的处理方法上，这个过程称为路由映射。

### Controller的主要注解

1. **@Controller**：标识一个类为Spring MVC控制器
2. **@RestController**：表示一个类为@Controller + @ResponseBody的组合，用于构建RESTful API
   ```java
   @RestController
   public class ParamsController {
       // 控制器方法...
   }
   ```

3. **@RequestMapping**：将HTTP请求映射到控制器方法
   ```java
   @RequestMapping(value = "/getTest1", method = RequestMethod.GET)
   public String getTest1() {
       return "Get Test1";
   }
   ```
   
4. **HTTP方法特定注解**：
   - **@GetMapping**：处理GET请求的简化注解
   - **@PostMapping**：处理POST请求的简化注解
   - **@PutMapping**：处理PUT请求的简化注解
   - **@DeleteMapping**：处理DELETE请求的简化注解
   - **@PatchMapping**：处理PATCH请求的简化注解

### 路径匹配规则

Spring Boot支持多种路径匹配规则：

1. **精确匹配**：完全匹配指定路径
   ```java
   @GetMapping("/users")  // 只匹配 /users
   ```

2. **路径变量**：使用`{}`定义路径变量，通过@PathVariable获取
   ```java
   @GetMapping("/users/{id}")  // 匹配 /users/1, /users/abc 等
   public User getUser(@PathVariable String id) { ... }
   ```

3. **通配符匹配**：使用`?`匹配单个字符，`*`匹配多个字符
   ```java
   @GetMapping("/files/*.jpg")  // 匹配所有.jpg结尾的文件
   ```

4. **双通配符**：使用`**`匹配多级路径
   ```java
   @GetMapping("/test/**")
   public String getTest5() {
       return "通配符测试";  // 匹配 /test/a, /test/a/b 等
   }
   ```

### 请求映射的属性

RequestMapping注解可以设置多种属性：

1. **value/path**：指定请求的路径
2. **method**：指定请求方法（GET、POST等）
3. **params**：指定请求必须包含的参数
4. **headers**：指定请求必须包含的头信息
5. **consumes**：指定请求的内容类型（Content-Type）
6. **produces**：指定响应的内容类型

```
@RequestMapping(
    value = "/users", 
    method = RequestMethod.POST,
    consumes = "application/json",
    produces = "application/json"
)
```

---
## 参数传递：如何向Controller传递参数

Spring Boot提供了多种方式将客户端数据传递给Controller方法。

### 1. 查询参数（Query Parameters）

使用`@RequestParam`注解获取URL中的查询参数。

```
// GET /getTest2?name=John&age=25
@RequestMapping(value = "/getTest2", method = RequestMethod.GET)
public String getTest2(@RequestParam String name, @RequestParam int age) {
    return "Get Test2, name: " + name + ", age: " + age;
}
```

**@RequestParam的主要属性**：
- **value/name**：参数名称
- **required**：是否必须（默认为true）
- **defaultValue**：默认值（当参数不存在时）

```
// GET /getTest3?name=John 或 GET /getTest3
@RequestMapping(value = "/getTest3", method = RequestMethod.GET)
public String getTest3(@RequestParam(value = "name", defaultValue = "luren") String name) {
    return "Get Test3, name: " + name;
}
```

### 2. 路径变量（Path Variables）

使用`@PathVariable`注解获取URL路径中的变量。

```
// GET /users/123
@GetMapping("/users/{id}")
public User getUser(@PathVariable("id") String userId) {
    // 根据userId查询用户
    return userService.findById(userId);
}
```

**多路径变量**：

```
// GET /users/123/orders/456
@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(@PathVariable String userId, @PathVariable String orderId) {
    return orderService.findByUserAndOrder(userId, orderId);
}
```

### 3. 表单数据（Form Data）

对于POST请求，可以直接使用方法参数或对象接收表单数据。

```
// POST /postTest2 with form data: username=John&password=123456
@RequestMapping(value = "/postTest2", method = RequestMethod.POST)
public String postTest2(String username, String password) {
    return "Post Test2, username: " + username + ", password: " + password;
}
```

### 4. 请求体（Request Body）

使用`@RequestBody`注解接收JSON或XML格式的请求体，并自动转换为Java对象。

```
// POST /postTest4 with JSON body: {"username":"John","password":"123456"}
@RequestMapping(value = "/postTest4", method = RequestMethod.POST)
public String postTest4(@RequestBody User user) {
    return "Post Test4, user: " + user;
}
```

### 5. 对象绑定

Spring Boot可以自动将请求参数映射到Java对象的属性上。

```
// POST /postTest3 with form data: username=John&password=123456
@RequestMapping(value = "/postTest3", method = RequestMethod.POST)
public String postTest3(User user) {
    return "Post Test3, user: " + user;
}
```

注意：
- 对于GET请求，参数来自URL查询字符串
- 对于POST请求，参数可以来自表单数据或URL查询字符串
- 对象属性名需要与请求参数名一致
- Spring Boot会自动进行类型转换

### 6. 请求头和Cookie

**请求头**
- ​本质​：HTTP 请求的“附加信息”，以 Key: Value 形式发送（如 User-Agent: Chrome）。
- ​作用​：告诉服务器客户端的配置、需求或身份（如浏览器类型、支持的数据格式、登录凭证等）。

**Cookie**
- ​本质​：Cookie​ 是服务器发送到用户浏览器并保存在本地的一小段数据（通常不超过 4KB）
- ​作用​：在多个请求之间 ​保持状态​（如用户登录、个性化设置等）

```
// 获取请求头
@GetMapping("/header")
public String getHeader(@RequestHeader("User-Agent") String userAgent) {
    return "User-Agent: " + userAgent;
}

// 获取Cookie
@GetMapping("/cookie")
public String getCookie(@CookieValue("JSESSIONID") String sessionId) {
    return "Session ID: " + sessionId;
}
```

### 7. 混合参数

可以在一个方法中组合使用多种参数注解。

```
@PostMapping("/users/{id}")
public User updateUser(
    @PathVariable String id,
    @RequestBody User user,
    @RequestHeader("Authorization") String auth,
    @RequestParam(required = false) Boolean active
) {
    // 处理请求...
}
```

---
## 数据响应：如何响应Controller传回的数据

Spring Boot提供了多种方式从Controller返回数据给客户端。

### 1. 返回字符串

在@RestController中，直接返回字符串会作为响应体发送给客户端。

```
@GetMapping("/hello")
public String hello() {
    return "Hello, World!";  // 直接返回字符串
}
```

### 2. 返回对象（自动转换为JSON）

Spring Boot会自动将返回的对象转换为JSON格式。

```
@GetMapping("/users/{id}")
public User getUser(@PathVariable String id) {
    User user = new User("John", "password");
    return user;  // 自动转换为JSON: {"username":"John","password":"password"}
}
```

### 3. 返回集合

```
@GetMapping("/users")
public List<User> getUsers() {
    List<User> users = new ArrayList<>();
    users.add(new User("John", "password1"));
    users.add(new User("Jane", "password2"));
    return users;  // 自动转换为JSON数组
}
```

### 4. 返回ResponseEntity

**ResponseEntity** 是 Spring Framework 提供的一个类，用于封装 HTTP 响应，可以灵活控制：
- 响应体数据（如 JSON、字符串、文件）
- 状态码（如 200、404、500）
- 响应头（如 Content-Type、自定义头）

使用ResponseEntity可以更精确地控制HTTP响应，包括状态码、响应头和响应体。

```
@GetMapping("/users/{id}")
public ResponseEntity<User> getUserWithStatus(@PathVariable String id) {
    User user = userService.findById(id);
    if (user != null) {
        return new ResponseEntity<>(user, HttpStatus.OK);  // 200 OK
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 404 Not Found
    }
}
```

设置自定义响应头：

```
@GetMapping("/download")
public ResponseEntity<byte[]> download() {
    byte[] data = // 获取文件数据
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDispositionFormData("filename", "example.pdf");
    return new ResponseEntity<>(data, headers, HttpStatus.OK);
}
```

### 5. 异步响应

对于长时间运行的操作，可以使用异步响应。

```
@GetMapping("/async")
public CompletableFuture<String> asyncResponse() {
    return CompletableFuture.supplyAsync(() -> {
        // 模拟长时间操作
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Async response after 5 seconds";
    });
}
```

### 6. 流式响应

对于大数据或实时数据，可以使用流式响应。

```
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamData() {
    return Flux.interval(Duration.ofSeconds(1))
               .map(sequence -> "Data " + sequence)
               .take(10);
}
```

### 7. 文件下载

```
@GetMapping("/file")
public ResponseEntity<Resource> downloadFile() {
    Path path = Paths.get("files/example.pdf");
    Resource resource = new UrlResource(path.toUri());
    
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"example.pdf\"")
            .body(resource);
}
```

### 8. 自定义响应格式

可以创建统一的响应格式，包含状态码、消息和数据。

```
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    
    // 构造函数、getter、setter等
}

@GetMapping("/users/{id}")
public ApiResponse<User> getUserWithFormat(@PathVariable String id) {
    User user = userService.findById(id);
    if (user != null) {
        return new ApiResponse<>(200, "Success", user);
    } else {
        return new ApiResponse<>(404, "User not found", null);
    }
}
```

## 实战示例

### 完整的RESTful API示例

```
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    
    // 构造函数注入UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 获取所有用户
    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    
    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 创建用户
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdUser);
    }
    
    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean deleted = userService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

### 错误处理

使用@ControllerAdvice或@RestControllerAdvice进行全局异常处理：

```
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

## 总结

Spring Controller是Web应用程序的核心组件，负责处理HTTP请求并返回响应。通过使用各种注解和参数类型，可以灵活地接收客户端数据并返回不同格式的响应。

主要内容回顾：
1. **Controller的作用**：处理请求、协调业务逻辑、选择视图
2. **路由映射**：通过@RequestMapping等注解将HTTP请求映射到控制器方法
3. **参数传递**：@RequestParam、@PathVariable、@RequestBody等方式接收客户端数据
4. **数据响应**：返回对象、ResponseEntity、异步响应等多种方式响应客户端

通过掌握这些知识，可以构建功能丰富、易于维护的Web应用程序。
