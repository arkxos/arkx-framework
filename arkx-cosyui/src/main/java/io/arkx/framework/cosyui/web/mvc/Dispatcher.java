package io.arkx.framework.cosyui.web.mvc;

import io.arkx.framework.WebCurrent;

/**
 * 转发器，用于保存forward和redirect操作的目标URL
 *
 */
public class Dispatcher {

    protected String forwardURL;

    protected String redirectURL;

    public void clear() {
        forwardURL = null;
        redirectURL = null;
    }

    public static void forward(String url) {
        Dispatcher d = WebCurrent.getDispatcher();
        if (d == null) {
            return;
        }
        d.forwardURL = url;
        d.redirectURL = null;
        throw new DispatchException();
    }

    public static void redirect(String url) {
        Dispatcher d = WebCurrent.getDispatcher();
        if (d == null) {
            return;
        }
        d.forwardURL = null;
        d.redirectURL = url;
        throw new DispatchException();
    }

    /**
     * 用于触发跳转
     */
    public static class DispatchException extends Error {

        private static final long serialVersionUID = 1L;

    }

    /**
     * 用于终止处理
     */
    public static class HandleEndException extends Error {

        private static final long serialVersionUID = 1L;

    }

    public String getForwardURL() {
        return forwardURL;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

}
