// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.basic.robot;

import io.arkx.framework.data.db.core.basic.exchange.MemChannel;
import io.arkx.framework.data.db.core.basic.task.TaskResult;

import java.util.Optional;

public abstract class AbstractRobot<R extends TaskResult> implements Robot {

  private MemChannel channel;

  public void setChannel(MemChannel channel) {
    this.channel = channel;
  }

  public MemChannel getChannel() {
    return this.channel;
  }

  public void clearChannel() {
    this.channel.clear();
  }

  public abstract Optional<R> getWorkResult();
}
