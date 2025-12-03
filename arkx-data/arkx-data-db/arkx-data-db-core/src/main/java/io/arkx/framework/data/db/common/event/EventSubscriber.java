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

import java.util.function.Consumer;

import com.google.common.eventbus.Subscribe;

public class EventSubscriber {

    private Consumer<ListenedEvent> handler;

    public EventSubscriber(Consumer<ListenedEvent> handler) {
        this.handler = handler;
    }

    @Subscribe
    public void handleEvent(ListenedEvent event) {
        handler.accept(event);
    }

}
