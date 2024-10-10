package com.xdreamaker.framework.ddd.es.continuance.producer.health;

import com.xdreamaker.framework.ddd.es.continuance.producer.jpa.CustomDomainEventEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * @author darkness
 * @date 2021/6/27 0:19
 * @version 1.0
 */
@Component
@AllArgsConstructor
public class EventHealthContributor implements InfoContributor {

    private final CustomDomainEventEntryRepository customDomainEventEntryRepository;

    @Override
    public void contribute(Info.Builder builder) {
        Long count = customDomainEventEntryRepository.countBySentFalse();

        builder.withDetail("failedMessage", count);
    }
}
