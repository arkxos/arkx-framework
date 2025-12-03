package io.arkx.framework.cosyui;

import io.arkx.framework.cosyui.template.exception.TemplateException;

/**
 * UI异常
 *
 */
public class UIException extends TemplateException {

    private static final long serialVersionUID = 1L;

    public UIException(String message) {
        super(message);
    }

}
