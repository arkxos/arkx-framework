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

import io.arkx.framework.data.db.core.basic.task.TaskResult;

public abstract class RobotWriter<R extends TaskResult> extends AbstractRobot<R> {

    public abstract void startWrite();

    @Override
    public void startWork() {
        startWrite();
    }

    public abstract void waitForFinish();

}
