package org.ark.framework.script;

import io.arkx.framework.cosyui.web.UIFacade;

/**
 * @class org.ark.framework.script.CheckScript
 * @author Darkness
 * @date 2013-1-31 下午12:22:34
 * @version V1.0
 */
public class CheckScript extends UIFacade {

    public void check() {
        String script = $V("Script");
        String lang = $V("Language");
        ScriptEngine se = new ScriptEngine(lang.equalsIgnoreCase("java") ? 1 : 0);
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
