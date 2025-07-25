package io.arkx.data.jdbc.repository.query.test.repository;

/**
 * @author Nobody
 * @date 2025-07-17 1:01
 * @since 1.0
 */

import io.arkx.data.jdbc.repository.query.test.model.Product;
import io.arkx.framework.data.common.sqltoy.SqlToyQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

	// 条件查询：按名称和类别过滤
	List<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category);

	// 分页条件查询：按名称和类别过滤
	Page<Product> findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(String name, String category, Pageable pageable);

	// 自定义查询：按价格范围和活跃状态过滤
//	@Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice AND active = :active")
//	Page<Product> findByPriceRangeAndActive(double minPrice, double maxPrice, boolean active, Pageable pageable);

	@SqlToyQuery
	Page<Product> findBySqlToyQuery(@Param("name") String name, @Param("category") String category, Pageable pageable);

}