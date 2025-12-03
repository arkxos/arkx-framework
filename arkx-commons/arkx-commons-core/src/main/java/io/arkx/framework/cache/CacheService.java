package io.arkx.framework.cache;

import io.arkx.framework.extend.AbstractExtendService;

/**
 * @class org.ark.framework.cache.CacheService 缓存数据提供者扩展服务
 * @private
 * @author Darkness
 * @date 2013-1-31 上午11:12:15
 * @version V1.0
 */
public class CacheService extends AbstractExtendService<CacheDataProvider> {

    /**
     * 获取缓存实例
     */
    public static CacheService getInstance() {
        return findInstance(CacheService.class);
    }

}
