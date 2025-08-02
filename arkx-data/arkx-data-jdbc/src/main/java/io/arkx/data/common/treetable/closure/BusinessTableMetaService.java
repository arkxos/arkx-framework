package io.arkx.data.common.treetable.closure;

import io.arkx.data.common.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.common.treetable.closure.repository.BusinessTableMetaJdbcRepository;
import org.springframework.stereotype.Service;

/**
 * @author Nobody
 * @date 2025-07-28 2:00
 * @since 1.0
 */
@Service
public class BusinessTableMetaService {

	private final BusinessTableMetaJdbcRepository metaRepository;

	public BusinessTableMetaService(BusinessTableMetaJdbcRepository metaRepository) {
		this.metaRepository = metaRepository;
	}

	public void initMeta(String businessTable, String bizTable) {
		BusinessTableMeta meta = new BusinessTableMeta();
		meta.setBusinessTable(businessTable);
		meta.setBizTable(bizTable);
		meta.setUseIndependent(false);
		metaRepository.saveOrUpdate(meta);
	}

	public void markAsIndependent(String businessTable) {
		BusinessTableMeta meta = metaRepository.findByBusinessTable(businessTable)
				.orElseThrow(() -> new IllegalArgumentException("业务表未配置: " + businessTable));
		meta.setUseIndependent(true);
		metaRepository.saveOrUpdate(meta);
	}

	public BusinessTableMeta getMeta(String businessTable) {
		return metaRepository.findByBusinessTable(businessTable)
				.orElseThrow(() -> new IllegalArgumentException("业务表未配置: " + businessTable));
	}
}
