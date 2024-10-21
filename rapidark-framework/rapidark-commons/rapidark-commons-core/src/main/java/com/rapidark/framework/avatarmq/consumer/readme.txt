consumer：
消息中间件中的消费者模块，负责接收生产者过来的消息。
在设计的时候，会对消费者进行一个集群化管理，同一个集群标识的消费者，会构成一个大的消费者集群，作为一个整体，接收生产者投递过来的消息。
此外，还提供消费者接收消息相关的API给客户端进行调用。

Consumer Clusters Manage / Message Routing：
消息的消费者集群管理以及消息路由模块
消息消费者对象 AvatarMQConsumer
消息的集群管理模块:
主要代码是ConsumerContext.java、ConsumerClusters.java。
消费者集群模块ConsumerClusters，主要负责定义消费者集群的行为，以及负责消息的路由。
ConsumerContext主要的负责管理消费者集群