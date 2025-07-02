---
jupytext:
  formats: "md:markdown,ipynb/SpringBoot+Vue/02-SpringBoot\u5FEB\u901F\u4E0A\u624B\
    .ipynb:notebook"
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

# SpringBoot 学习笔记

## 如何初始化一个SpringBoot项目

创建SpringBoot项目有以下几种方式：

1. **使用Spring Initializr网站**：
   - 访问 [https://start.spring.io/](https://start.spring.io/)
   - 选择项目类型（Maven/Gradle）
   - 设置项目元数据（Group、Artifact等）
   - 选择Spring Boot版本
   - 添加所需依赖（如Web、JPA、Security等）
   - 点击"生成"下载项目

2. **使用IDE创建**：
   - IntelliJ IDEA: File -> New -> Project -> Spring Initializr
   - Eclipse: File -> New -> Spring Starter Project
   - VS Code: 安装Spring Boot Extension Pack后通过命令面板创建

3. **使用Spring Boot CLI**：
   ```bash
   spring init --name=demo --dependencies=web,data-jpa my-project
   ```

4. **使用Maven命令**：
   ```bash
   mvn archetype:generate -DgroupId=com.example -DartifactId=demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
   ```

## SpringBoot的代码结构

标准的SpringBoot项目结构如下：

```
demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── demo/
│   │   │               ├── DemoApplication.java (启动类)
│   │   │               ├── controller/ (控制器)
│   │   │               ├── service/ (服务层)
│   │   │               ├── repository/ (数据访问层)
│   │   │               ├── model/ (实体类)
│   │   │               └── config/ (配置类)
│   │   └── resources/
│   │       ├── static/ (静态资源)
│   │       ├── templates/ (模板文件)
│   │       └── application.properties (配置文件)
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── demo/
│                       └── DemoApplicationTests.java
└── pom.xml (Maven配置文件)
```

主要目录说明：
- **src/main/java**: 存放项目Java源代码
- **src/main/resources**: 存放配置文件和静态资源
- **src/test**: 存放测试代码
- **pom.xml**: Maven项目配置文件

## SpringBoot如何书写依赖（pom.xml）

SpringBoot使用Maven或Gradle管理依赖，在pom.xml中：

```xml
<!-- 父项目依赖，提供版本管理 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.4</version>
    <relativePath/>
</parent>

<!-- 项目基本信息 -->
<groupId>org.learn</groupId>
<artifactId>demo</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>demo</name>
<description>demo</description>

<!-- 项目依赖 -->
<dependencies>
    <!-- 核心依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Web依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- 开发工具依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    
    <!-- 其他常用依赖示例 -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- 测试依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

关键点：
- 使用`spring-boot-starter-parent`作为父项目，自动管理依赖版本
- 使用starter依赖简化配置（如`spring-boot-starter-web`包含了Web开发所需的所有依赖）
- 可以通过`<scope>`指定依赖的作用范围（compile、runtime、test等）
- 通过`<optional>`标记可选依赖

## SpringBoot需要配置什么？如何配置（application.properties）

SpringBoot采用"约定优于配置"的原则，大多数配置都有默认值，但我们可以通过以下文件自定义配置：

```properties
# 应用配置
spring.application.name=demo         # 应用名称
server.port=8080                     # 服务端口
server.servlet.context-path=/api     # 应用路径前缀

# 开发工具配置
spring.devtools.restart.enabled=true               # 启用热重启
spring.devtools.livereload.enabled=true            # 启用实时刷新

# 数据库配置示例
# spring.datasource.url=jdbc:mysql://localhost:3306/demo
# spring.datasource.username=root
# spring.datasource.password=password
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA配置示例
# spring.jpa.hibernate.ddl-auto=update
# spring.jpa.show-sql=true

# 日志配置
# logging.level.root=INFO
# logging.level.org.springframework.web=DEBUG
# logging.file.name=logs/application.log

# 静态资源配置
spring.mvc.static-path-pattern=/images/**         # 静态资源访问路径
spring.resources.static-locations=classpath:/static/  # 静态资源位置

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB       # 最大文件大小
spring.servlet.multipart.max-request-size=10MB    # 最大请求大小
```

除了properties格式，还可以使用YAML格式（application.yml）：

```yaml
spring:
  application:
    name: demo
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
      
server:
  port: 8080
```

配置加载优先级：
1. 命令行参数
2. java:comp/env中的JNDI属性
3. Java系统属性（System.getProperties()）
4. 操作系统环境变量
5. application-{profile}.properties/yml
6. application.properties/yml

## 如何使用Maven编译运行

Maven是Java项目的构建工具，可以通过以下命令来编译和运行SpringBoot项目：

### 基本Maven命令

1. **清理项目**：
   ```bash
   mvn clean
   ```

2. **编译项目**：
   ```bash
   mvn compile
   ```

3. **运行测试**：
   ```bash
   mvn test
   ```

4. **打包项目**：
   ```bash
   mvn package
   ```

5. **安装到本地仓库**：
   ```bash
   mvn install
   ```

### 运行SpringBoot应用

使用Maven插件运行：

```
mvn spring-boot:run
```

打包后运行：

```
mvn package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

构建可执行JAR包：

```
mvn clean package spring-boot:repackage
```

常用选项：
- `-Dspring.profiles.active=dev` 激活指定配置文件
- `-Dserver.port=9090` 设置服务端口

## 一个最简单的Demo如何运行（DemoApplication.java）

SpringBoot应用的入口是包含main方法的启动类，通常命名为XxxApplication：

```
%%java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

关键点解析：
1. **@SpringBootApplication**：这是一个组合注解，等价于同时使用：
   - @Configuration：标记为配置类
   - @EnableAutoConfiguration：启用自动配置
   - @ComponentScan：启用组件扫描（默认扫描当前包及子包）

2. **SpringApplication.run()**：
   - 负责启动整个Spring应用
   - 启动嵌入式Web服务器（如Tomcat）
   - 加载并配置所有bean
   - 应用各种自动配置

3. **运行流程**：
   - 启动类加载
   - 应用上下文创建
   - Bean定义加载和注册
   - 应用配置加载
   - 自动配置处理
   - 嵌入式服务器启动
   - 应用就绪
   
4. **添加控制器示例**：
   ```java
   @RestController
   public class HelloController {
       @GetMapping("/hello")
       public String hello() {
           return "Hello, Spring Boot!";
       }
   }
   ```

启动后，访问 http://localhost:8080/hello 即可看到返回的"Hello, Spring Boot!"

## Maven项目结构与POM文件详解

Maven是Java项目的构建工具，POM（Project Object Model）是Maven项目的核心配置文件。下面详细解释pom.xml中的各个部分：

### XML声明和命名空间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
```

- **XML声明**：指定XML文档的版本和编码格式
- **xmlns**：XML命名空间，定义文档中使用的元素和属性
- **xsi:schemaLocation**：指定XML Schema的位置，用于验证XML文档的结构
    - XML Schema是XML文档的结构定义，用于验证XML文档的结构是否符合规范。

### 父项目配置

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.4</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
```

- **groupId**：组织标识符，通常是组织的域名反写
  - 反写：将域名反写为组织标识符。例如，如果域名是example.com，则组织标识符为com.example。
- **artifactId**：项目标识符，通常是项目名称
- **version**：项目版本号
- **relativePath**：父项目相对路径，空值表示从中央仓库查找

**中央仓库**是Maven的默认仓库，存储所有Maven依赖的库文件。当relativePath为空时，Maven会从中央仓库查找父项目。

### 项目基本信息

```xml
<groupId>org.learn</groupId>
<artifactId>demo</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>demo</name>
<description>demo</description>
<url>http://example.com/demo</url>
```

- **groupId**：组织标识符
- **artifactId**：项目标识符
- **version**：项目版本号（SNAPSHOT表示开发版本）
- **name**：项目名称
- **description**：项目描述
- **url**：项目主页

### 许可证信息

```xml
<licenses>
    <license>
        <name>Apache 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
    </license>
</licenses>
```

指定项目的许可证信息，说明项目的使用条款。

### 开发者信息

```xml
<developers>
    <developer>
        <name>zhangsan</name>
        <email>zhangsan@example.com</email>
    </developer>
</developers>
```

列出项目的开发者和维护者信息。

### 源代码管理信息

```xml
<scm>
    <connection>scm:git:git@example.com:demo.git</connection>
    <developerConnection>scm:git:git@example.com:demo.git</developerConnection>
    <tag>v0.0.1</tag>
    <url>http://example.com/demo</url>
</scm>
```

源代码管理（SCM）配置，指定代码仓库的连接信息。

### 属性配置

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

定义项目使用的各种属性，如Java版本、编码格式等。

### 依赖管理详解

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    
    <!-- 更多依赖... -->
</dependencies>
```

- **依赖scope说明**：
  - **compile**：默认值，编译和运行时都需要
  - **provided**：编译需要，运行时由JDK或容器提供
  - **runtime**：运行时需要，编译时不需要
  - **test**：测试时需要，打包时不包含
  - **system**：编译时需要，但需要显式提供jar文件
  - **import**：导入依赖的范围，只用于dependencyManagement

- **optional**：标记可选依赖，当其他项目依赖于此项目时，不会传递此依赖

### Spring Boot Devtools

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Spring Boot Devtools提供开发时便利功能：
- **自动重启**：修改类文件时自动重启应用
- **LiveReload**：浏览器自动刷新
- **全局设置**：设置不参与重启的资源
- **远程开发**：远程调试和开发支持

配置选项包括：
- **restart.enabled**：是否启用自动重启
- **restart.exclude**：指定不需重启的文件/目录
- **livereload.enabled**：是否启用LiveReload
- **livereload.port**：LiveReload端口

### 构建配置

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <!-- ... -->
                </annotationProcessorPaths>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!-- ... -->
        </plugin>
    </plugins>
</build>
```

- **plugins**：定义构建过程中使用的Maven插件
  - Maven插件是扩展Maven功能的核心组件，负责将抽象的构建生命周期阶段（如编译、测试、打包）转化为具体可执行的任务。
- **maven-compiler-plugin**：编译Java代码，配置注解处理器
- **spring-boot-maven-plugin**：打包Spring Boot应用为可执行JAR

**注解处理**：在编译时处理Java注解，生成额外的源文件或修改现有代码。Lombok就是一个常用的注解处理器，用于生成getter、setter等方法。

## Spring Boot应用入口详解（DemoApplication.java）

```
%%java
package com.example.demo;

import org.springframework.boot.SpringApplication; 
import org.springframework.boot.autoconfigure.SpringBootApplication; 

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### SpringApplication类

`SpringApplication`类是Spring Boot应用的引导类，主要职责包括：
- 引导应用程序启动
- 初始化Spring应用上下文
- 加载应用配置
- 启动嵌入式服务器（如Tomcat）
- 注册和管理bean

`run()`方法是启动整个应用的入口点，它接收主类和命令行参数，返回一个ApplicationContext对象。

### @SpringBootApplication注解

`@SpringBootApplication`是一个组合注解，相当于同时使用以下三个注解：

1. **@SpringBootConfiguration**：
   - 标记当前类为配置类
   - 允许定义@Bean方法并将其实例纳入Spring容器

2. **@EnableAutoConfiguration**：
   - 启用Spring Boot的自动配置机制
   - 根据项目依赖自动装配组件（如数据库连接池、Web框架等）
   - 减少手动配置的工作量

3. **@ComponentScan**：
   - 扫描当前包及其子包下的组件
   - 自动注册被@Component、@Service、@Controller等注解标记的类到Spring容器

Spring Boot的启动过程可以概括为以下七个关键步骤，每个步骤都对应着特定的初始化操作：

1. **准备环境**：
   - 创建和配置`Environment`对象，该对象负责管理应用的环境属性。
   - 读取系统属性和环境变量，这些属性将用于配置应用的行为。

2. **创建应用上下文**：
   - 根据应用的类型（如Web应用或非Web应用）创建相应的`ApplicationContext`实例。
   - 对于Web应用，会创建支持Web环境的上下文，如`AnnotationConfigServletWebServerApplicationContext`。

3. **应用前置处理**：
   - 调用`ApplicationContextInitializer`，这些初始化器允许在应用上下文刷新之前进行自定义的初始化操作。
   - 加载所有的`SpringApplicationRunListener`，这些监听器会在应用启动的不同阶段执行特定的逻辑。

4. **加载应用配置**：
   - 加载`application.properties`或`application.yml`文件，这些文件包含了应用的各种配置属性。
   - 处理命令行参数，这些参数可以覆盖配置文件中的设置。

5. **自动配置处理**：
   - 根据classpath中的依赖自动配置bean，这是Spring Boot的“约定优于配置”特性的体现。
   - 应用条件化配置，即根据特定的条件（如`@ConditionalOnClass`注解）来决定是否加载某个配置。

6. **启动嵌入式服务器**：
   - 如果是Web应用，Spring Boot会启动一个嵌入式服务器（如Tomcat、Jetty或Undertow）。
   - 将应用部署到这个嵌入式服务器上，使其能够接收和处理HTTP请求。

7. **发布应用就绪事件**：
   - 在所有初始化工作完成后，Spring Boot会发布`ApplicationReadyEvent`事件。
   - 这个事件通知所有的监听器，应用已经完全启动并准备好处理请求。

## 应用配置详解（application.properties）

Spring Boot的配置文件支持多种格式（properties、YAML、JSON等），以下是常见配置项的详细说明：

### 配置文件加载顺序

Spring Boot按照优先级从高到低加载以下位置的配置文件：

1. 命令行参数
2. JNDI属性
   1. JNDI（Java Naming and Directory Interface，Java命名与目录接口）是Java平台提供的一套标准API，用于访问各种命名和目录服务。其核心功能是通过名称绑定和查找对象，实现应用程序与底层服务（如数据库、LDAP、DNS等）的解耦
3. Java系统属性
4. 操作系统环境变量
5. 随机值属性源
6. profile特定配置文件（application-{profile}.properties）
7. 应用程序外部配置文件（application.properties）
8. @PropertySource注解指定的属性源
9.  默认属性

### 热部署配置

```properties
# 启用LiveReload功能，在代码更改时自动刷新浏览器
spring.devtools.livereload.enabled=true  
# 启用自动重启功能，代码更改后自动重启应用
spring.devtools.restart.enabled=true
```

LiveReload会在资源变更时自动刷新浏览器，提高开发效率。自动重启会在类文件变更时重新加载应用。

### 静态资源配置

```properties
# 配置静态资源访问路径
spring.mvc.static-path-pattern=/images/**
# 配置静态资源位置
spring.resources.static-locations=classpath:/static/
```

- **static-path-pattern**：指定客户端如何访问静态资源的URL模式
- **static-locations**：指定静态资源的实际存储位置

默认情况下，Spring Boot会从classpath下的/static、/public、/resources和/META-INF/resources目录中查找静态资源。

### 文件上传配置

```properties
# 设置上传文件的最大大小
spring.servlet.multipart.max-file-size=10MB
# 设置请求的最大大小
spring.servlet.multipart.max-request-size=10MB
```

- **max-file-size**：限制单个文件的最大大小
- **max-request-size**：限制整个请求的最大大小（包含所有文件）

### 配置属性绑定

Spring Boot可以自动将配置文件中的属性绑定到Java对象上：

```
%%java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private int version;
    // getter和setter方法
}
```

配置文件中：
```properties
app.name=MyApp
app.version=1
```

Spring Boot会自动创建AppProperties实例并注入值。

### 多环境配置（Profiles）

通过创建application-{profile}.properties或application-{profile}.yml文件来配置不同环境：

```properties
# application-dev.properties
server.port=8080
# application-prod.properties
server.port=80
```

通过以下方式激活特定配置：
- 命令行：`--spring.profiles.active=dev`
- 配置文件：`spring.profiles.active=dev`
- 环境变量：`SPRING_PROFILES_ACTIVE=dev`
