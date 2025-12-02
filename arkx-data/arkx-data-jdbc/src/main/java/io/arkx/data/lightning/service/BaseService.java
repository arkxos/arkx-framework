package io.arkx.data.lightning.service;

import java.util.List;

import io.arkx.data.lightning.repository.BaseJdbcRepository;
import io.arkx.framework.commons.collection.tree.Treex;

/**
 * @author Nobody
 * @date 2025-07-26 17:06
 * @since 1.0
 */
public interface BaseService<T, ID, R extends BaseJdbcRepository<T, ID>>
        extends
            ListCrudService<T, ID>,
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

    List<T> findChildrenByParentId(ID parentId);

    Treex<String, T> findAllTree();
}
