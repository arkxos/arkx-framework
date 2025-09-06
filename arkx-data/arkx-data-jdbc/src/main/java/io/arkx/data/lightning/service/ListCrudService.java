package io.arkx.data.lightning.service;

/**
 * @author Nobody
 * @date 2025-08-27 0:47
 * @since 1.0
 */
public interface ListCrudService <T, ID> extends Service<T,ID> {

	<S extends T> S save(S entity);

	<S extends T> java.util.List<S> saveAll(java.lang.Iterable<S> entities);

	java.util.Optional<T> findById(ID id);

	boolean existsById(ID id);

	java.util.List<T> findAll();

	java.util.List<T> findAllById(java.lang.Iterable<ID> ids);

	long count();

	void deleteById(ID id);

	void delete(T entity);

	void deleteAllById(java.lang.Iterable<? extends ID> ids);

	void deleteAll(java.lang.Iterable<? extends T> entities);

	void deleteAll();

}
