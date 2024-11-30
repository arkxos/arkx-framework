package com.rapidark.autoconfigure;

import com.rapidark.framework.common.constants.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author darkness
 * @date 2021/7/30 17:16
 * @version 1.0
 */
@Slf4j
@AutoConfiguration
public class MqAutoConfiguration {

    /**
     * direct模式，直接根据队列名称投递消息
     *
     * @return
     */
    @Bean
    public Queue apiResourceQueue() {
        Queue queue = new Queue(QueueConstants.QUEUE_SCAN_API_RESOURCE);
        log.info("Query {} [{}]", QueueConstants.QUEUE_SCAN_API_RESOURCE, queue);
        return queue;
    }

    @Bean
    public Queue accessLogsQueue() {
        Queue queue = new Queue(QueueConstants.QUEUE_ACCESS_LOGS);
        log.info("Query {} [{}]", QueueConstants.QUEUE_ACCESS_LOGS, queue);
        return queue;
    }

    /**
     * 延迟队列: 创建一个延迟队列, 此队列中的消息没有消费者去消费, 到了过期时间之后变成死信, 变死信之后会根据
     *           绑定的DLX和routingKey重新发送到指定交换机再到指定队列。
     */
//    @Bean
//    public Queue monthBillItemQueue() {
//        Queue queue = new Queue(QueueConstants.QUEUE_Ltc_Bill_Transaction_Changed);
//        log.info("Query {} [{}]", QueueConstants.QUEUE_Ltc_Bill_Transaction_Changed, queue);
//        return queue;
//    }
}
