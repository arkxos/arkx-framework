package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.core.JsonResult;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.UIException;
import com.arkxos.framework.cosyui.control.tree.Tree;
import com.arkxos.framework.cosyui.control.tree.TreeBody;
import com.arkxos.framework.cosyui.control.tree.TreeBodyManager;
import com.arkxos.framework.cosyui.expression.ExpressionException;
import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.tag.RestUtil;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.TemplateExecutor;
import com.arkxos.framework.cosyui.template.command.TagCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.exception.PrivException;
import com.rapidark.framework.Config;
import com.rapidark.framework.Current;
import com.rapidark.framework.FrameworkPlugin;

/**
 * 树标签　
 * 
 */
public class TreeTag extends ArkTag {
	private String id;

	private String method;
	private String rest;

	private String style;

	private boolean lazy;

	private boolean customscrollbar;

	private String checkbox;// 可能的值 all/branch/leaf

	private boolean cascade = true;// 是否级联

	private String radio;

	private boolean expand; // 延迟加载时全部展开

	private int level;

	private String bodyUID;

	@Override
	public String getTagName() {
		return "tree";
	}

	@Override
	public void init() throws ExpressionException {
		customscrollbar = true;
		super.init();
	}

	private TreeAction prepareAction() {
		TreeAction ta = new TreeAction();
		ta.setMethod(method);
		ta.setRest(rest);

		ta.setID(id);
		ta.setLazy(lazy);
		ta.setCustomscrollbar(customscrollbar);
		ta.setCheckbox(checkbox);
		ta.setCascade(cascade);
		ta.setRadio(radio);
		ta.setExpand(expand);
		if (level <= 0) {
			level = 999;
		}
		ta.setLevel(level);
		ta.setStyle(style);

		String content = getTagSource();
		ta.setTagBody(TreeBodyManager.get(ta, bodyUID, content));
		return ta;
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		try {
			if (StringUtil.isEmpty(method) && StringUtil.isEmpty(rest)) {
				throw new UIException("Tree's method or rest can't be empty");
			}

			TreeAction ta = prepareAction();
			ta.setParams(Current.getRequest());
			
			if(StringUtil.isNotEmpty(rest)) {
				JsonResult jsonResult = RestUtil.post(rest, Current.getRequest(), Tree.class);
				Tree tree = (Tree)jsonResult.getData();
				
				String branchIcon = tree.getBranchIcon();
				if(!StringUtil.isEmpty(branchIcon)) {
					ta.setBranchIcon(branchIcon);
				}
				String leafIcon = tree.getLeafIcon();
				if(!StringUtil.isEmpty(leafIcon)) {
					ta.setLeafIcon(leafIcon);
				}
				String identifierColumnName = tree.getIdentifierColumnName();
				if(!StringUtil.isEmpty(identifierColumnName)) {
					ta.setIdentifierColumnName(identifierColumnName);
				}
				String parentIdentifierColumnName = tree.getParentIdentifierColumnName();
				if(!StringUtil.isEmpty(parentIdentifierColumnName)) {
					ta.setParentIdentifierColumnName(parentIdentifierColumnName);
				}
				String rootText = tree.getRootText();
				if(!StringUtil.isEmpty(rootText)) {
					ta.setRootText(rootText);
				}
				String rootIcon = tree.getRootIcon();
				if(!StringUtil.isEmpty(rootIcon)) {
					ta.setRootIcon(rootIcon);
				}
				
				ta.bindData(tree.getDataTable());
			} else {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
				m.execute(ta);
			}
			
			ta.bindData();

			ta.addVariables(context);
		} catch (PrivException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isCustomscrollbar() {
		return customscrollbar;
	}

	public void setCustomscrollbar(boolean customscrollbar) {
		this.customscrollbar = customscrollbar;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("customscrollbar", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("checkbox"));
		list.add(new TagAttr("cascade", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("radio"));
		list.add(new TagAttr("expand", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("level", DataTypes.INTEGER.code()));
		list.add(new TagAttr("size", DataTypes.INTEGER.code()));
		list.add(new TagAttr("style"));
		list.add(new TagAttr("method"));
		list.add(new TagAttr("rest"));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.TreeTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public boolean isKeepTagSource() {
		return true;// 本标签要求编译后依然持有源代码
	}

	@Override
	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		String fileName = te.getFileName();
		if (fileName != null && fileName.startsWith(Config.getContextRealPath())) {
			fileName = fileName.substring(Config.getContextRealPath().length());
		}
		bodyUID = fileName + "#" + StringUtil.md5Hex(getTagSource());
		TreeAction ta = prepareAction();
		ta.setParams(new Mapx<>());

		TreeBody body = TreeBodyManager.get(ta, bodyUID, getTagSource());
		if (!tc.isHasBody()) {
			tc.setHasBody(true);
		}
		tc.setCommands(body.getExecutor().getCommands());
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

}
