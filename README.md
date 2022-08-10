# B站秒杀项目

## 项目知识点总结
超卖问题解决方案：
超卖问题导致的原因：库存判断是否为0和减库存两个操作不是原子的导致的。
解决方案：
1. 事务+悲观锁的方式处理，缺点是并发操作到这一步变成串行操作，并发度降低。qps只有200左右。
2. 乐观锁：库存为0直接取消，更新的时候在sql中判断库存和读取的库存一致才能减一更新。否则不更新。
3. update语句在sql中是原子的，那么直接只语句中判断库存大于0，才更新，负责就不会更新。
   相同用户重复购买的问题：
- 使用数据库联合索引：用户id+商品id的方式，避免重复下单。抛出异常重复key，事务回滚，保证数据的一致性。
- 仅仅使用redis标记，或者是缓存，不能保证用户重复下单的问题。还是会有重复的请求打到数据库。

并发度问题：
- 在redis缓存中扣库存，然后使用消息中间件异步写入数据库订单数据，可以提高并发度。
- 当库存没有的时候，直接服务端缓存库存为0的数据，可以避免后续的redis缓存访问操作，因为redis访问也是需要需要网络IO时间的。

如何避免并发量太高冲击服务，导致服务崩溃？
- 使用令牌桶限流算法，限制访问服务的流量。初始的时候：令牌把桶添满，所有的请求来了，都能获得令牌，获得处理，当令牌用完了，直接丢弃请求，不处理（提示 太火热的消息），后续的流量就会按照令牌的发放速度，恒定发送给服务器处理，避免服务器崩库。
- 前端限流：使用验证方式，不同人输入验证方式的时间不同，使得请求被均分。
- 接口隐藏，活到开始前，不提供正确秒杀接口，服务开始后，通过访问服务端获取正确秒杀接口，才能执行秒杀操作。
- 对一些异常请求设置黑名单，限制访问。
- 单个用户限制访问次数，（前后端共同限制）

rabbitmq死信消息：
当rabbitmq消费者消费消息失败的时候，会一直消费消息，最终导致服务阻塞。解决方式：处理好服务中所有可能发生的异常。

项目亮点：
使用jmeter测试：多次平均：单机qps从：280提高到3300

##参考：
源码地址：https://gitee.com/guizhizhe/seckill_demo.git
https://github.com/Grootzz/seckill
002_学习目标_哔哩哔哩_bilibili
微信公众号-后端漫谈-秒杀项目
我实现的源码地址：https://gitee.com/guizhizhe/seckill_demo.git
https://gitee.com/52itstyle/spring-boot-seckill?_from=gitee_search


有很多拓展思路：
能不能在本地换粗扣减库存，进一步提高并发。
能不能在本地：记录sql，然后load命令写入数据库，提高并发。
能不能将库存数量，分布在多个服务实例上，然后在不同的服务实例扣减库存，最终汇总到数据库

跟着B站视频敲代码而来[地址](https://www.bilibili.com/video/BV1sf4y1L7KE )

## 视频内容

1. 项目框架搭建
   1. SpringBoot环境搭建
   2. 集成Thymeleaf,RespBean
   3. MyBatis
2. 分布式会话
   1. 用户登录
      1. 设计数据库
      2. 明文密码二次MD5加密
      3. 参数校验+全局异常处理
   2. 共享Session
      1. SpringSession
      2. Redis
3. 功能开发
   1. 商品列表
   2. 商品详情
   3. 秒杀
   4. 订单详情
4. 系统压测
   1. JMeter
   2. 自定义变量模拟多用户
   3. JMeter命令行的使用
   4. 正式压测
      1. 商品列表
      2. 秒杀
5. 页面优化
   1. 页面缓存+URL缓存+对象缓存
   2. 页面静态化，前后端分离
   3. 静态资源优化
   4. CDN优化
6. 接口优化
   1. Redis预减库存减少数据库的访问
   2. 内存标记减少Redis的访问
   3. RabbitMQ异步下单
      1. SpringBoot整合RabbitMQ
      2. 交换机
7. 安全优化
   1. 秒杀接口地址隐藏
   2. 算术验证码
   3. 接口防刷
8. 主流的秒杀方案

## 软件架构

|                         技术                          | 版本  |                            说明                            |
| :---------------------------------------------------: | :---: | :--------------------------------------------------------: |
|                      Spring Boot                      | 2.6.4 |                                                            |
|                         MySQL                         |   8   |                                                            |
| [MyBatis Plus](https://github.com/baomidou/generator) | 3.5.1 |                                                            |
|                       Swagger2                        | 2.9.2 |        Swagger-models2.9.2版本报错，使用的是1.5.22         |
|         [Kinfe4j](https://doc.xiaominfo.com)          | 2.0.9 | 感觉比Swagger UI漂亮的一个工具，访问地址是ip:端口/doc.html |
|                   Spring Boot Redis                   |       |                                                            |



## 使用说明

登录页面：http://localhost:8080/login/toLogin

接口文档页面：http://localhost:8080/doc.html#/home

代码生成器：https://gitee.com/guizhizhe/code-generator.git

sqldoc：创建表语句和回滚压测数据

sftware：是从B站用户@登就等觉得 下载的一些视频中程序的安装包（我没有使用，我是用的是docker里面）

document：是从B站用户@登就等觉得 下载的系统说明文档，如果根据视频看的话，可以看文档里面，里面的代码是根据视频进度编写的，但是不要复制，pdf会有问题建议手打。





## 注意事项

1. 代码运行时一定要用localhost，原因可以看下CookieUtil类
2. 代码生成时没有加去掉表头，导致代码和视频中不一样，命名也有的不一样，



## 敲代码后感

​	首先要感谢B站用户@登就等觉得，从他那下载的静态资源，不然这个视频就看不下去了。

​	看视频，一定要敲，敲多了就会了，编程就是你知道的多了就牛逼了。

​	写到这吧，后面学WebStock，有推荐的视频可以留言。
