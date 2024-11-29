package com.rapidark.cloud.platform.gateway.framework.base;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.common.utils.CriteriaQueryWrapper;
import com.rapidark.framework.common.utils.PageResult;
import com.rapidark.framework.common.utils.QueryHelp;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.util.*;

/**
 * @Description 父业务类，提供基础数据库操作方法
 * @Author jianglong
 * @Date 2020/05/16
 * @Version V1.0
 */
public class BaseService<T,ID extends Serializable,R extends BaseRepository<T, ID>> {

	private static final String DEFAULT_SORT_FIELD = "createTime";
	@Autowired
	private EntityManager entityManager;

	@Autowired
	public R entityRepository;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void save(T t){
		entityRepository.save(t);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public List<T> saveAll(Iterable<T> ts){
		return entityRepository.saveAll(ts);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void update(T t){
		entityRepository.save(t);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void deleteById(ID id){
		entityRepository.deleteById(id);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void delete(T t){
		entityRepository.delete(t);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public void deleteInBatch(Iterable<T> ts){
		entityRepository.deleteInBatch(ts);
	}

	public T findById(ID id){
		Optional<T> optional = entityRepository.findById(id);
		if (optional.isPresent()){
			return optional.get();
		}
		return null;
	}

	public T findOneByCriteria(CriteriaQueryWrapper<T> criteria) {
		List<T> data = entityRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
		if(data.isEmpty()) {
			return null;
		}
		return data.get(0);
	}

	public List<T> findAllByCriteria(CriteriaQueryWrapper<T> criteria) {
		List<T> data = entityRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.buildPredicate(root, criteria, criteriaBuilder));
		return data;
	}

	public Page<T> findAllByCriteria(CriteriaQueryWrapper<T> criteria, Pageable pageable) {
		Page<T> data = entityRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.buildPredicate(root, criteria, criteriaBuilder), pageable);
		return data;
	}

	public void deleteByCriteria(CriteriaQueryWrapper<T> criteria) {
		List<T> data = entityRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.buildPredicate(root, criteria, criteriaBuilder));
		for (T entity : data) {
			delete(entity);
		}
	}

	public T findOneByExample(T example) {
		List<T> data = entityRepository.findAll(Example.of(example));
		if(data.isEmpty()) {
			return null;
		}
		return data.get(0);
	}

	public List<T> findAll(){
		return entityRepository.findAll();
	}

	public List<T> findAll(T t){
		return entityRepository.findAll(Example.of(t));
	}

	public long count(){
		return entityRepository.count();
	}

	public long count(T t){
		return entityRepository.count(Example.of(t));
	}

	public List<T> list(T t){
		return list(t, DEFAULT_SORT_FIELD);
	}

	public List list(T t, String ... properties){
		return entityRepository.findAll(Example.of(t,ExampleMatcher.matching()),Sort.by(Sort.Direction.ASC, properties));
	}

	public PageResult<T> pageList(T t, int currentPage, int pageSize){
		return pageList(t, currentPage, pageSize, DEFAULT_SORT_FIELD);
	}

	public PageResult<T> pageList(T t, int currentPage, int pageSize, String ... properties){
		Pageable pageable = PageRequest.of(currentPage-1,pageSize, Sort.by(Sort.Direction.DESC, properties));
		Page<T> pageData  =  entityRepository.findAll(Example.of(t), pageable);
		return this.setPageResult(pageData.getContent(), currentPage, pageSize, pageData.getTotalElements());
	}

	public PageResult<T> pageList(T t, ExampleMatcher matcher, int currentPage, int pageSize){
		Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD));
		Page<T> pageData  =  entityRepository.findAll(Example.of(t, matcher), pageable);
		return this.setPageResult(pageData.getContent(), currentPage, pageSize, pageData.getTotalElements());
	}

	/**
	 * 分页原生SQL查询
	 * @param sql
	 * @param params
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public PageResult pageNativeQuery(String sql, List<Object> params, int currentPage, int pageSize){
		String sqlCount = "SELECT count(1) FROM (" +sql+") t ";
		Query queryCount = entityManager.createNativeQuery(sqlCount);
		Query query = entityManager.createNativeQuery(sql);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				query.setParameter(i + 1, params.get(i));
				queryCount.setParameter(i + 1, params.get(i));
			}
		}
		long totalNum = (long) queryCount.getSingleResult();
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//转换成map
		query.setFirstResult((currentPage -1) * pageSize).setMaxResults(pageSize);
		List<Map<String,Object>> list = query.getResultList();
		return this.setPageResult(list, currentPage, pageSize, totalNum);
	}

	/**
	 * 分页原生SQL查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> nativeQuery(String sql, List<Object> params){
		Query query = entityManager.createNativeQuery(sql);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				query.setParameter(i + 1, params.get(i));
			}
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);//转换成map
		return query.getResultList();
	}

	/**
	 * 分页结果
	 * @param list
	 * @param currentPage
	 * @param pageSize
	 * @param totalNum
	 * @return
	 */
	public PageResult setPageResult(List<?> list, int currentPage, int pageSize, long totalNum){
		//分页结果
		PageResult pageResult = new PageResult();
		pageResult.setCurrentPage(currentPage);
		pageResult.setPageSize(pageSize);
		pageResult.setTotalNum(totalNum);
		pageResult.setLists(list);
		return pageResult;
	}

}