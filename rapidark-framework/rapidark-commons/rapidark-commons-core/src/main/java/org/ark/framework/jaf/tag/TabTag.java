package org.ark.framework.jaf.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

import org.ark.framework.jaf.controls.Tab;


/**
 * @class org.ark.framework.jaf.tag.TabTag
 * 
 * <h2>Tab标签</h2>
 * <br/><h2>{@link org.ark.framework.jaf.tag.ChildTabTag 子选项卡}<h2> 
 * <br/><img src="images/TabTag_1.png"/>
 * <br/>&lt;ark:tab>
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;	&lt;ark:childtab id="ApplicationInfo" src="ApplicationInfo.zhtml" selected="true" afterClick="check()">
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		&lt;img src='../../Icons/icon018a1.png' />
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;		&lt;b>基本信息&lt;/b>
 * <br/>&nbsp;&nbsp;&nbsp;&nbsp;	&lt;/ark:childtab>
 * <br/>&lt;/ark:tab>
 * <br/>
 * <br/>TabPage.setSrc("ApplicationInfo", "AppModuleInfo.zhtml?id=" + id);
 * @author Darkness
 * @date 2012-11-19 下午03:08:18 
 * @version V1.0
 */
public class TabTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	private boolean lazy;
	private String height;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		
		Tab.init();
		
		this.lazy = true;
		height = null;
	}

	public int doAfterBody() throws JspException {
		
		String content = getBodyContent().getString();
		
		Tab tab  = new Tab(content);
		tab.setLazy(lazy);
		tab.setHeight(height);
		String html = tab.getHtml();
		
		try {
			getPreviousOut().print(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
}