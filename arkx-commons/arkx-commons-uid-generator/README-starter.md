# uid-springboot-starter 分布式发号器

QQ~~吹水~~交流群：183579482

当前项目是一个starter，支持扩展为发号器中间件，也支持各个服务引入使用。

## 一、pom.xml引入依赖

执行 mvn deploy 后引入依赖  

```xml
<dependency>
    <groupId>io.arkx.framework</groupId>
    <artifactId>arkx-commons-uid-generator</artifactId>
    <version>${last.version}</version>
</dependency>
```

## 二、定制化环境配置

1、保留了[百度UIDGenerator](https://github.com/baidu/uid-generator)优化策略的参数配置，可通过application.yml/properties进行配置。

2、关于生成器Bean的配置，有none和 memory两种模式：

- none：使用基础的生成器，已足够满足绝大部分需求。

- memory：使用RingBuffer的结构，能提供600万/s的稳定吞吐量，根据使用年限会有所减少。充环的时间，也是可以通过设置参数 uid.schedule-interval 配置的，间隔多少秒充满数组中的ID，默认为0时在消耗至50%后再次充满。

3、关于机器工作节点的WorkerIdAssigner（一次性）的分配，有none、redis、db三种模式：

- none：使用随机生成（范围0~1000）workerId的方式，尽可能减少多个服务实例使用相同的workerId，不建议使用。

- reids：使用Redis原子递增的方式给机器分配workerId。
- db：基于MySQL的自增主键来分配workerId，前提是各个服务使用的是同一个数据库服务器，运行时将会检查是否存在fun_cloud_base 数据库与tf_ap_worker_node表，不存在则自动创建。

（3）支持JPA生成器IdentityGenerator，如何使用？

```java
import javax.persistence.*;

@Entity
@Table(name="t_user")
public class UserEntity {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "uid-id")
	@GenericGenerator(name = "uid-id", strategy = UidGenerator.JPA_ID)
	private Long id;
    
   	@Column(name="username", length = 50)
	private String username;
}
```

## 三、快速启动

无需任何配置，直接开箱即用一键启动！

默认使用的生成器模式与分配器都是使用的none模式，根据所需进行如下更换状态即可。

application.yml

```yaml
ark:
  uid:
    # 分配器：none（默认）、db、redis
    assigner-mode: none
    # 生成器：none（默认）、memory
    generator-mode: none
```

应用示例：

```java
@Service
public class TestService {
    @Autowired
    UidGenerator uidGenerator;
    
    public void test(){
        uidGenerator.getUID();
    }
}
```

整合不易，开源不易，有帮上你的话，还望顺手点个star支持一下！谢谢！

欢迎提issue、进群 183579482 反馈与使用登记。



