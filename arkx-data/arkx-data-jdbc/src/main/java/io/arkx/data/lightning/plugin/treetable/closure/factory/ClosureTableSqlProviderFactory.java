package io.arkx.data.lightning.plugin.treetable.closure.factory;

/**
 * @author Nobody
 * @date 2025-07-28 1:58
 * @since 1.0
 */

import io.arkx.data.lightning.plugin.treetable.closure.DatabaseFeatureDetector;
import io.arkx.data.lightning.plugin.treetable.closure.sql.ClosureTableSqlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClosureTableSqlProviderFactory {

	private final Map<String, ClosureTableSqlProvider> providerCache = new ConcurrentHashMap<>();

	@Autowired
	private List<ClosureTableSqlProvider> allProviders;

	@Autowired
	private DatabaseFeatureDetector featureDetector;

	public ClosureTableSqlProvider getProvider() {
		String dbType = featureDetector.getDatabaseType();
		return providerCache.computeIfAbsent(dbType, k -> {
			// 优先查找特定数据库的扩展实现（如MySQL 5.7、Oracle）
			for (ClosureTableSqlProvider provider : allProviders) {
				String providerName = provider.getClass().getSimpleName();
				if (provider.support(dbType.toLowerCase())) {
					return provider;
				}
			}
			// 回退到默认实现
			return allProviders.stream()
					.filter(p -> "defaultSqlProvider".equals(p.getClass().getAnnotation(Component.class).value()))
					.findFirst()
					.orElseThrow(() -> new UnsupportedOperationException("未找到支持的SQL提供器: " + dbType));
		});
	}
}