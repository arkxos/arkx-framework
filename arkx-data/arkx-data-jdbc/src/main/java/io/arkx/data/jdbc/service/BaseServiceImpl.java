package io.arkx.data.jdbc.service;

import io.arkx.data.jdbc.repository.BaseJdbcRepository;
import io.arkx.framework.commons.collection.DataTable;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.FluentQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Nobody
 * @date 2025-08-01 14:48
 * @since 1.0
 */
@NoRepositoryBean
public class BaseServiceImpl<T, ID, R extends BaseJdbcRepository<T, ID>> implements BaseService<T, ID, R> {

	protected R myRepository;

	public BaseServiceImpl(R myRepository) {
		this.myRepository = myRepository;
	}

	@Override
	public T insert(T instance) {
		return myRepository.insert(instance);
	}

	@Override
	public T update(T instance) {
		return myRepository.update(instance);
	}

	@Override
	public boolean support(String modelType) {
		return this.myRepository.support(modelType);
	}

	@Override
	public Map<ID, T> mget(Collection<ID> ids) {
		return this.myRepository.mget(ids);
	}

	@Override
	public Map<ID, T> mgetOneByOne(Collection<ID> ids) {
		return this.myRepository.mgetOneByOne(ids);
	}

	@Override
	public List<T> findAllOneByOne(Collection<ID> ids) {
		return this.myRepository.findAllOneByOne(ids);
	}

	@Override
	public void toggleStatus(ID id) {
		this.myRepository.toggleStatus(id);
	}

	@Override
	public void fakeDelete(ID... ids) {
		this.myRepository.fakeDelete(ids);
	}

	@Override
	public DataTable queryDataTable(String sql, Object... params) {
		return this.myRepository.queryDataTable(sql, params);
	}

	@Override
	public List<Map<String, Object>> queryMap(String sql, Object... params) {
		return this.myRepository.queryMap(sql, params);
	}

	@Override
	public List<T> queryList(String sql, Object... params) {
		return this.myRepository.queryList(sql, params);
	}

	@Override
	public long queryForLong(String sql, Object... params) {
		return this.myRepository.queryForLong(sql, params);
	}

	@Override
	public <S extends T> S save(S entity) {
		return this.myRepository.save(entity);
	}

	@Override
	public <S extends T> List<S> saveAll(Iterable<S> entities) {
		return this.myRepository.saveAll(entities);
	}

	@Override
	public Optional<T> findById(ID id) {
		return this.myRepository.findById(id);
	}

	@Override
	public boolean existsById(ID id) {
		return this.myRepository.existsById(id);
	}

	@Override
	public List<T> findAll() {
		return this.myRepository.findAll();
	}

	@Override
	public List<T> findAllById(Iterable<ID> ids) {
		return this.myRepository.findAllById(ids);
	}

	@Override
	public long count() {
		return this.myRepository.count();
	}

	@Override
	public void deleteById(ID id) {
		this.myRepository.deleteById(id);
	}

	@Override
	public void delete(T entity) {
		this.myRepository.delete(entity);
	}

	@Override
	public void deleteAllById(Iterable<? extends ID> ids) {
		this.myRepository.deleteAllById(ids);
	}

	@Override
	public void deleteAll(Iterable<? extends T> entities) {
		this.myRepository.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		this.myRepository.deleteAll();
	}

	@Override
	public List<T> findAll(Sort sort) {
		return this.myRepository.findAll(sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return this.myRepository.findAll(pageable);
	}

	@Override
	public <S extends T> Optional<S> findOne(Example<S> example) {
		return this.myRepository.findOne(example);
	}

	@Override
	public <S extends T> List<S> findAll(Example<S> example) {
		return this.myRepository.findAll(example);
	}

	@Override
	public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
		return this.myRepository.findAll(example, sort);
	}

	@Override
	public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
		return this.myRepository.findAll(example, pageable);
	}

	@Override
	public <S extends T> long count(Example<S> example) {
		return this.myRepository.count(example);
	}

	@Override
	public <S extends T> boolean exists(Example<S> example) {
		return this.myRepository.exists(example);
	}

	@Override
	public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return this.myRepository.findBy(example, queryFunction);
	}

}
