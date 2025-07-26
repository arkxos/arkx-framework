package io.arkx.data.jdbc.service;

import io.arkx.data.jdbc.repository.BaseJdbcRepository;

import java.io.Serializable;

/**
 * @author Nobody
 * @date 2025-07-26 17:06
 * @since 1.0
 */
public interface BaseService<T, ID extends Serializable> extends BaseJdbcRepository<T, ID> {

}
