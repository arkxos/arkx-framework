package com.xdreamaker.framework.ddd.es.continuance.producer.health;

import com.xdreamaker.framework.ddd.es.continuance.producer.jpa.CustomDomainEventEntry;
import com.xdreamaker.framework.ddd.es.continuance.producer.jpa.CustomDomainEventEntryRepository;
import com.xdreamaker.framework.ddd.stream.ContractPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author darkness
 * @date 2021/6/27 0:20
 * @version 1.0
 */
@Service
@Slf4j
@AllArgsConstructor
public class ScheduleService {

    private final CustomDomainEventEntryRepository customDomainEventEntryRepository;
    private final ContractPublisher contractPublisher;

    @Scheduled(cron = "0 0 12 * * ?")
    @SchedulerLock(name = "failedMessageDiscoveryTask")
    public void failedMessageDiscovery() {

        int page = 0;
        PageRequest request = PageRequest.of(page, 1000);

        Page<CustomDomainEventEntry> results = customDomainEventEntryRepository.findBySentFalse(request);
        log.warn(MessageFormat.format("发现 [{0}] 条失败消息，尝试重新发送", results.getTotalElements()));
        sendFailedMessage(results.getContent());
        while (results.hasNext()) {
            request = PageRequest.of(page + 1, 1000);
            results = customDomainEventEntryRepository.findBySentFalse(request);
            sendFailedMessage(results.getContent());
        }
        log.info("所有失败消息尝试发送完毕");
    }

    private void sendFailedMessage(Collection<CustomDomainEventEntry> failedEvents) {

        failedEvents.forEach(e -> {
            contractPublisher.sendEvent(e);
            e.setSent(true);
            customDomainEventEntryRepository.save(e);
        });
    }
}
