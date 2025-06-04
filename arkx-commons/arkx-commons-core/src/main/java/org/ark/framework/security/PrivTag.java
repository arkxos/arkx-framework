package org.ark.framework.security;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.PlaceHolderContext;

import io.arkx.framework.Account;
import io.arkx.framework.Config;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.util.ObjectUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;


/**
 * @class org.ark.framework.security.PrivTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:25:22 
 * @version V1.0
 */
public class PrivTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	public static String PrivCheckAttr = "_PrivCheckFlag";
	private boolean login;
	private String loginType;
	private String userType;
	private String priv;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.login = true;
		this.loginType = "User";
		this.userType = "";
		this.priv = "";
	}

	public int doStartTag() throws JspException {
		try {
			HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
			Priv.LoginType lt = null;
			if ("User".equals(this.loginType))
				lt = Priv.LoginType.User;
			else {
				lt = Priv.LoginType.Member;
			}
			PlaceHolderContext context = PlaceHolderContext.getInstance(this, this.pageContext);
			if (!PrivCheck.check(this.login, lt, this.userType, this.priv, context)) {
				Current.setVariable(PrivCheckAttr, "false");
				if (Account.isLogin())
					response.sendRedirect(Config.getContextPath() + "NoPrivilege.zhtml?Priv=" + this.priv);
				else
					response.sendRedirect(Config.getContextPath() + Config.getLoginPage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int doEndTag() throws JspException {
		if (ObjectUtil.equal("false", Current.getVariable(PrivCheckAttr))) {
			return 5;
		}
		return 6;
	}

	public boolean isLogin() {
		return this.login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getLoginType() {
		return this.loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getUserType() {
		return this.userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPriv() {
		return this.priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}
}