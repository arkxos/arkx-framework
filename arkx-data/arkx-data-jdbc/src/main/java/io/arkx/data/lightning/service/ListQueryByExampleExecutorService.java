package io.arkx.data.lightning.service;

/**
 * @author Nobody
 * @date 2025-08-27 0:52
 * @since 1.0
 */
public interface ListQueryByExampleExecutorService<T> {

	<S extends T> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example);

	<S extends T> java.util.List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort);

	<S extends T> java.util.Optional<S> findOne(org.springframework.data.domain.Example<S> example);

	<S extends T> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable);

	<S extends T> long count(org.springframework.data.domain.Example<S> example);

	<S extends T> boolean exists(org.springframework.data.domain.Example<S> example);

	<S extends T, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>,R> queryFunction);

}