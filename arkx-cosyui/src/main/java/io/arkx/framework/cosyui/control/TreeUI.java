package io.arkx.framework.cosyui.control;

import io.arkx.framework.Constant;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.annotation.Verify;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.control.tree.Tree;
import io.arkx.framework.cosyui.control.tree.TreeBodyManager;
import io.arkx.framework.cosyui.tag.RestUtil;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.security.PrivCheck;
import io.arkx.framework.security.VerifyCheck;

/**
 * 树标签服务器端响应UI类
 *
 */
public class TreeUI extends UIFacade {

	// NO_UCD
	@Priv(login = false)
	@Verify(ignoreAll = true)
	public void doWork(Mapx<String, String> params) {
		try {
			TreeAction ta = new TreeAction();
			String method = params.get(Constant.Method);
			ta.setMethod(method);
			String rest = params.get(Constant.Rest);
			ta.setRest(rest);

			if ("true".equals(params.get(Constant.TreeLazy))) {
				if (!"false".equals(params.get(Constant.TreeExpand))) {
					ta.setExpand(true);
				}
				ta.setLazy(true);
			}

			if (ObjectUtil.notEmpty(params.get("ParentLevel"))) {
				ta.setParentLevel(Request.getInt("ParentLevel"));
				ta.setLazyLoad(true);
			}

			if (ObjectUtil.notEmpty(params.get("ParentID"))) {
				ta.setParentID(params.get("ParentID"));
				ta.setLazyLoad(true);
			}

			if (params.get(Constant.TreeCheckbox) != null && !"".equals(params.get(Constant.TreeCheckbox))) {
				ta.setCheckbox(params.get(Constant.TreeCheckbox));
				if ("false".equals(params.get(Constant.TreeCascade))) {
					ta.setCascade(false);
				}
			}
			else if (params.get(Constant.TreeRadio) != null && !"".equals(params.get(Constant.TreeRadio))) {
				ta.setRadio(params.get(Constant.TreeRadio));
			}

			ta.setID(params.get(Constant.ID));
			ta.setParams(Request);

			int level = params.getInt(Constant.TreeLevel);
			String style = params.get(Constant.TreeStyle);
			if (level <= 0) {
				level = 999;
			}
			ta.setLevel(level);
			ta.setStyle(style);

			ta.setAjaxRequest(true);
			ta.setTagBody(TreeBodyManager.get(Request.getString(Constant.TagBody)));

			if (!StringUtil.isEmpty(rest)) {
				JsonResult jsonResult = RestUtil.post(rest, WebCurrent.getRequest(), Tree.class);
				Tree tree = (Tree) jsonResult.getData();

				String branchIcon = tree.getBranchIcon();
				if (!StringUtil.isEmpty(branchIcon)) {
					ta.setBranchIcon(branchIcon);
				}
				String leafIcon = tree.getLeafIcon();
				if (!StringUtil.isEmpty(leafIcon)) {
					ta.setLeafIcon(leafIcon);
				}

				String identifierColumnName = tree.getIdentifierColumnName();
				if (!StringUtil.isEmpty(identifierColumnName)) {
					ta.setIdentifierColumnName(identifierColumnName);
				}
				String parentIdentifierColumnName = tree.getParentIdentifierColumnName();
				if (!StringUtil.isEmpty(parentIdentifierColumnName)) {
					ta.setParentIdentifierColumnName(parentIdentifierColumnName);
				}
				String rootText = tree.getRootText();
				if (!StringUtil.isEmpty(rootText)) {
					ta.setRootText(rootText);
				}
				String rootIcon = tree.getRootIcon();
				if (!StringUtil.isEmpty(rootIcon)) {
					ta.setRootIcon(rootIcon);
				}

				ta.bindData(tree.getDataTable());
			}
			else {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				// 参数检查
				if (!VerifyCheck.check(m)) {
					String message = "Verify check failed:method=" + method + ",data=" + WebCurrent.getRequest();
					LogUtil.warn(message);
					WebCurrent.getResponse().setFailedMessage(message);
					return;
				}
				m.execute(ta);
			}

			ta.bindData();// 这是为了兼容旧的写法，setRootText()和setRootIcon()经常写在bindData()之后

			String html = ta.getResult();
			$S("HTML", html);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
