package io.arkx.framework.cosyui.template.exception;

import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.i18n.Lang;

/**
 * 模板运行时异常
 *
 */
public class TemplateRuntimeException extends TemplateException {

	private static final long serialVersionUID = 1L;

	public TemplateRuntimeException(String message) {
		super(message);
	}

	public TemplateRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateRuntimeException(Throwable cause) {
		super(cause);
	}

	public TemplateRuntimeException(String message, AbstractTag tag) {
		super((tag == null ? "" : tag.getUrlFile() + Lang.get("Staticize.ErrorOnLine", tag.getStartLineNo()))
				+ message);
	}

}
