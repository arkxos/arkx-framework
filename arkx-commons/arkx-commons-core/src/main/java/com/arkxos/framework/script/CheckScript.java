package com.arkxos.framework.script;

import io.arkx.framework.cosyui.web.UIFacade;

public class CheckScript extends UIFacade {
	public void check(String script, String language) {
		ScriptEngine se = new ScriptEngine(language.equalsIgnoreCase("java") ? 1 : 0);
		try {
			se.compileFunction("Test", script);
		} catch (EvalException ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("Row ");
			sb.append(ex.getRowNo());
			sb.append(" exists error:<br><font color='red'>");
			sb.append(ex.getLineSource());
			sb.append("</font>");
			this.Response.setFailedMessage(sb.toString());
			return;
		}
		this.Response.setStatus(1);
	}
}
