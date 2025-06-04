package io.arkx.framework.cosyui.control.datalist;

import io.arkx.framework.Constant;
import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.control.DataListAction;
import io.arkx.framework.cosyui.template.TemplateCompiler;
import io.arkx.framework.cosyui.template.TemplateExecutor;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;

/**
 * DataList的标签体
 */
public class DataListBody {
	String uid;
	String source;
	String template;
	TemplateExecutor executor;

	public DataListBody(String uid, String source) {
		this.uid = uid;
		this.source = source;
		this.source = source.substring(source.indexOf('>') + 1, source.lastIndexOf("</ark:datalist>"));
	}

	public String getUID() {
		return uid;
	}

	public void compile(DataListAction dla) {
		template = rewrite(dla);
		TemplateCompiler tc = new TemplateCompiler(ZhtmlManagerContext.getInstance());
		tc.compileSource(template);
		executor = tc.getExecutor();
	}

	String rewrite(DataListAction dla) {
		FastStringBuilder sb = new FastStringBuilder();
		String ID = dla.getID();
		sb.append("<!--_ARK_DATALIST_START_").append(ID).append("-->\n");
		sb.append("<input type=\"hidden\" id=\"").append(ID).append("\" method=\"").append(dla.getMethod()).append("\"");
		if (dla.isPageEnabled()) {
			sb.append(" page=\"true\"");
		}
		if (dla.getPageSize() > 0) {
			sb.append(" size=\"").append(dla.getPageSize()).append("\"");
		}
		if (dla.isAutoFill()) {
			sb.append(" autofill=\"true\"");
		}
		if (dla.isAutoPageSize()) {
			sb.append(" autopagesize=\"true\"");
		}
		if (dla.getDragClass() != null) {
			sb.append(" dragclass=\"").append(dla.getDragClass()).append("\"");
		}
		if (dla.getListNodes() != null) {
			sb.append(" listnodes=\"").append(dla.getListNodes()).append("\"");
		}
		if (dla.getSortEnd() != null) {
			sb.append(" sortend=\"").append(dla.getSortEnd()).append("\"");
		}
		sb.append("/>");
		sb.append("<ark:list>");
		sb.append(source);
		sb.append("</ark:list>");
		sb.append("<script ztype='DataList'>");
		getScript(dla, sb);
		sb.append("</script>");
		sb.append("\n<!--_ARK_DATALIST_END_").append(ID).append("-->");
		return sb.toStringAndClose();
	}

	void getScript(DataListAction dla, FastStringBuilder sb) {
		String ID = dla.getID();
		sb.append("<ark:if condition='${!_DataListAction.AjaxRequest}'>");
		sb.append("function DataList_").append(ID).append("_Init(afterInit){");
		sb.append("var dl = new Ark.DataList(document.getElementById('").append(ID).append("'));");
		sb.append("dl.setParam('").append(Constant.ID).append("','").append(ID).append("');");
		sb.append("<ark:foreach data='${_DataListAction.Params}'>");
		sb.append("dl.setParam('${Key}',\"${javaEncode(Value)}\");");
		sb.append("</ark:foreach>");
		sb.append("dl.setParam('").append(Constant.Page).append("',").append(dla.isPageEnabled()).append(");");
		if (StringUtil.isNotEmpty(dla.getDragClass())) {
			sb.append("dl.setParam('").append(Constant.DragClass).append("','").append(dla.getDragClass()).append("');");
		}
		if (StringUtil.isNotEmpty(dla.getSortEnd())) {
			sb.append("dl.setParam('").append(Constant.SortEnd).append("','").append(dla.getSortEnd()).append("');");
		}
		sb.append("dl.setParam('" + Constant.TagBody + "', '").append(uid).append("');");
		sb.append("if(afterInit){afterInit();}");
		sb.append("}");
		sb.append("</ark:if>");

		sb.append("function DataList_").append(ID).append("_Update(){");
		sb.append("var dl = $('#").append(ID).append("').getComponent('DataList');");
		sb.append("if(!dl){return;}");

		sb.append("dl.setParam('").append(Constant.DataGridPageIndex).append("',${_DataListAction.PageIndex});");
		sb.append("dl.setParam('").append(Constant.DataGridPageTotal).append("',${_DataListAction.Total});");
		sb.append("dl.setParam('").append(Constant.Size).append("',${_DataListAction.PageSize});");

		sb.append("}");
		sb.append("DataList_").append(ID).append("_Update();");
		sb.append("<ark:if condition='${!_DataListAction.AjaxRequest}'>");
		sb.append("Ark.Page.onLoad(function(){DataList_").append(ID).append("_Init(DataList_").append(ID).append("_Update);}, 8);");
		sb.append("</ark:if>");
	}

	public TemplateExecutor getExecutor() {
		return executor;
	}

	public String getTemplate() {
		return template;
	}
}
