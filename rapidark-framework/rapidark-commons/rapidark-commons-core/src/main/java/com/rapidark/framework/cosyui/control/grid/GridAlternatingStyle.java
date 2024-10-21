package com.rapidark.framework.cosyui.control.grid;

import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.cosyui.control.DataGridAction;
import com.rapidark.framework.cosyui.html.HtmlTR;

/**
 * 交替样式
 * 
 */
public class GridAlternatingStyle extends AbstractGridFeature {

	@Override
	public void rewriteTR(DataGridAction dga, HtmlTR tr) {
		if (!"Template".equalsIgnoreCase(tr.getAttribute("ztype"))) {
			return;
		}
		String style1 = tr.getAttribute("style1");
		String style2 = tr.getAttribute("style2");
		String class1 = tr.getAttribute("class1");
		String class2 = tr.getAttribute("class2");
		tr.removeAttribute("style1");
		tr.removeAttribute("style2");
		tr.removeAttribute("class1");
		tr.removeAttribute("class2");
		tr.addAttribute("class", "dg-row");
		if (ObjectUtil.notEmpty(style1)) {
			if (ObjectUtil.isEmpty(style2)) {
				tr.addAttribute("style", style1);
			} else {
				tr.addAttribute("style", "${i%2==0?'" + style1 + "':'" + style2 + "'}");
			}
		} else if (ObjectUtil.notEmpty(style2)) {
			tr.addAttribute("style", style2);
		}
		if (ObjectUtil.notEmpty(class1)) {
			if (ObjectUtil.isEmpty(class2)) {
				tr.addAttribute("class", "dg-row " + class1);
			} else {
				tr.addAttribute("class", "dg-row ${i%2==0?'" + class1 + "':'" + class2 + "'}");
			}
		} else if (ObjectUtil.notEmpty(class2)) {
			tr.addAttribute("class", "dg-row " + class2);
		}
	}
}
