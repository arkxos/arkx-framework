package com.rapidark.framework.cosyui.web.mvc;

import java.util.Comparator;

import com.rapidark.framework.commons.collection.ReadOnlyList;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.extend.AbstractExtendService;

/**
 * URL处理者扩展服务
 * 
 */
public class URLHandlerService extends AbstractExtendService<IURLHandler> {
	public static URLHandlerService getInstance() {
		return findInstance(URLHandlerService.class);
	}

	@Override
	protected void prepareItemList() {
		itemList = ObjectUtil.toList(itemMap.values());
		itemList = ObjectUtil.sort(itemList, new Comparator<IURLHandler>() {
			@Override
			public int compare(IURLHandler o1, IURLHandler o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		itemList = new ReadOnlyList<>(itemList);
	}
}
