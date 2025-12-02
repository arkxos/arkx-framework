package io.arkx.framework.data.jpa.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.arkx.framework.commons.model.PageResult;
import io.arkx.framework.commons.util.CriteriaQueryWrapper;
import io.arkx.framework.data.common.repository.ExtBaseRepository;

public interface IBaseService<T, ID extends Serializable, R extends ExtBaseRepository<T, ID>> {
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    void save(T t);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    List<T> saveAll(Iterable<T> ts);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    void update(T t);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    void deleteById(ID id);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    void delete(T t);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    void deleteInBatch(Iterable<T> ts);

    T findById(ID id);

    T findOneByCriteria(CriteriaQueryWrapper<T> criteria);

    <Q> List<T> findAllByCriteria(Q criteria);

    <Q> Page<T> findAllByCriteria(Q criteria, Pageable pageable);

    List<T> findAllByCriteria(CriteriaQueryWrapper<T> criteria);

    Page<T> findAllByCriteria(CriteriaQueryWrapper<T> criteria, Pageable pageable);

    void deleteByCriteria(CriteriaQueryWrapper<T> criteria);

    T findOneByExample(T example);

    List<T> findAll();

    List<T> findAll(T t);

    long count();

    long count(T t);

    List<T> list(T t);

    List list(T t, String... properties);

    PageResult<T> pageList(T t, int currentPage, int pageSize);

    PageResult<T> pageList(T t, int currentPage, int pageSize, String... properties);

    PageResult<T> pageList(T t, ExampleMatcher matcher, int currentPage, int pageSize);

    PageResult pageNativeQuery(String sql, List<Object> params, int currentPage, int pageSize);

    List<Map<String, Object>> nativeQuery(String sql, List<Object> params);

    PageResult setPageResult(List<?> list, int currentPage, int pageSize, long totalNum);

    Boolean exists(T example);
}
