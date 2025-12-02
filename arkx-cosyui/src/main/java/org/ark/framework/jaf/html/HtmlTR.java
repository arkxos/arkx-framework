package org.ark.framework.jaf.html;

import java.util.ArrayList;
import java.util.regex.Matcher;

import io.arkx.framework.commons.util.StringUtil;

/**
 * @class org.ark.framework.jaf.html.HtmlTR
 *
 * @author Darkness
 * @date 2013-1-31 下午12:50:55
 * @version V1.0
 */
public class HtmlTR extends HtmlElement {
    protected HtmlTable parent;
    protected ArrayList<String> pList = null;

    public HtmlTR() {
        this(null);
    }

    public HtmlTR(HtmlTable parent) {
        this.parent = parent;
        this.ElementType = "TR";
        this.TagName = "tr";
    }

    public void addTD(HtmlTD td) {
        addChild(td);
    }

    public HtmlTD getTD(int index) {
        return (HtmlTD) this.Children.get(index);
    }

    public void removeTD(int index) {
        if ((index < 0) || (index > this.Children.size())) {
            throw new RuntimeException("Index out of range:" + index);
        }
        this.Children.remove(index);
    }

    public void setHeight(int height) {
        this.Attributes.put("height", Integer.valueOf(height));
    }

    public int getHeight() {
        return ((Integer) this.Attributes.get("height")).intValue();
    }

    public void setAlign(String align) {
        this.Attributes.put("align", align);
    }

    public String getAlign() {
        return (String) this.Attributes.get("align");
    }

    public void setBgColor(String bgColor) {
        this.Attributes.put("bgColor", bgColor);
    }

    public String getVAlign() {
        return (String) this.Attributes.get("vAlign");
    }

    public void setVAlign(String vAlign) {
        this.Attributes.put("vAlign", vAlign);
    }

    public int getRowIndex() {
        for (int i = 0; i < this.ParentElement.Children.size(); i++) {
            if (((HtmlElement) this.ParentElement.Children.get(i)).equals(this)) {
                return i;
            }
        }
        throw new RuntimeException("getRowIndex() failed");
    }

    public void parseHtml(String html) throws Exception {
        Matcher m = HtmlTable.PTR.matcher(html);
        if (!m.find()) {
            throw new Exception("Parse tr failed:" + html);
        }
        String attrs = m.group(1);
        String tds = m.group(2).trim();

        this.Attributes.clear();
        this.Children.clear();

        m = HtmlTable.PInnerTable.matcher(tds);
        int lastEndIndex = 0;
        while (m.find(lastEndIndex)) {
            if (this.pList == null) {
                this.pList = new ArrayList();
            }
            this.pList.add(m.group(0));
            lastEndIndex = m.end();
        }
        if (this.pList != null) {
            for (int i = 0; i < this.pList.size(); i++) {
                tds = StringUtil.replaceEx(tds, ((String) this.pList.get(i)).toString(),
                        "<!--_ARK_INNERTABLE_PROTECTED_" + i + "-->");
            }
        }

        this.Attributes = parseAttr(attrs);

        m = HtmlTable.PTDPre.matcher(tds);
        lastEndIndex = 0;
        while (m.find(lastEndIndex)) {
            String t = tds.substring(m.start(), m.end());
            HtmlTD td = new HtmlTD(this);
            td.parseHtml(t);
            addTD(td);
            lastEndIndex = m.end();
        }
    }

    public String restoreInnerTable(String html) {
        if ((this.pList == null) || (this.pList.size() == 0)) {
            return html;
        }
        String[] arr = StringUtil.splitEx(html, "<!--_ARK_INNERTABLE_PROTECTED_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (StringUtil.isNotEmpty(arr[i])) {
                if (i != 0) {
                    int index = Integer.parseInt(arr[i].substring(0, arr[i].indexOf("-")));
                    sb.append(((String) this.pList.get(index)).toString());
                    arr[i] = arr[i].substring(arr[i].indexOf(">") + 1);
                }
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }

    public HtmlTable getParent() {
        return this.parent;
    }
}
