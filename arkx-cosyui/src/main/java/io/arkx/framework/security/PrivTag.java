package io.arkx.framework.security;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.annotation.Priv.LoginType;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.template.TagAttr;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限声明标签 ，用于在zhtml页面中声明需要的权限，如果当前用户不满声明的权限要求，则会重定向到权限不足页面。
 * 
 */
public class PrivTag extends ArkTag {
	private boolean login;
	private String loginType;
	private String userType;
	private String priv;

	@Override
	public void init() throws ExpressionException {
		login = true;
		loginType = "User";
		userType = "";
		priv = "";
		super.init();
	}

	@Override
	public int doStartTag() {
		LoginType lt = null;
		if ("User".equals(loginType)) {
			lt = LoginType.User;
		} else {
			lt = LoginType.Member;
		}
		PrivCheck.check(login, lt, userType, priv, pageContext);
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() {
		return EVAL_PAGE;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getPriv() {
		return priv;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	@Override
	public String getTagName() {
		return "priv";
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("login", TagAttr.BOOL_OPTIONS));
		Mapx<String, String> loginTypes = new Mapx<>();
		loginTypes.put("Member", "Member");
		loginTypes.put("User", "User");
		list.add(new TagAttr("loginType", loginTypes));
		list.add(new TagAttr("priv"));
		list.add(new TagAttr("userType"));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.PrivTag.Name}";
	}

	@Override
	public String getDescription() {
		return "@{Framework.PrivTag.Desc}";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

}
