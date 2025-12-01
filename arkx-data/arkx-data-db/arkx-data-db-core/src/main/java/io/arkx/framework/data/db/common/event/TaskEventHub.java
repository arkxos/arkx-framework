// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.common.event;

import com.google.common.collect.Lists;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class TaskEventHub {

  private String identifier;
  private ExecutorService executor;
  private List<EventSubscriber> eventSubscribers;
  private ExceptionHandler exceptionHandler;

  public TaskEventHub(String identifier, int poolSize, ExceptionHandler handler) {
    this.identifier = Objects.requireNonNull(identifier, "identifier must not be null");
    this.executor = new ThreadPoolExecutor(
        poolSize,
        poolSize,
        0L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(),
        new BasicThreadFactory.Builder()
            .namingPattern(this.identifier + "-%d")
            .build()
    );
    this.eventSubscribers = new CopyOnWriteArrayList<>();
    this.exceptionHandler = handler;
  }

  public void registerSubscriber(EventSubscriber subscriber) {
    this.eventSubscribers.add(subscriber);
  }

  public void notifyEvent(@Nullable Object... args) {
    ListenedEvent event = new ListenedEvent(this, identifier, args);
    for (EventSubscriber subscriber : Lists.newArrayList(eventSubscribers)) {
      this.executor.submit(() -> {
        try {
          subscriber.handleEvent(event);
        } catch (Throwable e) {
          exceptionHandler.handleException(event, e);
        }
      });
    }
  }
}
