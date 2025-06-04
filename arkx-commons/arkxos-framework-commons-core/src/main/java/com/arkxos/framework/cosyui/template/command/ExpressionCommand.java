package com.arkxos.framework.cosyui.template.command;

import java.util.Date;

import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.config.ExpressionAutoEscaping;
import com.arkxos.framework.cosyui.expression.ExpressionException;
import com.arkxos.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.cosyui.template.AbstractTag;
import com.arkxos.framework.cosyui.template.ITemplateCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.i18n.LangUtil;

/**
 * 表达式命令
 * 
 */
public class ExpressionCommand implements ITemplateCommand {
	private String expr;
	private String source;
	private boolean autoEscaping = true;
	private boolean i18N = false;

	public ExpressionCommand(String str) {
		source = expr = str;
		if (expr.startsWith("@")) {// 国际化字符串
			i18N = true;
		}
		if (expr.startsWith("${(") && expr.endsWith(")}")) {
			autoEscaping = false;// 保持原样输出
			expr = "${" + expr.substring(3, expr.length() - 2) + "}";
		} else if (!ExpressionAutoEscaping.getValue()) {
			autoEscaping = false;
		}
	}

	@Override
	public int execute(AbstractExecuteContext context) throws TemplateRuntimeException {
		try {
			Object v = "";
			if (i18N) {
				v = LangUtil.get(expr, context.getLanguage());
			} else {
				v = context.evalExpression(expr);
				if (v == null) {
					v = "";
				} else {
					if (v instanceof String) {
						String str = LangUtil.get(v.toString(), context.getLanguage());
						if (autoEscaping) {
							v = StringUtil.quickHtmlEncode(str);
						} else {
							v = str;
						}
					} else if (v instanceof Date) {
						v = DateUtil.toDateTimeString((Date) v);
					}
				}
			}
			context.getOut().write(v);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new TemplateRuntimeException(e);
		}
		return AbstractTag.EVAL_PAGE;
	}

	public String getSource() {
		return source;
	}
}
