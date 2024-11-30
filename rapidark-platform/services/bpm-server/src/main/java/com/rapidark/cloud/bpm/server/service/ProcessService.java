package com.rapidark.cloud.bpm.server.service;

import com.rapidark.cloud.bpm.client.model.TaskOperate;

/**
 * 自定义流程接口
 *
 * @author: liuyadu
 * @date: 2019/4/4 13:54
 * @description:
 */
public interface ProcessService {
    /**
     * 执行日志
     *
     * @param taskOperate
     */
    void complete(TaskOperate taskOperate);
}
