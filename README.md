# community

# 1. 初识 Spring Boot

## 1.1 环境配置

### 开发环境

https://start.spring.io/

![image-20220309203455959](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203092034047.png)

### 依赖

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
```

### pom.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.6.4</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.c</groupId>
	<artifactId>community</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>war</packaging>
	<name>community</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
			<version>2.6.3</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

### 启动main()方法

![image-20220309204223567](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203092042642.png)

![image-20220309204303814](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203092043847.png)

### 定义第一个返回页面

com.c.community.controller.AlphaController.java

```java
package com.c.community.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    public String sayHello() {
        return "Hello Spring Boot";
    }
}
```

编译，启动，访问

![image-20220309204719887](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203092047916.png)

### Tomcat的配置

编辑application.properties文件

```properties
# tomcat的端口设置
server.port=8080
server.servlet.context-path=/community
```

![image-20220309205015673](https://raw.githubusercontent.com/chegn7/IMGRepo/IMG/202203092050705.png)

## 1.2 Spring 入门

https://spring.io

### Spring Core

#### Spring IoC

- IoC 控制反转 Inversion of Control

控制反转，是一种面向对象编程的思想

- Dependency Injection

依赖注入，是IoC思想的实现方式

- IoC Container

IoC容器，是实现依赖注入的关键，本质上是一个工厂



@Service @Controller @Repository @Component

这四个注解加到bean上都可以让bean被扫描到



@PostConstruct



@Scope





分布式session

粘性session

同步session

session服务器

数据库集群存储session（redis）





前端交互，可以把参数装到model里，也可以用request取值





显示登录信息

拦截器 降低耦合度

