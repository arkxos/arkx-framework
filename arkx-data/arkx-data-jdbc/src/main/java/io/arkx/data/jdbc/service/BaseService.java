package io.arkx.data.jdbc.service;

import io.arkx.data.jdbc.repository.BaseJdbcRepository;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nobody
 * @date 2025-07-26 17:06
 * @since 1.0
 */
public interface BaseService<T, ID, R extends BaseJdbcRepository<T, ID>> extends BaseJdbcRepository<T, ID> {

	@Deprecated
	default List<T> list() {
		return findAll();
	}

	default T getById(ID id) {
		return findById(id).orElse(null);
	}

}
