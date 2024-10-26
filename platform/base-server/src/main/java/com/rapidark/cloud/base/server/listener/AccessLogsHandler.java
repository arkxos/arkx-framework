package com.rapidark.cloud.base.server.listener;

import com.alibaba.fastjson.JSON;
import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.base.server.repository.GatewayAccessLogsRepository;
import com.rapidark.framework.boot.ip2region.IP2regionTemplate;
import com.rapidark.framework.common.constants.QueueConstants;
import com.rapidark.framework.common.utils.BeanConvertUtils;
import com.rapidark.framework.common.utils.SystemIdGenerator;
import com.rapidark.framework.commons.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

/**
 * mq消息接收者
 *
 * @author liuyadu
 */
@Configuration
@Slf4j
public class AccessLogsHandler {

    @Autowired
    private GatewayAccessLogsRepository gatewayAccessLogsRepository;

    /**
     * 临时存放减少io
     */
    @Autowired
    private IP2regionTemplate ip2regionTemplate;

    @Autowired
    private SystemIdGenerator systemIdGenerator;

    /**
     * 接收访问日志
     *
     * @param message
     */
    @RabbitListener(queues = QueueConstants.QUEUE_ACCESS_LOGS)
    public void accessLogsQueue(@Payload String message) {
        try {
            if (!StringUtil.isEmpty(message)) {
                GatewayAccessLogs logs = JSON.parseObject(message, GatewayAccessLogs.class);
                if (logs != null) {
                    if (logs.getIp() != null) {
                        logs.setRegion(ip2regionTemplate.getRegion(logs.getIp()));
                    }
                    logs.setAccessId(systemIdGenerator.generate());
                    logs.setUseTime(logs.getResponseTime().getTime() - logs.getRequestTime().getTime());
                    gatewayAccessLogsRepository.save(logs);
                }
            }
        } catch (Exception e) {
            log.error("error:", e);
        }
    }
}
