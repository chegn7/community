一、介绍：
1. 框架
ssm

2. 开发环境
构建工具：Apache Maven

集成开发工具: IntelliJ IDEA

数据库：MySQL、Redis

应用服务器：Apache Tomcat

版本控制工具：Git

3. Spring MVC
三层架构：表现层、业务层、数据访问层

MVC

-Model：模型层

-View：视图层

-Controller：控制层

核心组件：

前端控制器：DispatcherServlet

​     


4. 创建maven项目
可在  https://start.spring.io/   创建maven项目

5. MyBatis
MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生类型、接口和 Java 的 POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

核心组件

--SqlSessionFactory: 用于创建SqlSession的工厂类

--SqlSession：MyBatis的核心组件，用于面向数据库执行SQL

--主配置文件：XML配置文件，可以对MyBatis的底层行为做出详细的配置

--Mapper接口：Dao接口，在MyBatis中习惯称为Mapper

--Mapper映射器：用于编写SQL，并将SQL和实体类映射的组件，采用XML、注解均可实现

6. 版本控制工具 - Git


二、开发社区登录模块
1. 发送邮件
邮箱设置：

——启用客户端SMTP服务

 

Spring Email：

——导入jar包

——邮箱参数配置

——使用javaMailSend发送邮件

 

模板引擎：

——使用Thymeleaf发送HTML邮件

 

2. 注册功能
访问注册页面：

——点击链接，打开注册页面

 

提交注册数据：

——通过表单提交数据

——服务端验证账号是否已存在，邮箱是否已注册

——服务端发送激活邮件

 

激活注册账号：

——点击邮件中的链接，访问服务端的激活服务。

 

3. 会话管理
HTTP基本性质：

——HTTP是简单的、可扩展的、无状态的、有会话的

 

Cookie：

——是服务器发送到浏览器，并保存在浏览器端的一块数据。

——浏览器下次访问该服务器时，会自动携带该块数据，并将其发送服务器

 

Session

——是JavaEE的标准，用于在服务器端记录客户端信息。

——数据存放在服务器端更加安全，但是也会增加服务端的内存压力。

 

4. 生成验证码
kaptcha：

——导入jar包（pom添加依赖）

——编写Kaptcha配置类

——生成随机字符、生成图片


5. 登录、退出功能
访问登录页面

登录：

——验证账号、密码、验证码

——成功，生成登录凭证，发送给客户端

——失败，跳回登录页

退出：

——将登录凭证修改为失效状态

——跳转首页

 

6. 显示登录信息
拦截器示例：

——定义拦截器，实现HandlerInterceptor

——配置拦截器，为它指定拦截、排除的路径

拦截器应用：

——在请求开始时查询登录用户

——在本次请求中持有用户数据

——在模板视图上显示用户数据

——在请求结束时清理用户数据

 

7. 账号设置
上传文件：

——请求：必须是POST请求

——表单：enctype = “multipart/from-data”

——Spring MVC：通过MultipartFile处理上传文件

开发步骤：

——访问账号设置页面

——上传头像

——获取头像

8. 检查登录状态
使用拦截器：

——在方法前标注自定义注解

——拦截所有请求，只处理带有该注解的方法

自定义注解：

——常用元注解：@Target、@Retention、@Document、@Inherited

——读取注解：Method.getDeclaredAnnotations(),  Method.getAnnotation(Class<T> annotationClass)

 

三、社区核心功能
1. 过滤敏感词
前缀树：

——名称：Trie、字典树、查找树

——特点：查找效率高，消耗内存大

——应用：字符串检索、词频统计、字符串排序等

敏感词过滤器：

——定义前缀树

——根据敏感词，初始化前缀树

——编写过滤敏感词的方法

 

2. 发布帖子
AJAX

——Asynchronous JavaScript and XML

——异步的JavaScript与XML，不是一门新技术

——使用AJAX，网页能够增量更新呈现在页面上，而不需要刷新整个页面

——虽然X代表XML，但目前JSON的使用比XML更加普遍

示例：

——使用jQuery发送AJAX请求。

实践：

——采用AJAX请求，实现发布帖子的功能。

 

3. 帖子详情
DiscussPostMapper

DiscussPostService

DiscussPostController 

index.html

——在帖子标题上增加访问详情页面的链接

discuss-detail.html

——处理静态资源的访问路径

——复用index.html的header区域

——显示标题、作者、发布时间、帖子正文等内容

 

4. 显示评论
数据层

——根据实体查询一页评论数据。

——根据实体查询评论的数量。

业务层

——处理查询评论的业务。  

——处理查询评论数量的业务。

表现层

——显示帖子详情数据时，同时显示该帖子所有的评论数据

 

5. 添加评论
数据层：

——增加评论数据。

——修改帖子的评论数量。

业务层：

——处理添加评论的业务：先增加评论、再更新帖子的评论数量。

表现层 ：

——处理添加评论数据的请求。

——设置添加评论的表单。

 

6. 私信列表
私信列表

——查询当前用户的会话列表，   每个会话只显示一条最新的私信。

——支持分页显示。

私信详情

——查询某个会话所包含的私信。

——支持分页显示。

 

7. 发送私信
发送私信

——采用异步的方式发送私信。

——发送成功后刷新私信列表。

设置已读

——访问私信详情时，   将显示的私信设置为已读状态。

 

8. 统一处理异常
@ControllerAdvice

——用于修饰类，表示该类是Controller的全局配置类。

——在此类中，可以对Controller进行如下三种全局配置：异常处理方案、绑定数据方案、绑定参数方案。

@ExceptionHandler

——用于修饰方法，该方法会在Controller出现异常后被调用，用于处理捕获到的异常。

@ModelAttribute

——用于修饰方法，该方法会在Controller方法执行前被调用，用于为Model对象绑定参数。

@DataBinder

——用于修饰方法，该方法会在Controller方法执行前被调用，用于绑定参数的转换器。

 

四、Redis，一站式高性能存储方案
1. Redis入门
Redis是一款基于键值对的NoSQL数据库，它的值支持多种数据结构：字符串(strings)、哈希(hashes)、列表(lists)、集合(sets)、有序集合(sorted sets)等。

Redis将所有的数据都存放在内存中，所以它的读写性能十分惊人。 同时，Redis还可以将内存中的数据以快照或日志的形式保存到硬盘上，以保证数据的安全性。

Redis典型的应用场景包括：缓存、排行榜、计数器、社交网络、消息队列等。

 

2. Spring整合Redis
 引入依赖

——spring-boot-starter-data-redis

配置Redis

——配置数据库参数

——编写配置类，构造RedisTemplate

访问Redis

——redisTemplate.opsForValue()

——redisTemplate.opsForHash()

——redisTemplate.opsForList()

——redisTemplate.opsForSet()

——redisTemplate.opsForZSet()

 

3. 点赞
点赞 ：

——支持对帖子、评论点赞。

——第1次点赞，第2次取消点赞。

首页点赞数量 ：

——统计帖子的点赞数量。

详情页点赞数量 ：

——统计点赞数量。

——显示点赞状态。

 

4. 收到的赞
 重构点赞功能：

——以用户为key，记录点赞数量

——increment(key)，decrement(key)

开发个人主页

——以用户为key，查询点赞数量

 

5. 关注、取消关注
需求

——开发关注、取消关注功能。

——统计用户的关注数、粉丝数。

关键

——若A关注了B，则A是B的Follower（粉丝），B是A的Followee（目标）。

——关注的目标可以是用户、帖子、题目等，在实现时将这些目标抽象为实体。

 

6. 关注列表、粉丝列表
 业务层：

——查询某个用户关注的人，支持分页。

——查询某个用户的粉丝，支持分页。

表现层 ：

——处理“查询关注的人”、“查询粉丝”请求。

——编写“查询关注的人”、“查询粉丝”模板。

 

7. 优化登录模块
使用Redis存储验证码:

——验证码需要频繁的访问与刷新，对性能要求较高。

——验证码不需永久保存，通常在很短的时间后就会失效。

——分布式部署时，存在Session共享的问题。

使用Redis存储登录凭证：

——处理每次请求时，都要查询用户的登录凭证，访问的频率非常高。

使用Redis缓存用户信息：

——处理每次请求时，都要根据凭证查询用户信息，访问的频率非常高。

 

五、Kafka，构建TB级异步消息系统
1. 阻塞队列
BlockingQueue ：

——解决线程通信的问题。

——阻塞方法：put、take。

生产者消费者模式：

——生产者：产生数据的线程。

——消费者：使用数据的线程。

实现类 ：

——ArrayBlockingQueue

——LinkedBlockingQueue

——PriorityBlockingQueue、SynchronousQueue、DelayQueue等。 

 

2. Kafka入门
Kafka简介:

——Kafka是一个分布式的流媒体平台。

——应用：消息系统、日志收集、用户行为追踪、流式处理。

Kafka特点 ：

——高吞吐量、消息持久化、高可靠性、高扩展性。

Kafka术语：

——Broker、Zookeeper - Topic、Partition、Offset - Leader Replica 、Follower Replica

 

3. Spring整合Kafka
引入依赖：

—— spring-kafka

配置Kafka：

——配置server、consumer

访问Kafka ：

——生产者 

        kafkaTemplate.send(topic, data);

——消费者 

        @KafkaListener(topics = {"test"})
    
        public void handleMessage(ConsumerRecord record) {}

 


4. 发送系统通知
触发事件 ：

——评论后，发布通知

——点赞后，发布通知

——关注后，发布通知

处理事件 

——封装事件对象

——开发事件的生产者

——开发事件的消费者


5. 显示系统通知
 通知列表：

——显示评论、点赞、关注三种类型的通知

通知详情 ：

——分页显示某一类主题所包含的通知

未读消息 ：

——在页面头部显示所有的未读消息数量

 

六、Elasticsearch，分布式搜索引擎
1. Elasticsearch入门
Elasticsearch简介：

——一个分布式的、Restful风格的搜索引擎。

——支持对各种类型的数据的检索。

——搜索速度快，可以提供实时的搜索服务。

——便于水平扩展，每秒可以处理PB级海量数据。

Elasticsearch术语 ：

——索引、类型、文档、字段。

——集群、节点、分片、副本。

 

2. Spring整合Elasticsearch
引入依赖 :

——spring-boot-starter-data-elasticsearch

配置Elasticsearch：

—— cluster-name、cluster-nodes

Spring Data Elasticsearch

——ElasticsearchTemplate

——ElasticsearchRepository

 

3. 开发社区搜索功能
 搜索服务：

——将帖子保存至Elasticsearch服务器。

——从Elasticsearch服务器删除帖子。

——从Elasticsearch服务器搜索帖子。

发布事件：

——发布帖子时，将帖子异步的提交到Elasticsearch服务器。

——增加评论时，将帖子异步的提交到Elasticsearch服务器。

——在消费组件中增加一个方法，消费帖子发布事件。

显示结果：

——在控制器中处理搜索请求，在HTML上显示搜索结果。

 

七、构建安全高效的企业服务
1. Spring Security
简介：

——Spring Security是专注于为java应用程序提供身份认证和授权的框架，它的强大之处在于它可以轻松扩展以满足自定义的需求。

特征：

——对身份的认证和授权提供全面的、可扩展的支持。

——防止各种攻击，如会话固定攻击、点击劫持、csrf攻击等。

——支持与Servlet API、Spring MVC等Web技术集成。

 

2. 权限控制

登录检查:

——之前采用拦截器实现登录检查，这仅仅是简单的权限管理方案，现将其废弃。

授权配置：

——对当前系统内包含的所有的请求，分配访问权限（普通用户、管理员、版主）

认证方案：

——绕过Security认证流程，采用系统原来的认证方案。

CSRF配置：

——防止CSRF攻击的基本原理，以及表单、AJAX相关的配置。

 

3. 置顶、加精、删除
功能：

——点击置顶，修改帖子类型

——点击“加精”、“删除”，修改帖子状态

权限管理：

——版主：置顶、加精。

——管理员：删除。

按钮显示：

——版主：置顶、加精。

——管理员：删除。

 

4. Redis高级数据类型
HyperLogLog：

——采用一种基数算法，用于完成独立总数统计。

——占据空间小，无论统计多少个数据，只占12k内存空间。

——不精确的统计算法，标准误差0.81%。

Bitmap：

——不是一种独立的数据结构，实际上就是字符串

——支持按位存取数据，可以将其看成是byte数组。

——适合存储索大量的连续的数据的布尔值。

 

5. 网站数据统计
UV（Unique Visitor）：

——独立访客，需通过用户IP排统计数据。

——每次访问都要进行统计。

——HyperLogLog，性能好，存储空间小。

DAU（Daily Active User）：

——日活跃用户，需通过用户ID排重统计数据。

——访问过一次，则认为活跃。

——Bitmap，性能好，可统计精确的结果。

 

6. 任务执行和调度
JDK线程池：

——ExecutorService

——ScheduledExecutorService

Spring线程池：

——ThreadPoolTaskExecutor

——ThreadPoolTaskScheduler

分布式定时任务：

——Spring Quartz

 

7. 热帖排行
公式：

——log ( 精华分 + 评论数 * 10 + 点赞数 * 2 ） + （ 发布时间 - 牛客纪元 ）

 

8. 生成长图
wkhtmltopdf

——wkhtmltopdf url file

——wkhtmltoimage url file

java

——Runtime.getRuntime().exec()

 

9. 将文件上传至云服务器
客户端上传：

——客户端将数据提交给云服务器，并等待响应。

——用户上传头像时，将表单数据提交给云服务器。

服务器直传：

——应用服务器将数据直接提交给云服务器，并等待响应。

——分享时，服务端自动生成的图片，提交给云服务器。

使用七牛云服务器。

 

10. 优化网站性能
本地缓存：

——将数据缓存在应用服务器上，性能最好。

——常用缓存工具：Ehcache、Guava、Caffeine等

分布式缓存：

——将数据缓存在NoSQL数据库上，跨服务器。

——常用缓存工具：MemCache、Redis等。

多级缓存：

——一级缓存（本地缓存）> 二级缓存（分布式缓存）> DB

——避免缓存雪崩（缓存失效，大量请求直达DB），提高系统的可用性。

 

八、项目发布与总结