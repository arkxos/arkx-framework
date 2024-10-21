package org.ark.framework.jaf;

import java.text.DecimalFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.ark.framework.jaf.tag.IListTag;
import org.ark.framework.security.PrivCheck;

import com.rapidark.framework.Account;
import com.rapidark.framework.Config;
import com.rapidark.framework.Member;
import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.DateUtil;
import com.rapidark.framework.commons.util.NumberUtil;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.cosyui.web.CookieData;
import com.rapidark.framework.cosyui.web.RequestData;
import com.rapidark.framework.cosyui.web.ResponseData;

/**
 * @class org.ark.framework.jaf.PlaceHolderContext
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:58:03 
 * @version V1.0
 */
public class PlaceHolderContext implements IExpressionContext {
	
	private HttpServletRequest request;
	private Tag tag;
	private CookieData cookies;
	private Mapx<String, Mapx<String, Object>> allMap = new Mapx<>();

	public static PlaceHolderContext getInstance(Tag tag, PageContext pageContext) {
		return getInstance(tag, (HttpServletRequest) pageContext.getRequest());
	}

	public static PlaceHolderContext getInstance(Tag tag, HttpServletRequest request) {
		if (Current.getPlaceHolderContext() != null) {
			PlaceHolderContext context = Current.getPlaceHolderContext();
			if (context.tag == tag) {
				return context;
			}
			PlaceHolderContext context2 = new PlaceHolderContext();
			context2.request = context.request;
			context2.tag = tag;
			context2.cookies = context.cookies;
			return context2;
		}
		PlaceHolderContext context = new PlaceHolderContext();
		context.request = request;
		context.tag = tag;

		context.cookies = Current.getCookies();
		if (context.cookies == null) {
			context.cookies = new CookieData(request);
		}
		Current.setVariable("_ARK_PLACEHOLDERCONTEXT_KEY", context);
		return context;
	}

	public PlaceHolderContext() {
		this.cookies = new CookieData();
	}

	public Object eval(PlaceHolder holder) {
		Mapx map = null;
		Object v = null;

		RequestData rMap = Current.getRequest();
		if (rMap == null) {
			rMap = Current.initRequest(this.request);
		}
		this.allMap.put("Header", rMap.getHeaders());

		if ("Current".equals(holder.getPrefix())) {
			v = Current.getVariable(holder.getVarName());
		}

		if ((v == null) && ("Request".equals(holder.getPrefix()))) {
			if (holder.getVarName().equals("QueryString"))
				v = rMap.getQueryString();
			else if (holder.getVarName().equals("URL"))
				v = rMap.getURL();
			else if (holder.getVarName().equals("ClientIP"))
				v = rMap.getClientIP();
			else if (holder.getVarName().equals("ServerName"))
				v = rMap.getServerName();
			else if (holder.getVarName().equals("Port"))
				v = new Integer(rMap.getPort());
			else if (holder.getVarName().equals("Scheme"))
				v = rMap.getScheme();
			else {
				v = rMap.get(holder.getVarName());
			}

		}

		if ((v == null) && ("Response".equals(holder.getPrefix()))) {
			ResponseData response = Current.getResponse();
			if (response != null) {
				v = response.get(holder.getVarName());
			}

		}

		if ((v == null) && ("Cookie".equals(holder.getPrefix()))) {
			v = this.cookies.getCookie(holder.getVarName());
		}

		if ((v == null) && ("Config".equals(holder.getPrefix()))) {
			v = Config.getValue(holder.getVarName());
		}

		if ((v == null) && ("User".equals(holder.getPrefix()))) {
			if (holder.getVarName().equals("Login"))
				v = Boolean.valueOf(Account.isLogin());
			else if (holder.getVarName().equals("UserName"))
				v = Account.getUserName();
			else if (holder.getVarName().equals("BranchInnerCode"))
				v = Account.getBranchInnerCode();
			else if (holder.getVarName().equals("isBranchAdministrator"))
				v = Boolean.valueOf(Account.isBranchAdministrator());
			else if (holder.getVarName().equals("SessionID"))
				v = Account.getSessionID();
			else if (holder.getVarName().equals("RealName"))
				v = Account.getRealName();
			else if (holder.getVarName().equals("Type"))
				v = Account.getType();
			else if (holder.getVarName().equals("Language"))
				v = Account.getLanguage();
			else {
				v = Account.getValue(holder.getVarName());
			}

		}

		if ((v == null) && ("Member".equals(holder.getPrefix()))) {
			if (holder.getVarName().equals("Login"))
				v = Boolean.valueOf(Member.isLogin());
			else if (holder.getVarName().equals("UserName"))
				v = Member.getUserName();
			else if (holder.getVarName().equals("RealName"))
				v = Member.getRealName();
			else if (holder.getVarName().equals("Type"))
				v = Member.getType();
			else if (holder.getVarName().equals("Language"))
				v = Account.getLanguage();
			else {
				v = Member.getValue(holder.getVarName());
			}
		}

		if ((v == null) && ("System".equals(holder.getPrefix()))) {
			if (holder.getVarName().equals("CurrentTime"))
				v = new Date();
			else {
				v = Config.getValue(holder.getPrefix() + "." + holder.getVarName());
			}
		}

		if ((v == null) && ("List".equals(holder.getPrefix()))) {
			String var = holder.getVarName();
			if (this.tag != null) {
				Tag parent = this.tag.getParent();
				while (parent != null) {
					if ((parent instanceof IListTag)) {
						if (!var.startsWith("Parent.")) {
							DataRow dr = ((IListTag) parent).getCurrentDataRow();
							v = dr.get(var);
							break;
						}
						var = var.substring(7);
					}

					parent = parent.getParent();
				}
			}
		}

		if ((v == null) && ("Priv".equals(holder.getPrefix()))) {
			v = Boolean.valueOf(PrivCheck.check(this, holder.getVarName()));
		}

		if (v == null) {
			map = this.allMap.get(holder.getPrefix());
			if (map != null) {
				v = map.get(holder.getVarName());
			}
		}

		if (v == null) {
			String var = holder.getVarName();
			if (this.tag != null) {
				Tag parent = this.tag.getParent();
				while (parent != null) {
					if ((parent instanceof IListTag)) {
						if (!var.startsWith("Parent.")) {
							DataRow dr = ((IListTag) parent).getCurrentDataRow();
							v = dr.get(var);
							break;
						}
						var = var.substring(7);
					}

					parent = parent.getParent();
				}
			}

		}

		String k = null;
		if (ObjectUtil.empty(holder.getPrefix()))
			k = holder.getVarName();
		else {
			k = holder.getPrefix() + "." + holder.getVarName();
		}
		if ((v == null) && (map != null)) {
			v = map.get(k);
		}

		if (v == null) {
			v = Current.getVariable(k);
		}

		if (v == null) {
			ResponseData response = Current.getResponse();
			if (response != null) {
				v = response.get(k);
			}
		}
		if (v == null) {
			v = rMap.getString(k);
		}

		return applyModifier(holder, v);
	}

	public static Object applyModifier(PlaceHolder holder, Object v) {
		if (StringUtil.isNotEmpty(holder.getFormat())) {
			if ((v instanceof Number)) {
				DecimalFormat df = new DecimalFormat(holder.getFormat());
				v = df.format(((Number) v).doubleValue());
			} else if ((v instanceof Date)) {
				v = DateUtil.toString((Date) v, holder.getFormat());
			} else if (NumberUtil.isNumber(String.valueOf(v))) {
				DecimalFormat df = new DecimalFormat(holder.getFormat());
				v = df.format(Double.parseDouble(String.valueOf(v)));
			} else if (DateUtil.isDateTime(String.valueOf(v))) {
				v = DateUtil.toString(DateUtil.parseDateTime(String.valueOf(v)), holder.getFormat());
			}
		}
		if ((StringUtil.isNotEmpty(holder.getCharWidth())) && (StringUtil.isDigit(holder.getCharWidth()))) {
			int charWidth = Integer.parseInt(holder.getCharWidth());
			if (StringUtil.lengthEx(v.toString()) > charWidth * 2) {
				return StringUtil.subStringEx(v.toString(), charWidth - 1) + "...";
			}
		}

		return v;
	}

	public void addMap(Mapx<String, Object> map, String prefix) {
		this.allMap.put(prefix, map);
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}