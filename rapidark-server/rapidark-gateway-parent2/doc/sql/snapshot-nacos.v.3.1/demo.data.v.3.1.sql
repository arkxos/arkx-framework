-- ######### 此为演示数据脚本 ##########
-- ######### 用于snapshot-nacos.v.3.1版本

/*Data for the table `apidoc` */

insert  into `apidoc`(`id`,`content`) values ('1',' ##### 内网API接口说明\n接口  | 项目 | 负责人 | 说明\n---|---|---|---\n无| 无 | 无| 无\n'),('3',' ##### 内网API接口说明\n接口  | 项目 | 负责人 | 说明\n---|---|---|---\nregUser| 用户管理系统 | 张三| 注册用户\ngetUser| 用户管理系统 | 张三| 获取用户\ndeleteUser| 用户管理系统 | 张三 | 删除用户\naddUser| 用户管理系统 | 李四 | 添加用户\ngetToken| 用户管理系统 | 李四 | 获取用户token'),('userCenter-getUser','\n### 接口信息\n1. 项目：project1\n2. 负责人：张三\n\n### 接口说明\n> 提供用户信息对接公共服务入口\n\n### 接口地址\n> http://192.168.11.45:8769/\n\n### 数据格式\n> content-type: application/json;charset=UTF-8\n\n### 请求参数\n```java\nid=123456\n```\n1. id:用户ID\n\n### 响应状态码\n1. 200：正常\n2. 其它码：异常\n\n### 响应参数\n```json\n{\"id\":\"123456\",\"name\":\"userName\",\"age\":23,\"sex\":\"1\"}\n```\n1. id:用户ID\n2. name:用户名称\n3. age:用户年龄\n4. sex:用户性别，1男，2女\n\n### 备注\n1. 为保证接口稳定性，同一个接口相同请求源和相同参数限制请求次数，每1s最多100次；\n2. 请求时请添加token值到header头部，以做身份识别；\n'),('userCenter-regUser','');

/*Data for the table `balanced` */

insert  into `balanced`(`id`,`name`,`groupCode`,`loadUri`,`status`,`createTime`,`updateTime`,`remarks`) values ('bcd1ed61eaee40eda56f128372f3b683','获取用户','interior_api','userCenter/getUser','0','2021-05-12 20:39:36','2021-10-13 20:17:25',NULL);

/*Data for the table `client` */

insert  into `client`(`id`,`systemCode`,`name`,`groupCode`,`ip`,`status`,`createTime`,`updateTime`,`remarks`) values ('c7473ec4be7544fc95986ad070ab7c06','RiskManage','风控系统','interior_api','192.168.11.45','0','2020-12-30 16:21:41','2021-09-26 15:44:31','风险分析与风险实时监控系统。负责人：徐某');

/*Data for the table `loadserver` */

insert  into `loadserver`(`id`,`routeId`,`balancedId`,`weight`,`createTime`,`updateTime`,`remarks`) values (1,'userCenter-getUser','bcd1ed61eaee40eda56f128372f3b683',31,'2021-05-12 20:39:36',NULL,NULL),(2,'EXAMPLES-getUser','bcd1ed61eaee40eda56f128372f3b683',78,'2021-10-13 20:17:25',NULL,NULL);

/*Data for the table `monitor` */

insert  into `monitor`(`id`,`status`,`emails`,`topic`,`recover`,`frequency`,`alarmTime`,`sendTime`,`updateTime`) values ('userCenter-getToken','2','admin@xxx.com','网关服务发生告警，请及时处理','0','30m','2021-09-30 15:09:03','2021-10-13 20:17:01','2021-09-27 14:57:38'),('userCenter-getUser','1','admin@xxx.com','监控告警','1','12h','2021-05-12 19:45:38','2021-05-12 19:45:04','2021-09-30 15:13:42'),('userCenter-regUser','0','','','0','1h',NULL,NULL,'2021-09-26 20:18:01');

/*Data for the table `regserver` */

insert  into `regserver`(`id`,`clientId`,`routeId`,`token`,`secretKey`,`tokenEffectiveTime`,`status`,`createTime`,`updateTime`) values (1,'c7473ec4be7544fc95986ad070ab7c06','userCenter-getUser','eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQ2VudGVyLWdldFVzZXIsYzc0NzNlYzRiZTc1NDRmYzk1OTg2YWQwNzBhYjdjMDYsMTYzMjk4NjU1MDExNSIsImlzcyI6InByby1zZXJ2ZXIiLCJleHAiOjE2MzI3NTg0MDB9.1XR8M1UqfqXZpasIKJAlkzhZSkeN8WoKkDUnIGiuPFU','c7473ec4be7544fc95986ad070ab7c06','2021-09-28 00:00:00','0','2020-12-30 16:22:24','2021-09-30 15:04:51');

/*Data for the table `gatewayAppRoute` */

insert  into `gatewayAppRoute`(`id`,`systemCode`,`name`,`groupCode`,`uri`,`path`,`method`,`host`,`remoteAddr`,`header`,`filterGatewaName`,`filterHystrixName`,`filterRateLimiterName`,`filterAuthorizeName`,`fallbackMsg`,`fallbackTimeout`,`replenishRate`,`burstCapacity`,`weight`,`status`,`stripPrefix`,`requestParameter`,`rewritePath`,`accessHeader`,`accessIp`,`accessParameter`,`accessTime`,`accessCookie`,`createTime`,`updateTime`) values ('EXAMPLES-getUser','CRM','用户管理服务-获取用户','interior_api','lb://provider-examples','/proxy/userCenter/getUser','','','','','',NULL,NULL,'','',0,20,100,NULL,'0',1,'','','','','','','','2021-10-13 19:53:50','2021-10-13 20:14:52'),('userCenter-addUser','CRM','用户管理系统-添加用户','interior_api','http://192.168.11.45:8769','/gatewayAppRoute/userCenter/addUser','POST',NULL,NULL,NULL,'',NULL,NULL,'','',0,20,100,NULL,'1',1,'',NULL,'','','','','','2020-12-30 15:37:00','2021-09-26 20:11:14'),('userCenter-delUser','CRM','用户管理系统-删除用户','interior_api','http://192.168.11.45:8769','/gatewayAppRoute/userCenter/delUser','GET',NULL,NULL,NULL,'',NULL,NULL,'','',0,20,100,NULL,'1',1,'',NULL,'','','','','','2020-12-30 15:35:22','2021-09-26 20:15:57'),('userCenter-feign','CRM','用户管理系统-Feign获取用户信息','interior_api','lb://consumer-examples','/gatewayAppRoute/userCenter/feign/getUser','GET','','','','','custom',NULL,'','',5000,20,100,NULL,'0',1,'version,snapshot-nacos.v.3.1','','','','','','','2021-02-01 17:33:38','2021-10-13 20:15:06'),('userCenter-getToken','CRM','用户管理系统-获取Token','interior_api','http://192.168.11.45:8769','/gatewayAppRoute/userCenter/getToken','','127.0.0.1:8771','127.0.0.1','X-Request-Id,userCenter-getToken','',NULL,NULL,'','',0,20,100,NULL,'0',1,'version,snapshot-nacos.v.3.1','/userCenter(?<segment>.*),/producer$\\{segment}','','','','','','2020-12-30 15:40:11','2021-09-27 14:57:38'),('userCenter-getUser','CRM','用户管理系统-获取用户','interior_api','http://192.168.11.45:8769','/gatewayAppRoute/userCenter/getUser','',NULL,NULL,NULL,'id,ip,token',NULL,NULL,'','',0,10,10,NULL,'0',1,'',NULL,'','','','','','2020-12-30 15:34:25','2021-09-30 15:13:42'),('userCenter-regUser','CRM','用户管理系统-注册用户','interior_api','http://192.168.11.45:8769','/gatewayAppRoute/userCenter/regUser','POST',NULL,NULL,NULL,'',NULL,NULL,'','',0,20,100,NULL,'1',1,'',NULL,'','','','','','2020-12-30 15:33:01','2021-09-26 20:18:01');

/*Data for the table `secureip` */

insert  into `secureip`(`ip`,`status`,`createTime`,`updateTime`,`remarks`) values ('192.168.11.45','0','2020-12-30 16:39:39','2020-12-30 16:50:25','非法IP');
