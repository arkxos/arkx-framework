package io.arkx.framework.cosyui.web.mvc;

import java.util.Comparator;

import io.arkx.framework.commons.collection.ReadOnlyList;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.extend.AbstractExtendService;

/**
 * URL处理者扩展服务
 *
 */
public class URLHandlerService extends AbstractExtendService<IURLHandler> {

    public static URLHandlerService getInstance() {
        return AbstractExtendService.findInstance(URLHandlerService.class);
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
