package com.arkxos.framework.security;

import com.arkxos.framework.Account;
import com.arkxos.framework.Config;
import com.arkxos.framework.Current;
import com.arkxos.framework.Member;
import com.arkxos.framework.annotation.Priv;
import com.arkxos.framework.annotation.Priv.LoginType;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.core.FrameworkException;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.cosyui.template.AbstractExecuteContext;
import com.arkxos.framework.extend.ExtendManager;
import com.arkxos.framework.extend.action.PrivExtendAction;
import com.arkxos.framework.security.exception.MemberNotLoginException;
import com.arkxos.framework.security.exception.NoPrivException;
import com.arkxos.framework.security.exception.UserNotLoginException;
import com.arkxos.framework.thirdparty.commons.ArrayUtils;

/**
 * 权限检查类，用于检查对当前页面、当前UI方法的访问是否被允许
 * 
 */
public class PrivCheck {

	public static void check(IMethodLocator m) {
		if (m == null) {
			return;
		}
		// 从注解中获取权限信息
		boolean flag = m.isAnnotationPresent(Priv.class);
		if (flag) {
			String parentPriv = "";
			if(m.getMethod().getDeclaringClass().isAnnotationPresent(Priv.class)) {
				parentPriv = m.getMethod().getDeclaringClass().getAnnotation(Priv.class).value();
			}
			
			Priv methodPriv = m.getAnnotation(Priv.class);
			String childPriv = methodPriv.value();
			
			String privVlaue = "";
			if(!StringUtil.isEmpty(parentPriv) && !StringUtil.isEmpty(childPriv)) {
				privVlaue = parentPriv + "." + childPriv;
			} else if (!StringUtil.isEmpty(parentPriv)){
				privVlaue = parentPriv;
			} else {
				privVlaue = childPriv;
			}
			
			check(methodPriv.login(), methodPriv.loginType(), methodPriv.userType(), privVlaue, Current.getExecuteContext());
		} else {// 必须有@Priv注解，否则禁止访问
			throw new NoPrivException("Method hasn't @Priv annotation");
		}
	}

	public static void check(boolean login, LoginType loginType, String userType, String priv, AbstractExecuteContext context) {
		if (!login) {// 不需要登录
			return;
		}
		if (login) {// 要求登录
			if (Config.isFrontDeploy() && Account.isLogin()) {
				throw new FrameworkException("User cann't login in Front Deploy mode!");
			}
			if (loginType == LoginType.User && !Account.isLogin()) {
				throw new UserNotLoginException("User isn't logined");
			}
			if (loginType == LoginType.Member && !Member.isLogin()) {
				throw new MemberNotLoginException("Member isn't logined");
			}

			if (ObjectUtil.notEmpty(userType)) {
				if (loginType == LoginType.User && ObjectUtil.notEqual(Account.getType(), userType)) {
					throw new NoPrivException("User.Type is not applicable:" + userType);
				}

				if (loginType == LoginType.Member && ObjectUtil.notEqual(Member.getType(), userType)) {
					throw new NoPrivException("Member.Type is not applicable:" + userType);
				}
			}
			if(!StringUtil.isEmpty(priv)) {
				if (!check(context, priv)) {
					throw new NoPrivException("Privilege " + priv + " is not owned by current user.");
				}
			}
		}
	}

	/**
	 * 用默认的占位符上下文来校验
	 */
	public static boolean check(String priv) {
		return check(Current.getExecuteContext(), priv);
	}

	public static void assertPriv(String priv) {
		assertPriv(priv, "Privilege " + priv + " is not owned by current user.");
	}

	public static void assertPriv(String priv, String errorMessage) {
		if (!check(priv)) {
			throw new NoPrivException(errorMessage);
		}
	}

	/**
	 * 校验当前用户是否拥有相应权限，校验前会先替换环境变量
	 */
	public static boolean check(AbstractExecuteContext context, String priv) {
		if (ObjectUtil.isEmpty(priv)) {
			return true;
		}
		for (String item : StringUtil.splitEx(priv, Privilege.Or)) {
			item = item.trim();
			if (ObjectUtil.isEmpty(item)) {
				continue;
			}
			if (context != null && item.indexOf("${") >= 0) {
				item = context.eval(item);

			}
			boolean allowFlag = false;
			// 表达式存在逗号，则分组验证Ids权限权限，结果为分组相与
			if (item.indexOf(",") > 0) {
				String ids = item.substring(item.lastIndexOf(".") + 1);
				String pre_item = item.substring(0, item.lastIndexOf(".") + 1);
				Object[] arr = new Object[0];
				for (String id : StringUtil.splitEx(ids, ",")) {
					arr = ArrayUtils.addAll(arr, ExtendManager.invoke(PrivExtendAction.ExtendPointID, new Object[] { pre_item + id }));
				}
				if (arr != null && arr.length > 0) {
					allowFlag = true;
					for (Object obj : arr) {
						Integer flag = (Integer) obj;
						if (flag == Privilege.Flag_NotSet) {
							allowFlag = false;
							break;
						}
					}
				}
			} else {
				Object[] arr = ExtendManager.invoke(PrivExtendAction.ExtendPointID, new Object[] { item });
				if (arr != null) {
					for (Object obj : arr) {
						Integer flag = (Integer) obj;
						if (flag == Privilege.Flag_Allow) {
							allowFlag = true;
							break;
						}
					}
				}
			}

			if (allowFlag) {
				return true;// 有一个权限项满足了，就整体返回true
			}
		}
		return false;
	}

}
