package org.ark.framework.jaf;

import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.StringUtil;

/**
 * @class org.ark.framework.jaf.PlaceHolder
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:57:53 
 * @version V1.0
 */
public class PlaceHolder {
	
	private String prefix;
	private String varName;
	private String format;
	private String charWidth;

	public PlaceHolder(String holder) {
		if (holder.startsWith("${")) {
			if (!holder.endsWith("}")) {
				throw new RuntimeException("Place hold not endswith '}':" + holder);
			}
			holder = holder.substring(2, holder.length() - 1);
		}
		int index1 = holder.indexOf("|");
		if (index1 > 0) {
			String modifiers = holder.substring(index1 + 1);
			holder = holder.substring(0, index1);
			Mapx<String, String> map = StringUtil.splitToMapx(modifiers, "&&", "=");
			this.format = map.getString("Format");
			this.charWidth = map.getString("CharWidth");
		}
		int index2 = holder.indexOf(".");
		if (index2 > 0) {
			this.prefix = holder.substring(0, index2);
			this.varName = holder.substring(index2 + 1);
		} else {
			this.varName = holder;
		}
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getVarName() {
		return this.varName;
	}

	public String getFormat() {
		return this.format;
	}

	public String getCharWidth() {
		return this.charWidth;
	}
}