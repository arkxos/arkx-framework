package org.ark.framework.jaf.controls;

import java.lang.reflect.Method;

import org.ark.framework.jaf.Current;
import org.ark.framework.security.PrivCheck;
import org.ark.framework.security.VerifyCheck;

import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.web.UIFacade;


/**
 * @class org.ark.framework.jaf.controls.DataListUI
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:43:08 
 * @version V1.0
 */
public class DataListUI extends UIFacade {
	@Verify(ignoreAll = true)
	@Priv(login = false)
	public void doWork() {
		try {
			DataListAction dla = new DataListAction();

			dla.setTagBody(StringUtil.htmlDecode($V("_ARK_TAGBODY")));
			dla.setPage("true".equalsIgnoreCase($V("_ARK_PAGE")));
			String method = $V("_ARK_METHOD");
			dla.setMethod(method);
			dla.setID($V("_ARK_ID"));
			dla.setSortEnd($V("_ARK_SORTEND"));
			dla.setDragHandle($V("_ARK_DRAGHANDLE"));
			dla.setParams(this.Request);
			dla.setPageSize(Integer.parseInt($V("_ARK_SIZE")));
			if (dla.getPageSize() > 10000) {
				dla.setPageSize(10000);
			}
			if (dla.isPage()) {
				dla.setPageIndex(0);
				if ((this.Request.get("_ARK_PAGEINDEX") != null) && (!this.Request.get("_ARK_PAGEINDEX").equals(""))) {
					dla.setPageIndex(Integer.parseInt(this.Request.get("_ARK_PAGEINDEX").toString()));
				}
				if (dla.getPageIndex() < 0) {
					dla.setPageIndex(0);
				}
				if ((this.Request.get("_ARK_PAGETOTAL") != null) && (!this.Request.get("_ARK_PAGETOTAL").equals(""))) {
					dla.setTotal(Integer.parseInt(this.Request.get("_ARK_PAGETOTAL").toString()));
					if (dla.getPageIndex() > Math.ceil(dla.getTotal() * 1.0D / dla.getPageSize())) {
						dla.setPageIndex(new Double(Math.floor(dla.getTotal() * 1.0D / dla.getPageSize())).intValue());
					}
				}
			}

			Method m = Current.findMethod(method, new Class[] { DataListAction.class });
			if (!PrivCheck.check(m, this.Request, this.Response)) {
				return;
			}

			if (!VerifyCheck.check(m)) {
				String message = "Verify check failed:method=" + method + ",data=" + Current.getRequest();
				LogUtil.warn(message);
				Current.getResponse().setFailedMessage(message);
				return;
			}
			Current.invokeMethod(m, new Object[] { dla });
			$S("HTML", dla.getHtml());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}