package io.arkx.framework.core;

import io.arkx.framework.extend.AbstractExtendService;

/**
 * Runtime异常捕获器扩展服务
 *
 */
public class ExceptionCatcherService extends AbstractExtendService<IExceptionCatcher> {
    public static ExceptionCatcherService getInstance() {
        return AbstractExtendService.findInstance(ExceptionCatcherService.class);
    }
}
