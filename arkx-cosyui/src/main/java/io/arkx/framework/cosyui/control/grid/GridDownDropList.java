package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;

/**
 * 下拉列表列
 *
 */
public class GridDownDropList extends AbstractGridFeature {

    public static final String ZTYPE = "DropDownList";

    @Override
    public void rewriteTD(DataGridAction dga, HtmlTD th, HtmlTD td) {
        if (!ZTYPE.equalsIgnoreCase(th.getAttribute("ztype"))) {
            return;
        }
        String field = th.getAttribute("field");
        String data = th.getAttribute("data");
        String zstyle = th.getAttribute("zstyle");
        if (StringUtil.isEmpty(zstyle)) {
            zstyle = "width:100px";
        }
        String id = dga.getID() + "_DropDwonList_" + field;
        FastStringBuilder sb = new FastStringBuilder();
        sb.append("<select ztype='combox' disabled='true' style='display:none;" + zstyle + ";' name='" + id + "' id='"
                + id + "_${i}' >");
        sb.append("<ark:list data='" + data + "' item='data'>\n");
        String selected = "${" + field + "==data[0]'?\"selected='true'\":''} ";
        sb.append("<option value='data[0]' ").append(selected).append(">${data[1]}</option>");
        sb.append("</ark:list>\n");
        sb.append("</select>");
        td.setInnerHTML(sb.toStringAndClose());
    }

}
