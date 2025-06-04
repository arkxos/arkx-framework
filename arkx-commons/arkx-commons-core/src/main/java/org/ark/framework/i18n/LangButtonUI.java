package org.ark.framework.i18n;

import org.ark.framework.jaf.tag.ListAction;

import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.i18n.LangUtil;

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