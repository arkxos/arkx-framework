# flying-fish-gateway（飞鱼网关系统）

#### 介绍

基于spring-cloud-gateway开发的网关路由服务，用于服务与服务之间的通讯边界管理，支持过滤器、熔断、鉴权、限流、日志、监控等功能

解决目前项目中A==>B,A==>C,A==>...,服务与服务之间以直链访问的方式，改为A==>gateway==>B\C\...，服务走网关访问另一个服务，使得服务通讯可控、可复用、统一管理、安全鉴权、服务能力保护等效果

![网络架构流程](https://oscimg.oschina.net/oscnet/up-561ee6859c868ed8b098b313097d4f726c5.png "网络架构流程")

(网络架构流程)

#### 特色

1.  完全支持动态网关路由配置，在控制台界面修改完毕，即可生效
2.  支持可动态配置IP、ID、TOKEN过滤器
3.  支持可动态配置全局、自定义熔断器，实现业务高峰抗压能力
4.  支持可动态配置IP、URL、REQUESTID等基于令牌桶算法自定义限流器，实现业务峰值固化，避免雪崩效应
4.  支持可动态配置HEADER、IP、请求参数、时间、Cookie等鉴权验证，加强业务访问安全，避免或减少每个业务服务重复实现安全鉴权规则

#### 流程图
![网络拓扑图](https://oscimg.oschina.net/oscnet/up-eb3ae5a6e7c95e3f28ac4bc666e6455ea9a.png "网络拓扑")

（网络拓扑）

![执行流程图](https://oscimg.oschina.net/oscnet/up-060f6ce5895acaad0bb35dafca4c76ecf1e.png "执行流程")

（执行流程）

1.  前置nginx用于负载均衡，将请求代理分发到不同网关；
2.  gateway网关路由对请求进行鉴权、限流、熔断、日志等；
3.  gateway网关路由对注册服务进行管理与路由转发；

注：可不使用nginx，单机部署，客户端直连gateway网关转发到服务端，考虑高可用推荐前置nginx代理

#### 软件架构

软件架构说明

本工程共分五个模块：
1.  dynameic-gateway为核心网关路由服务，提供客户端请求转发，服务端地址路由功能，以及过滤器、熔断、鉴权、限流、日志、监控等功能
2.  dynameic-manage为界面可视化管理后台，提供客户端管理、服务端管理、IP访问管理等功能
3.  formwork为核心框架，提供基础与公共业务处理模块与类
4.  admin-server为springboot服务提供监控管理，支持可视化WEBUI，只需启动即可，无其它业务操作
5.  examples为示例项目
6.  doc为项目使用说明文档，与升级版本SQL脚本


后续开发计划：
1.  增加全局日志输出，按一定格式存储，方便后续引入elk做数据分析（已支持）
2.  增加临控功能，支持大厅全局图表展示，与单个服务、单个客户端等访问量、流量、错误等监控图表展示（已部份支持）
3.  增加可编程脚本组件，实现代码动态编译、热部署（已支持）


#### 安装教程

1.  依赖spring-boot、spring-cloud，注意版本的搭配
2.  需安装eureke、consul、nacos等其中任意一种，也可无需注册中心运行
3.  详细安装文档：**[doc/flying-fish-gateway-说明文档.docx](https://gitee.com/omsgit/flying-fish-gateway/tree/snapshot-nacos.v.3.2/doc)** 

#### 使用说明

1.  jdk1.8 +
2.  mysql.5.x + 
3.  redis3.x +

#### 前端项目

1. 本项目采用前后端分离
2. 前端采用vue2.x开发，可能部份前端插件会下载缓慢（也有可能失败），请多尝试几次安装
3. 注：另一个配套的前端控制台管理项目 flaying-fish-manage ，已将前端代码移至在本项目pages/目录下，后续该前端控制台管理项目独立仓库将会下线，不再同时更新，统一在此一个项目内发布 

#### 前端界面
![负载管理](https://oscimg.oschina.net/oscnet/up-69a24d02ef07aa936fb08d975094fc091f9.png "负载管理")
(负载管理)
![服务详情](https://oscimg.oschina.net/oscnet/up-59965a0a5c4a81408c39ed05e455d1cd92e.png "服务详情")
（服务详情）
![注册服务](https://oscimg.oschina.net/oscnet/up-a6bf4b28a70bebfad8a64ec06ca3048f7ab.png "注册服务")
（注册客户端拓扑）
![注册客户端](https://oscimg.oschina.net/oscnet/up-1fc8fe48b58c22b5ed55fcfc8ceb967dbae.jpg "注册客户端")
（注册客户端）
![创建网关服务](https://oscimg.oschina.net/oscnet/up-20e95055f547cfb2ed50ccad0325e804d51.jpg "创建网关服务")
（创建网关服务）
![网关规则组件](https://oscimg.oschina.net/oscnet/up-81032cc9d0db52a33e85a448d39bec73a01.jpg "网关规则组件")
（网关规则组件）
![规则组件代码](https://oscimg.oschina.net/oscnet/up-bf5c7972212f79faaef2f54240cca3ea279.jpg "规则组件代码")
（规则组件代码）
![IP名单](https://oscimg.oschina.net/oscnet/up-4f65dc499db0ac4031b94f86e8d4c93737f.png "IP名单")
（IP名单）
![接口文档](https://oscimg.oschina.net/oscnet/up-a5fad9cf38978d22150fb633e367583d720.png "接口文档")
（接口文档）
![接口统计](https://oscimg.oschina.net/oscnet/up-72f2ccc487377e1f585c72b270e3c0d529e.JPEG "接口统计")
（接口统计）
![接口监控](https://oscimg.oschina.net/oscnet/up-16fe307cc86ff3d49f0dce35b6019549d26.JPEG "接口监控")
（接口监控）

[参见个人说明文章](https://my.oschina.net/u/437309?tab=newest&catalogId=7056744)

#### 更新记录
++++++++ **snapshot-nacos.v.3.4**
1. 增加基于redis的请求响应结果缓存策略，可设置配置、字典等固定数据接口响应结果缓存时长,减少后端接口高并发压力
2. 代码优化，界面微调，已知问题修复

注：前端代码已移至本项目的pages目录

++++++++ **snapshot-nacos.v.3.3**
1. 升级springcloud 2021.0.4\springboot 2.6.11版本
2. 升级nacos注册与发现中心 2.1.2版本
3. 增加基于alibaba流量治理组件Sentinel实现流量控制、熔断降级服务（版本升级已移除Hystrix、RateLimiter服务），并支持sentinel-1.8.5与nacos-2.1.2配置互通
4. 代码优化，界面调整，已知问题修复
5. 网关管理平台，增加登录鉴权
6. 集成vue已编译打包代码，直接生成jar启动即可访问管理平台

注：前端需要同步更新到snapshot-nacos.v.3.3; 最近一年多因疫情、工作、各种原因更新比较缓慢；后续视有利条件将加强维护与升级；

  ++++++++ **snapshot-nacos.v.3.2** 
 1. 增加基于groovy语言开发网关路由动态规则组件，对请求和响应做热编码逻辑处理(适用场景：参数较验、数据包装、加密|鉴权、内容格式化、前置与后置判断、消息推送、事件回调、日志打印等)
 2. 代码优化，界面微调，已知问题修复
 3. 更新VUE前端package.json中的插件版本
 
 注：前端需要同步更新到snapshot-nacos.v.3.2
 
 ++++++++ **snapshot-nacos.v.3.1** 
1. 优化网关发布后，通过nacos配置中心实时推送并加载网关配置，替代原定时扫描模式
2. 增加基于jwt的客户端访问网关Token权限较验
3. 代码优化，界面微调，已知问题修复
4. doc增加示例数据脚本

注：前端需要同步更新到snapshot-nacos.v.3.1

 ++++++++ **snapshot-nacos.v.3.0** 
1. 升级springcloud Hoxton.SR10\springboot 2.3.9.RELEASE版本
2. 去除eureka注册发现中心，改为nacos（2.0.3版本）实现动态服务注册与发现、配置管理
3. 此版本基于snapshot.v.3.0改造，核心代码一致
4. 增加示例子模块

注：前端需要同步更新到snapshot-nacos.v.3.0

 ++++++++ **snapshot.v.3.0** 
1. 升级springcloud Hoxton.SR10\springboot 2.3.9.RELEASE版本
2. 扩展功能：新增接口心跳监控、邮件告警功能
3. 代码优化，已知问题修复

注：前端需要同步更新到v.3.0

 ++++++++ **snapshot.v.2.0** 
1. 代码优化，已知问题修复
2. 扩展功能：新增接口访问统计功能
3. 优化：客户端名称可修改。

注：前端需要同步更新到v.2.0

 ++++++++ **snapshot.v.1.0** 
1. 代码优化，已知问题修复
2. 扩展功能：接口文档，提供API接口文档描述功能
3. 添加限流拦截之后的通用规范json格式输出

 ++++++++ **master** 
1. 初始版本
2. 建立基础功能：负载管理、服务管理、客户端管理、IP名单管理
3. 其它底层开发


#### 参与贡献

1.  本项目为个人开发，基于实际应用场景设计与架构，满足内部业务代理网关需求与使用
2.  欢迎有兴趣的朋友下载、学习、使用，如有问题请在issues中留言
3.  本项目完全免费开源，可自行修改、编辑、另行发版与使用，不受任何商业限制（保不保留原作者信息，无所谓了）

