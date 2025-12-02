package org.ark.framework.jaf.controls;

import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.html.HtmlP;
import org.ark.framework.security.PrivCheck;
import org.ark.framework.security.VerifyCheck;

import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.web.UIFacade;

/**
 * @class org.ark.framework.jaf.controls.TreeUI
 *
 * @author Darkness
 * @date 2013-1-31 下午12:45:09
 * @version V1.0
 */
public class TreeUI extends UIFacade {
    @Priv(login = false)
    @Verify(ignoreAll = true)
    public void doWork() {
        try {
            TreeAction ta = new TreeAction();

            ta.setTagBody(StringUtil.htmlDecode($V("_ARK_TAGBODY")));
            String method = $V("_ARK_METHOD");
            ta.setMethod(method);

            if ("true".equals($V("_ARK_TREE_LAZY"))) {
                if (!"false".equals($V("_ARK_TREE_EXPAND"))) {
                    ta.setExpand(true);
                }
                ta.setLazy(true);
            }

            if (($V("ParentLevel") != null) && (!"".equals($V("ParentLevel")))) {
                ta.setParentLevel(Integer.parseInt($V("ParentLevel")));
                ta.setLazyLoad(true);
            }

            ta.setID($V("_ARK_ID"));
            ta.setParams(this.Request);

            String levelStr = $V("_ARK_TREE_LEVEL");
            String style = $V("_ARK_TREE_STYLE");
            if (ObjectUtil.empty(levelStr)) {
                levelStr = "0";
            }

            int level = Integer.parseInt(levelStr);
            if (level <= 0) {
                level = 999;
            }
            ta.setLevel(level);
            ta.setStyle(style);

            HtmlP p = new HtmlP();
            p.parseHtml(ta.getTagBody());
            ta.setTemplate(p);

            Method m = Current.findMethod(method, new Class[]{TreeAction.class});
            if (!PrivCheck.check(m, this.Request, this.Response)) {
                return;
            }

            if (!VerifyCheck.check(m)) {
                String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
                LogUtil.warn(message);
                Current.getResponse().setFailedMessage(message);
                return;
            }
            Current.invokeMethod(m, new Object[]{ta});

            $S("HTML", ta.getHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
