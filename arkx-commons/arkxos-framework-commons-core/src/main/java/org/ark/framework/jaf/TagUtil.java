package org.ark.framework.jaf;

import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;

import jakarta.servlet.jsp.PageContext;

/**
 * @class org.ark.framework.jaf.TagUtil
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:58:13
 * @version V1.0
 */
public class TagUtil {

	private static final String PageContextAttribte_TagID = "ARK_TAGID_";

	public static String getTagID(PageContext pageContext, String prefix) {
		if (ObjectUtil.empty(pageContext.getAttribute(PageContextAttribte_TagID))) {
			pageContext.setAttribute(PageContextAttribte_TagID, 0);
		}
		int tagid = Integer.valueOf(pageContext.getAttribute(PageContextAttribte_TagID).toString()).intValue();
		tagid++;
		pageContext.setAttribute(PageContextAttribte_TagID, tagid);
		return (StringUtil.isEmpty(prefix) ? PageContextAttribte_TagID : prefix) + tagid;
	}
	
}
