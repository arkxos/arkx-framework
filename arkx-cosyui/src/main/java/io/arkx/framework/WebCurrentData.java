package io.arkx.framework;

import io.arkx.framework.commons.util.Errorx;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.ResponseData;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.cosyui.web.mvc.Dispatcher;
import io.arkx.framework.cosyui.web.mvc.IURLHandler;

/**
 * @author Nobody
 * @date 2025-06-04 20:35
 * @since 1.0
 */
public class WebCurrentData extends CurrentData {

    public IURLHandler handler;
    public AbstractExecuteContext executeContext;
    public UIFacade facade;
    public IMethodLocator method;
    public Account.UserData userData;

    public RequestData request = new RequestData();
    public ResponseData response = new ResponseData();
    // public Transaction transaction = new Transaction();
    public Dispatcher dispatcher = new Dispatcher();

    public Errorx errorx = new Errorx();

    /**
     * 清空数据
     */
    @Override
    public void clear() {
        super.clear();

        handler = null;
        facade = null;
        method = null;
        executeContext = null;
        userData = null;
        // transaction = null;
        if (errorx != null) {
            Errorx.clear();
        }

        if (dispatcher != null) {
            dispatcher.clear();
        }
        if (request != null) {
            if (request.getEntryTableLength() < 64) {
                request.clear();
            } else {
                request = new RequestData();
            }
        }
        if (response != null) {
            if (response.getEntryTableLength() < 64) {
                response.clear();
            } else {
                response = new ResponseData();
            }
        }
    }
}
