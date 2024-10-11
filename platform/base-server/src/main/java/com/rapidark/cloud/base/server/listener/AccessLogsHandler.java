package com.rapidark.cloud.base.server.listener;

import cn.hutool.core.util.IdUtil;
import com.rapidark.cloud.base.client.model.entity.GatewayAccessLogs;
import com.rapidark.cloud.base.server.repository.GatewayAccessLogsRepository;
import com.rapidark.cloud.base.server.service.IpRegionService;
import com.rapidark.common.constants.QueueConstants;
import com.rapidark.common.utils.BeanConvertUtils;
import com.rapidark.common.utils.SystemIdGenerator;
import com.rapidark.common.utils.UuidUtil;
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
    private IpRegionService ipRegionService;

    @Autowired
    private SystemIdGenerator systemIdGenerator;

    /**
     * 接收访问日志
     *
     * @param access
     */
    @RabbitListener(queues = QueueConstants.QUEUE_ACCESS_LOGS)
    public void accessLogsQueue(@Payload Map access) {
        try {
            if (access != null) {
                GatewayAccessLogs logs = BeanConvertUtils.mapToObject(access, GatewayAccessLogs.class);
                if (logs != null) {
                    if (logs.getIp() != null) {
                        logs.setRegion(ipRegionService.getRegion(logs.getIp()));
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
