# enums-spring-boot-starter

[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)
[![Gitee Stars](https://gitee.com/zc_oss/enums-spring-boot-starter/badge/star.svg?theme=dark)](https://gitee.com/zc_oss/enums-spring-boot-starter)
[![Gitee fork](https://gitee.com/zc_oss/enums-spring-boot-starter/badge/fork.svg?theme=dark)](https://gitee.com/zc_oss/enums-spring-boot-starter)

[English](https://gitee.com/zc_oss/enums-spring-boot-starter/blob/master/README_en.MD) | 简体中文

**enums-spring-boot-starter** 是一个针对 Java 枚举类型的开源项目，它可以帮助你在数据库操作和接口传参过程中自动转换枚举类型。通过使用这个库，你可以更方便地在不同的场景中使用 Java 枚举类型。

[toc]
## 主要特性
这个通用枚举处理器分为三个子Module架构来实现不同层面的枚举功能处理, 我们可以按需取用!
 1. **enums-core**: 枚举核心定义和转换
  - 枚举规范定义
  - 全局枚举Json自动序列化
  - MVC前后端枚举传参转换
 2. **enums-conversion**: 枚举全局数据库转换映射框架
  - 枚举与数据库数值的转换映射
  - Java对象与数据库Json的转换映射
  - 内置默认基础类型转换器
 3. **enums-scanner**: 全服务枚举码表扫描器
  - 全局枚举的code&name码表扫描
  - 提供枚举码表数据缓存扩展

[![架构图](resource/enum-starter.png)](架构图)


## 安装使用攻略
### 一、最小化引入使用(仅需前后端的传参枚举转换和Json序列化)
在我们的项目中, 如果仅仅是只需要使用到全局处理枚举的一些基础功能, 如前后端传参和获取数据时能自动实现枚举与数值的转换映射
那么我们仅需要本框架包中的**enums-core**模块部分

#### 使用Maven
在你的 `pom.xml` 文件中添加以下依赖：
   ```xml 
   
   <dependency>
       <groupId>io.arkx.framework</groupId>
       <artifactId>enums-core</artifactId>
       <version>1.1.8-RELEASE</version>
   </dependency>
   ```
#### 使用Gradle
在你的 build.gradle 文件中添加以下依赖： 
   ```groovy
   implementation 'io.gitee.zhucan123:enums-core:1.1.8-RELEASE'
   ```


### 使用方法

（在此部分提供关于如何在项目中使用该库的具体代码示例）

#### 示例

##### 1.定义我们的枚举

```java

@Getter
@AllArgsConstructor
public enum OrderBillStateEnum implements ExtensionEnum {
    PENDING_AUDIT(1, "待审核"),
    PART_AUDIT(2, "部分审核通过"),
    NOT_PASS(3, "审核不通过"),
    UNCONFIRMED(5, "未确认"),
    UNDER_COMPLAINT(7, "申诉中"),
    CONFIRMED(9, "已确认");

    private final int code;
    private final String name;
    
}
```

##### 2.前端接受参数枚举(前端传递code值就行)

如 http://localhost:8080/orders?billState=1

```java

@Data
public class PluginOrderBillQuery {

    @ApiModelProperty("账单审核状态")
    private OrderBillStateEnum billState;

}
```

##### 3.接口查询出来的数据DTO返回前端时, 定义的枚举会自动转换为code值

```java

@Data
public class PluginOrderBillDTO {

    @ApiModelProperty("账单id")
    private Long id;

    @ApiModelProperty("账单审核状态")
    private OrderBillStateEnum billState;
}
```
响应体结构示例
```json
{
  "success": 1,
  "message": "",
  "data": {
    "id": 1244,
    "billState": 1
  }
}
```







### 二、集成数据库的Enum映射和JSON映射转换
如果我们在使用到全局处理枚举的一些基础功能, 如前后端传参和获取数据时能自动实现枚举与数值的转换映射, 同时也需要枚举能自动与数据的数字类型进行转换时
我们则需要使用**enums-conversion**框架包 (包含了**enums-core**核心框架)
#### 使用Maven
在你的 `pom.xml` 文件中添加以下依赖：
```xml

<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkxos-framework-enum-starter-conversion</artifactId>
    <version>1.1.8-RELEASE</version>
</dependency>
```

#### 使用Gradle

在你的 build.gradle 文件中添加以下依赖：

```groovy
implementation 'io.gitee.zhucan123:spring-boot-starter-enums-conversion:1.1.8-RELEASE'
```
### 使用方法

（在此部分提供关于如何在项目中使用该库的具体代码示例）

#### 示例

##### 1.定义我们的枚举
需要注意其实我们只是需要额外的增加一个**@EnumAutoConverter**注解就行了

```java

@Getter
@AllArgsConstructor
@EnumAutoConverter
public enum OrderBillStateEnum implements ExtensionEnum {
    PENDING_AUDIT(1, "待审核"),
    PART_AUDIT(2, "部分审核通过"),
    NOT_PASS(3, "审核不通过"),
    UNCONFIRMED(5, "未确认"),
    UNDER_COMPLAINT(7, "申诉中"),
    CONFIRMED(9, "已确认");

    private final int code;
    private final String name;
    
}
```





### 三、全服务枚举码表扫描器
在我们服务中存在N多枚举状态等情况下, 我们需要提供一个全局枚举数据扫描器来给我们前端做各种选项候选项值时, 这个就是 **enums-scanner**(同样包含了**enums-core**核心框架)
#### 使用Maven
在你的 `pom.xml` 文件中添加以下依赖：
```xml

<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkxos-framework-enums-starter-scanner</artifactId>
    <version>1.1.8-RELEASE</version>
</dependency>
```

#### 使用Gradle

在你的 build.gradle 文件中添加以下依赖：

```groovy
implementation 'io.gitee.zhucan123:spring-boot-starter-enums-scanner:1.1.8-RELEASE'
```

### 使用方法

（在此部分提供关于如何在项目中使用该库的具体代码示例）

#### 示例

##### 1.定义我们的枚举
需要注意其实我们只是需要额外的增加一个**@EnumScan**注解就行了, 这个注解标识了我们需要扫描的枚举.

```java

@Getter
@AllArgsConstructor
@EnumScan
public enum OrderBillStateEnum implements ExtensionEnum {
    PENDING_AUDIT(1, "待审核"),
    PART_AUDIT(2, "部分审核通过"),
    NOT_PASS(3, "审核不通过"),
    UNCONFIRMED(5, "未确认"),
    UNDER_COMPLAINT(7, "申诉中"),
    CONFIRMED(9, "已确认");

    private final int code;
    private final String name;
    
}
```

##### 2. 提供一个Api用来获取全项目枚举数据(通过EnumScanHandler处理器可以得到全项目枚举数据)

```java
@RestController
@RequestMapping
public class EnumsController {

    @Resource
    private EnumScanHandler enumScanHandler;

    @GetMapping("/enums")
    public List<CodeTable> enums(){
        return enumScanHandler.codeTables();
    }
}

```
响应体结构示例
```json
[{
 	"enumName": "OrderBillStateEnum",
 	"items": [{
 		"code": 1,
 		"name": "待审核"
 	}, {
 		"code": 2,
 		"name": "部分审核通过"
 	}, {
 		"code": 3,
 		"name": "审核不通过"
 	}, {
        "code": 5,
      	"name": "未确认"
    }, {
      	"code": 7,
      	"name": "申诉中"
     }, {
      	"code": 9,
      	"name": "已确认"
     }],
 	"defaultItem": 1,
 	"classPath": "com.zhucan.management.infrastructure.constants.enums.OrderBillStateEnum"
 },{
	"enumName": "AbnormalHandleRoleEnum",
	"items": [{
		"code": 1,
		"name": "Scrum Master"
	}, {
		"code": 2,
		"name": "Product Owner"
	}, {
		"code": 3,
		"name": "Handler"
	}],
	"defaultItem": 1,
	"classPath": "com.zhucan.management.infrastructure.constants.enums.AbnormalHandleRoleEnum"
}, {
	"enumName": "AbnormalCauseTypeEnum",
	"items": [{
		"code": 1,
		"name": "需求分类错误"
	}, {
		"code": 2,
		"name": "租户名称未填写或格式不规范"
	}, {
		"code": 3,
		"name": "出库工时未填写"
	}, {
		"code": 4,
		"name": "出库工时为0，但未出库原因未填写"
	}, {
		"code": 5,
		"name": "需求评估工时不正确"
	}],
	"defaultItem": 1,
	"classPath": "com.zhucan.management.infrastructure.constants.enums.AbnormalCauseTypeEnum"
}]
```
## 贡献

我们欢迎所有对该项目感兴趣的开发者参与其中。请阅读我们的贡献指南了解更多信息。

## 许可证

该项目根据 Apache License 2.0 许可进行授权。


> 希望这个README模板对你有所帮助。你可以根据实际情况进一步完善和优化这个README文件，确保它包含所有必要的信息，以便其他开发者更容易理解和使用你的项目。祝你好运！
