package org.ark.framework.i18n;

import org.ark.framework.jaf.tag.ListAction;

import com.rapidark.framework.annotation.Priv;
import com.rapidark.framework.commons.collection.DataTable;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.cosyui.web.UIFacade;
import com.rapidark.framework.i18n.LangUtil;

public class LangButtonUI extends UIFacade {
	@Priv(login = false)
	public void bindLanguageList(ListAction la) {
		DataTable dt = new DataTable();
		dt.insertColumn("Key");
		dt.insertColumn("Name");
		Mapx<String, String> all = LangUtil.getSupportedLanguages();
		for (String key : all.keyArray()) {
			dt.insertRow(new Object[] { key, all.get(key) });
		}
		la.bindData(dt);
	}
}