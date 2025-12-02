package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;

/**
 * 多选框列
 *
 */
public class GridCheckbox extends AbstractGridFeature {

	public static final String ZTYPE = "Checkbox";

	@Override
	public void rewriteTD(DataGridAction dga, HtmlTD th, HtmlTD td) {
		if (!ZTYPE.equalsIgnoreCase(th.getAttribute("ztype"))) {
			return;
		}
		th.addAttribute("disabledresize", "true");
		String field = th.getAttribute("field");
		String checkedvalue = th.getAttribute("checkedvalue");
		String disabled = th.getAttribute("disabled");
		if (checkedvalue == null) {
			checkedvalue = "Y";
		}
		if (disabled == null || disabled.equalsIgnoreCase("true")) {
			disabled = "disabled";
		}
		else {
			disabled = "";
		}
		String id = dga.getID();
		String checked = "${" + field + "=='" + checkedvalue + "'?'checked':''} ";
		td.setInnerHTML("<input type='checkbox' " + disabled + " name='" + id + "_" + field + "_Checkbox' id='" + id
				+ "_" + field + "_Checkbox_${i}' value='" + checkedvalue + "' " + checked + " autocomplete='off' />");

	}

}
