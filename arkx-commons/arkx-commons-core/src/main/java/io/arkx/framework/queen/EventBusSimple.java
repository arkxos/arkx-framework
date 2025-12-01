/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Vladislav Zablotsky
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. 
 */
package io.arkx.framework.queen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple event bus with no background threads.
 * All consumers will be called directly during event publishing.
 * You can use it in a cases where event publishing is rare
 * or if there is requirement to use as less threads as possible.
 */
public class EventBusSimple<E extends Event> implements EventBus<E> {

    private static final Logger logger = LoggerFactory.getLogger(EventBusSimple.class);

    private final ReferenceQueue gcQueue = new ReferenceQueue();

    private final AtomicInteger processing = new AtomicInteger();

    private final Set<MesssageHandler> handlers = Collections.newSetFromMap(new ConcurrentHashMap<MesssageHandler, Boolean>());

    private boolean isCloseOnFinish;
    private FinishHandler finishHandler;
    
    @Override
    public void subscribe(MesssageHandler<E> subscriber) {
        handlers.add(subscriber);
    }

    @Override
    public void unsubscribe(MesssageHandler<E> subscriber) {
        handlers.remove(subscriber);
    }

    @Override
    public void publish(E event) {
        if (event == null) {
            return;
        }
        processing.incrementAndGet();
        try {
            event.lock();
            processEvent(event);
        } finally {
            processing.decrementAndGet();
        }
    }

    @Override
    public boolean hasPendingEvents() {
        return processing.get() > 0;
    }

    private void processEvent(E event) {
//    	MesssageHandler wh;
//        while ((wh = (WeakHandler)gcQueue.poll()) != null) {
//            handlers.remove(wh);
//        }
        if (event != null) {
            notifySubscribers(event);
        }
    }

    private void notifySubscribers(E event) {
        for (MesssageHandler eh : handlers) {
//            MesssageHandler eh = wh.get();
            if (eh == null) {
                continue;
            }

            try {
                if (eh.getType() == null) {
                    if (eh.canHandle(event.getType())) {
                        eh.handle(event);
                    }
                } else if (eh.getType().equals(event.getType())) {
                    eh.handle(event);
                }
            } catch (Throwable th) {
                logger.error("Handler fail on event " + event.getType() + ". " + th.getMessage(), th);
            }
        }
    }

	@Override
	public EventBus<E> closeOnFinish(FinishHandler handler) {
		this.isCloseOnFinish = true;
		this.finishHandler = handler;
		return this;
	}
	
	private void checkClose() {
		if(!isCloseOnFinish) {
			return;
		}
		
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
}
