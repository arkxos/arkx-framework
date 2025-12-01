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

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Async event bus that will run each event/handler call in separate thread.
 * By default using CachedThreadPool to run handlers.
 */
public class EventBusAsync<E extends Event> implements EventBus<E> {

    private static final Logger logger = LoggerFactory.getLogger(EventBusAsync.class);

    private Thread eventQueueThread;

    private final Queue<E> eventsQueue = new ConcurrentLinkedQueue<>();

//    private final ReferenceQueue gcQueue = new ReferenceQueue<>();

    private final Set<MesssageHandler<E>> handlers = Collections.newSetFromMap(new ConcurrentHashMap<MesssageHandler<E>, Boolean>());

    private final ExecutorService handlersExecutor;
    
    private boolean isCloseOnFinish;
    private FinishHandler finishHandler;

    /**
     * Create new EventBus instance with default presets.
     */
    public EventBusAsync() {
        this(Executors.newFixedThreadPool(4));
    }

    /**
     * Create instance with customer ExecutorService for event handlers.
     *
     * @param handlersExecutor Will be used to run event handler processing for each event
     */
    public EventBusAsync(ExecutorService handlersExecutor) {
        this.handlersExecutor = handlersExecutor;
    }

    public void start() {
    	eventQueueThread = new Thread(this::eventsQueue, "EventQueue handlers thread");
        eventQueueThread.setDaemon(false);
        eventQueueThread.start();
    }
    
    @Override
    public void subscribe(MesssageHandler<E> subscriber) {
        handlers.add(subscriber);//new WeakHandler(subscriber, gcQueue));
    }

    @Override
    public void unsubscribe(MesssageHandler<E> subscriber) {
        handlers.remove(subscriber);//new WeakHandler(subscriber, gcQueue));
    }

    @Override
    public void publish(E event) {
        if (event == null) {
            return;
        }
        event.lock();
        eventsQueue.add(event);
    }

    @Override
    public boolean hasPendingEvents() {
        return !eventsQueue.isEmpty();
    }

    private void eventsQueue() {
        while (needRun()) {
//        	System.out.println("need run");
        	
//            WeakHandler wh;
//            while ((wh = (WeakHandler)gcQueue.poll()) != null) {
//                handlers.remove(wh);
//            }

            E event = eventsQueue.poll();
            if (event != null) {
                notifySubscribers(event);
            }
        }
        
        System.out.println("close");
        
        close();
        eventQueueThread.interrupt();
    }

    private void notifySubscribers(E event) {
        for (MesssageHandler<E> eh : handlers) {
//            EventHandler eh = wh.get();
            if (eh == null) {
                continue;
            }
//            System.out.println(event);
            try {
                if (eh.getType() == null) {
                    if (eh.canHandle(event.getType())) {
//                    	System.out.println("can");
                        handlersExecutor.submit(() -> {
                            runHandler(eh, event);
                        });
                    }
                } else if (eh.getType().equals(event.getType())) {
                    handlersExecutor.submit(() -> {
                        runHandler(eh, event);
                    });
                }
            } catch (Throwable th) {
                logger.error("Handler notify fail on event " + event.getType() + ". " + th.getMessage(), th);
            }
        }
    }

    private void runHandler(MesssageHandler eh, E event) {
        try {
            eh.handle(event);
        } catch (Throwable th) {
            logger.error("Handler fail on event " + event.getType() + ". " + th.getMessage(), th);
        }
    }

    @Override
	public EventBus<E> closeOnFinish(FinishHandler handler) {
		this.isCloseOnFinish = true;
		this.finishHandler = handler;
		return this;
	}
	
	private boolean needRun() {
		if(!isCloseOnFinish) {
			return true;
		}
		
		if(hasPendingEvents()) {
			return true;
		}
		
		if(((ThreadPoolExecutor)handlersExecutor).getActiveCount() > 0) {
			return true;
		}
		
		return false;
	}
	
	private void close() {
		if(finishHandler != null) {
			finishHandler.onFinish();
		}
		handlersExecutor.shutdown();
	}
}
