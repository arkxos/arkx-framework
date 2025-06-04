package io.arkx.framework.cosyui.control.tree;

import com.arkxos.framework.Constant;
import com.arkxos.framework.Current;
import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.control.TreeAction;
import io.arkx.framework.cosyui.html.HtmlElement;
import io.arkx.framework.cosyui.html.HtmlParser;
import io.arkx.framework.cosyui.template.TemplateCompiler;
import io.arkx.framework.cosyui.template.TemplateExecutor;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

/**
 * 树标签体
 * 
 */
public class TreeBody {
	private String uid;
	private HtmlElement item;
	private String template;
	private String prefix = "";
	private TemplateExecutor executor;

	TreeBody(String uid, String source) {
		this.uid = uid;
		HtmlParser p = new HtmlParser(source);
		p.parse();
		HtmlElement e = p.getDocument().element("ark:tree");
		source = e.getInnerHTML();
		int start = source.indexOf("<p");
		int end = source.lastIndexOf("</p");
		prefix = source.substring(0, start);
		source = "<dt" + source.substring(start + 2, end) + "</dt>";
		item = new HtmlElement("dt");
		item.parseHtml(source);
	}

	public String getUID() {
		return uid;
	}

	void compile(TreeAction ta) {
		rewrite(ta);
		TemplateCompiler tc = new TemplateCompiler(ZhtmlManagerContext.getInstance());
		tc.compileSource(template);
		executor = tc.getExecutor();
	}

	private void rewrite(TreeAction ta) {
		// 标签体改写
		String innerHTML = item.getInnerHTML();
		innerHTML = "${(_NodeIcons)}" + innerHTML;
		item.setInnerHTML(innerHTML);
		item.setAttribute("${(_NodeAttributes)}", HtmlElement.SINGLETON_ATTRIBUTE);

		String afterDrag = item.getAttribute("afterDrag");
		if (StringUtil.isNotEmpty(afterDrag)) {
			item.setAttribute("dragEnd", "Tree.dragEnd");
			item.setAttribute("onMouseUp", "DragManager.onMouseUp(event,this)");
			String userAgent = Current.getRequest().getHeaders().getString("user-agent");
			if (userAgent != null && userAgent.indexOf("msie") >= 0) {
				item.setAttribute("onMouseEnter", "DragManager.onMouseOver(event,this)");
			} else {
				item.setAttribute("onMouseOver", "DragManager.onMouseOver(event,this)");
			}
			item.setAttribute("dragOver", "Tree.dragOver");
			if (userAgent != null && userAgent.indexOf("msie") >= 0) {
				item.setAttribute("onMouseLeave", "DragManager.onMouseOut(event,this)");
			} else {
				item.setAttribute("onMouseOut", "DragManager.onMouseOut(event,this)");
			}
			item.setAttribute("dragOut", "Tree.dragOut");
		}

		// 模板重写
		FastStringBuilder sb = new FastStringBuilder();
		String ID = ta.getID();
		sb.append("<ark:if condition='${!_TreeAction.LazyLoad}'>");
		sb.append("<div id='").append(ID)
			.append("_outer' class='treeContainer' style='-moz-user-select:none;")
			.append(ta.getStyle())
				.append("'><div ztype='tree' onselectstart='stopEvent(event);' id='")
				.append(ID)
				.append("' method='").append(ta.getMethod())
				.append("' rest='").append(ta.getRest())
				.append("' class='z-tree'");
		if (item.hasAttribute("onclick")) {
			sb.append(" onclick=\"" + item.getAttribute("onclick") + "\"");
			item.removeAttribute("onclick");
		}
		if (ta.getCheckbox() != null) {
			sb.append(" checkbox='" + ta.getCheckbox() + "'");
			if (!ta.isCascade()) {
				sb.append(" cascade='false'");
			}
		} else if (ta.getRadio() != null) {
			sb.append(" radio='true'");
		}
		if (ta.isCustomscrollbar()) {
			sb.append(" customscrollbar='true'");
		} else {
			sb.append(" customscrollbar='false'");
		}
		sb.append("><table><tr><td>");

		// 根节点
		sb.append("<dl><dt treenodetype='root' level='0' id='").append(ID).append("__TreeRoot' parentid='' lazy='0'>");
		sb.append("<img src=\"${Config['App.ContextPath']}").append(TreeData.Blank_Image_Path);
		sb.append("\" class='tree-skeleton-icon' style='background:url(${Config['App.ContextPath']}${_TreeAction.RootIcon}) no-repeat center center;'>");
		if ("all".equals(ta.getCheckbox())) {
			sb.append("<img src=\"${Config['App.ContextPath']}").append(TreeData.Blank_Image_Path);
			sb.append("\" class='tree-skeleton-icon ").append(TreeData.Class_CheckBox).append("'>");
		}
		sb.append("${_TreeAction.RootText}</dt>");
		sb.append("<dd>");
		sb.append("</ark:if>");

		// 节点循环
		sb.append("<ark:list>");
		sb.append(prefix);
		sb.append("${(_NodeWrapStart)}");
		sb.append(item.getOuterHTML());
		sb.append("${(_NodeWrapEnd)}");
		sb.append("</ark:list>");

		sb.append("<ark:if condition='${!_TreeAction.LazyLoad}'>");
		sb.append("</dd></dl></td></tr></table></div></div>\n\r");
		sb.append("<script>");
		getScript(ta, sb);
		sb.append("</script></ark:if>");
		template = sb.toStringAndClose();
	}

	private void getScript(TreeAction ta, FastStringBuilder sb) {
		String ID = ta.getID();
		sb.append("<ark:if condition='${!_TreeAction.AjaxRequest}'>");
		sb.append("function Tree_").append(ID).append("_Init(afterInit){");
		sb.append("var tree = new Ark.Tree(document.getElementById('").append(ID).append("'));");
		sb.append("<ark:foreach data='${_TreeAction.Params}'>");
		sb.append("tree.setParam('${Key}',\"${(javaEncode(Value))}\");");
		sb.append("</ark:foreach>");
		if (ta.getStyle() != null) {
			sb.append("tree.setParam('").append(Constant.TreeStyle).append("',\"").append(ta.getStyle()).append("\");");
		}
		sb.append("tree.setParam('").append(Constant.TreeParentColumn).append("','").append(ta.getParentIdentifierColumnName())
				.append("');");
		sb.append("tree.setParam('").append(Constant.TreeLevel).append("',").append(ta.getLevel()).append(");");
		sb.append("tree.setParam('").append(Constant.TreeLazy).append("',\"").append(ta.isLazy()).append("\");");
		sb.append("tree.setParam('").append(Constant.TreeExpand).append("',\"").append(ta.isExpand()).append("\");");
		sb.append("tree.setParam('" + Constant.TagBody + "', '").append(uid).append("');");
		sb.append("if(afterInit){afterInit();}");
		sb.append("}");
		sb.append("</ark:if>");

		sb.append("function Tree_").append(ID).append("_Update(){");
		sb.append("var tree = $('#").append(ID).append("').getComponent('Tree');");
		sb.append("if(!tree){return;}");

		sb.append("tree.setParam('").append(Constant.TreeParentColumn).append("','").append(ta.getParentIdentifierColumnName())
				.append("');");
		sb.append("tree.setParam('").append(Constant.TreeLevel).append("',${_TreeAction.Level});");
		sb.append("tree.setParam('").append(Constant.TreeLazy).append("','${_TreeAction.Lazy}');");
		sb.append("}");

		sb.append("Tree_").append(ID).append("_Update();");

		sb.append("<ark:if condition='${!_TreeAction.AjaxRequest}'>");
		sb.append("Ark.Page.onLoad(function(){Tree_").append(ID).append("_Init(Tree_").append(ID).append("_Update);}, 7);");
		sb.append("</ark:if>");
	}

	public TemplateExecutor getExecutor() {
		return executor;
	}

	public String getTemplate() {
		return template;
	}
}
