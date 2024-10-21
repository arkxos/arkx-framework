package org.ark.framework.jaf.zhtml;

import com.rapidark.framework.commons.collection.Mapx;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlFragment
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:54:58 
 * @version V1.0
 */
public class ZhtmlFragment {
	public static final int FRAGMENT_HTML = 1;
	public static final int FRAGMENT_TAG = 2;
	public static final int FRAGMENT_HOLDER = 3;
	public static final int FRAGMENT_SCRIPT = 4;
	public int Type;
	public String TagPrefix;
	public String TagName;
	public Mapx<String, String> Attributes;
	public String FragmentText;
	public int StartLineNo;
	public int StartCharIndex;
	public int EndCharIndex;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.Type == 1) {
			sb.append("HTML");
		}
		if (this.Type == 2) {
			sb.append("TAG");
		}
		if (this.Type == 4) {
			sb.append("SCRIPT");
		}
		if (this.Type == 3) {
			sb.append("HOLDER");
		}
		sb.append(":");
		if (this.Type == 2) {
			sb.append("<" + this.TagPrefix + ":" + this.TagName);
		}
		if (this.Type == 3) {
			sb.append("${");
		}
		if (this.FragmentText != null) {
			String str = this.FragmentText.replaceAll("[\\n\\r]+", "\\\\n");
			str = this.FragmentText.replaceAll("\\s+", " ");
			if (str.length() > 100) {
				str = str.substring(0, 100);
			}
			sb.append(str);
		}
		if (this.Type == 3) {
			sb.append("}");
		}
		return sb.toString();
	}
}