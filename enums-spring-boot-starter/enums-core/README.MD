# enums-core

## 介绍
标识型接口, 仅用于标明枚举类继承至该接口, 并增加默认的json序列化注解标识
## 软件架构
 ... ...

## 安装教程

```mxml
       <dependency>
             <groupId>com.arkxit</groupId>
             <artifactId>enums-core</artifactId>
             <version>1.0.0-RELEASE</version>
       </dependency>
```

## 使用说明

#### 1. 项目中的枚举类 实现 CodeEnum 接口,并重写获取 code 和 value 的方法,并加上枚举扫描的注解 @EnumAutoConverter 然后在实体类中就可直接使用,存入数据库的会是此枚举的code值
```java
/**
 * @author: zhuCan
 * @date: 2020/7/13 12:02
 * @description:
 */
@Getter
public enum  ElementStatusEnum implements CodeEnum {

  /**
   * 删除
   */
  DELETION(1,"删除"),

  /**
   * 标准
   */
  NORM(2,"标准"),

  /**
   * 定制
   */
  CUSTOMIZATION(3,"定制化");

  private final Integer code;
  private final String name;

}
```






#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
