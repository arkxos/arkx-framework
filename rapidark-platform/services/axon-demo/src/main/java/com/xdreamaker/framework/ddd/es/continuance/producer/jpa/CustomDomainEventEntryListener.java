package com.xdreamaker.framework.ddd.es.continuance.producer.jpa;

import com.xdreamaker.framework.ddd.stream.ContractPublisher;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.PostPersist;
import java.util.concurrent.CompletableFuture;

/**
 * @author darkness
 * @date 2021/6/26 23:57
 * @version 1.0
 */
@Component
@Slf4j
public class CustomDomainEventEntryListener {
    
    private static CustomDomainEventEntryRepository customDomainEventEntryRepository;

    private static ContractPublisher contractPublisher;

    @Autowired
    public void init(CustomDomainEventEntryRepository customDomainEventEntryRepository, ContractPublisher contractPublisher) {
        this.customDomainEventEntryRepository = customDomainEventEntryRepository;
        this.contractPublisher = contractPublisher;
    }

    @PostPersist
    void onPersist(CustomDomainEventEntry entry) {

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    CompletableFuture.runAsync(() -> sendEvent(entry.getEventIdentifier()));
                }
            }
        });
    }

    @Transactional
    public void sendEvent(String identifier) {
        CustomDomainEventEntry eventEntry = customDomainEventEntryRepository.findByEventIdentifier(identifier);

        if (!eventEntry.isSent()) {
            contractPublisher.sendEvent(eventEntry);
            eventEntry.setSent(true);
            customDomainEventEntryRepository.save(eventEntry);
        }
    }
}
