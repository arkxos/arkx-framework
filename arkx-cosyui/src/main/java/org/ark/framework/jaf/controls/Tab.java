package org.ark.framework.jaf.controls;

import java.util.ArrayList;

import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.html.HtmlScript;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * @author Darkness
 * @date 2012-11-19 下午03:19:36
 * @version V1.0
 */
public class Tab {

    public static final String TabTagKey = "_ARK_TABTAGKEY";

    private String content;

    private boolean lazy;

    private String height;

    public Tab(String content) {
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    public String getHtml() {
        StringBuilder sb = new StringBuilder();

        sb.append("<table width=\"100%\" height=\"" + getHeight()
                + "\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" id=\"js_layoutTable\" class=\"js_layoutTable\">");
        sb.append("<tr><td height=\"33\" valign=\"top\" style=\"_position:relative\">");
        sb.append(
                "<div class=\"z-tabpanel\"><div class=\"z-tabpanel-ct\"><div class=\"z-tabpanel-overflow\"><div class=\"z-tabpanel-nowrap\">");
        sb.append(content);
        sb.append("</div></div></div></div>");
        sb.append("</td></tr>");
        String selectedID = "";

        ArrayList<String[]> children = (ArrayList<String[]>) Current.getVariable(TabTagKey);
        if (ObjectUtil.notEmpty(children)) {
            for (String[] arr : children) {
                String id = arr[0];
                String selected = arr[2];
                if ("true".equals(selected)) {
                    selectedID = id;
                    break;
                }
            }
            if (ObjectUtil.empty(selectedID)) {
                selectedID = ((String[]) children.get(0))[0];
            }
        }

        if (ObjectUtil.notEmpty(children)) {
            sb.append("<tr><td height=\"*\" valign=\"top\">");
            for (String[] arr : children) {
                String id = arr[0];
                String src = arr[1];
                String displayType = arr[3];
                String childTabContent = arr[4];
                if ("iframe".equals(displayType)) {
                    if (id.equals(selectedID)) {
                        sb.append("<iframe ").append(src).append(
                                " width=\"100%\" height=\"100%\" style=\"position:static;left:-22in;top:-11in;\" id=\"_ChildTabFrame_")
                                .append(id)
                                .append("\" frameborder=\"0\" scrolling=\"auto\" allowtransparency=\"true\"></iframe>");
                    } else {
                        sb.append("<iframe ");
                        if (this.lazy) {
                            sb.append("src='about:blank' _");
                        }
                        sb.append(src).append(
                                " width='100%' height='100%' style=\"position:absolute;left:-22in;top:-11in;\" id=\"_ChildTabFrame_")
                                .append(id)
                                .append("\" frameborder=\"0\" scrolling=\"auto\" allowtransparency=\"true\"></iframe>");
                    }
                } else {
                    if (id.equals(selectedID)) {
                        sb.append("<div style='position:static;left:-22in;top:-11in;' id='_ChildTabFrame_" + id + "'>"
                                + childTabContent + "</div>");
                    } else {
                        sb.append("<div style=\'position:absolute;left:-22in;top:-11in;\' id='_ChildTabFrame_" + id
                                + "' style='height:0px;display:none;'>" + childTabContent + "</div>");
                    }
                }
            }
            HtmlScript script = new HtmlScript();
            script.setInnerHTML(
                    "Ark.Page.onReady(function(){Ark.TabPage.init(\"_ChildTabFrame_" + selectedID + "\");},5);");
            sb.append(script.getOuterHtml());
            sb.append("</td></tr>");
        }
        sb.append("</table>");

        return sb.toString();
    }

    public static void init() {
        Current.setVariable(TabTagKey, new ArrayList<String[]>());
    }

    public boolean isLazy() {
        return this.lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public String getHeight() {

        if (height == null) {
            return "100%";
        }
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

}
