package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;

/**
 * 拖拽
 *
 */
public class GridDrag extends AbstractGridFeature {

	@Override
	public void rewriteTD(DataGridAction dga, HtmlTD th, HtmlTD td) {
		if (!"true".equalsIgnoreCase(th.getAttribute("drag"))) {
			return;
		}
		String style = td.getAttribute("style");
		if (style != null) {
			td.addAttribute("style", style);
		}
		td.addClassName("z-draggable");
	}

}
