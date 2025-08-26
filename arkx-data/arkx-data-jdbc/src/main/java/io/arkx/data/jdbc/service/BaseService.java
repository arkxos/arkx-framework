package io.arkx.data.jdbc.service;

import io.arkx.data.jdbc.repository.BaseJdbcRepository;
import io.arkx.data.service.ExtBaseService;
import io.arkx.data.service.ListCrudService;
import io.arkx.data.service.ListPagingAndSortingService;
import io.arkx.data.service.ListQueryByExampleExecutorService;
import io.arkx.framework.data.common.repository.ExtBaseRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.ListQueryByExampleExecutor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nobody
 * @date 2025-07-26 17:06
 * @since 1.0
 */
public interface BaseService<T, ID, R extends BaseJdbcRepository<T, ID>>
		extends ListCrudService<T, ID>,
		ListPagingAndSortingService<T, ID>,
		ListQueryByExampleExecutorService<T>,
		ExtBaseService<T, ID> {

	T insert(T instance);

	T update(T instance);

	@Deprecated
	default List<T> list() {
		return findAll();
	}

	default T getById(ID id) {
		return findById(id).orElse(null);
	}

}
