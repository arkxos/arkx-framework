package io.arkx.framework.core.method;

import io.arkx.framework.cosyui.web.UIFacade;

/**
 * UI方法类，前台的一个URL请求或者一次UI控件中的服务器调用
 */
public abstract class UIMethod extends UIFacade {

    /**
     * 执行响应逻辑
     */
    public abstract void execute();

}
