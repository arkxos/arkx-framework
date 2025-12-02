package io.arkx.data.lightning.test;

/**
 * @author Nobody
 * @date 2025-07-17 1:04
 * @since 1.0
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.test.context.ActiveProfiles;

import io.arkx.data.lightning.repository.support.SqlToyJdbcRepositoryFactoryBean;
import io.arkx.data.lightning.sample.model.Product;
import io.arkx.data.lightning.sample.repository.ProductRepository;

@EnableJdbcRepositories(repositoryFactoryBeanClass = SqlToyJdbcRepositoryFactoryBean.class,
        basePackages = {"io.arkx.data.lightning.sample.repository"})
@DataJdbcTest // 仅加载 JDBC 相关 Bean
@ActiveProfiles("test") // 使用测试配置（可选）
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // 测试保存和查询
    @Test
    void saveAndFindById_ShouldReturnProduct() {
        Product product = Product.builder().name("Test Product").price(new BigDecimal("99.99"))
                .category("Test Category").active(true).build();

        Product saved = productRepository.save(product);
        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Product");
        assertThat(found.get().getPrice()).isEqualTo(new BigDecimal("99.99"));
    }

    // 测试删除
    @Test
    void delete_ShouldRemoveProduct() {
        Product product = productRepository
                .save(Product.builder().name("To Delete").price(new BigDecimal("50.0")).build());
        Long productId = product.getId();

        productRepository.deleteById(productId);
        Optional<Product> afterDelete = productRepository.findById(productId);

        assertThat(afterDelete).isEmpty();
    }

    // 测试自定义查询：按名称和类别筛选（分页）
    @Test
    void findByNameAndCategory_WithPagination_ShouldReturnPage() {
        // 插入测试数据
        productRepository.saveAll(List.of(Product.builder().name("Apple").category("Fruit").build(),
                Product.builder().name("Banana").category("Fruit").build(),
                Product.builder().name("Carrot").category("Vegetable").build()));

        // 查询名称含"App"且类别为"Fruit"的第一页（每页2条）
        Page<Product> result = productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase("App",
                "Fruit", PageRequest.of(0, 2));

        assertThat(result.getTotalElements()).isEqualTo(1); // 只有Apple符合条件
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
    }

    // 测试自定义查询：按名称和类别筛选（分页）
    @Test
    void findBySqlToy_WithPagination_ShouldReturnPage() {
        // 插入测试数据
        productRepository
                .saveAll(List.of(Product.builder().name("Apple").category("Fruit").price(new BigDecimal("0")).build(),
                        Product.builder().name("Apple2").category("Fruit").price(new BigDecimal("0")).build(),
                        Product.builder().name("Banana").category("Fruit").price(new BigDecimal("0")).build(),
                        Product.builder().name("Carrot").category("Vegetable").price(new BigDecimal("0")).build()));

        // 查询名称含"App"且类别为"Fruit"的第一页（每页2条）
        Page<Product> result = productRepository.findBySqlToyQuery("App", "Fruit", PageRequest.of(0, 1));

        assertThat(result.getTotalElements()).isEqualTo(2); // 只有Apple符合条件
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
    }

    // 测试自定义查询：价格范围和活跃状态（分页）
    @Test
    void findByPriceRangeAndActive_WithPagination_ShouldReturnPage() {
        // 插入测试数据
        productRepository.saveAll(List.of(Product.builder().price(new BigDecimal("100.0")).active(true).build(),
                Product.builder().price(new BigDecimal("200.0")).active(true).build(),
                Product.builder().price(new BigDecimal("300.0")).active(false).build(),
                Product.builder().price(new BigDecimal("250.0")).active(true).build()));

        // 查询价格在150-250之间且活跃的产品（第0页，每页2条）
        // Page<Product> result = productRepository.findByPriceRangeAndActive(
        // 150.0, 250.0, true, PageRequest.of(0, 2));
        //
        // assertThat(result.getTotalElements()).isEqualTo(2); // 200和250符合条件
        // assertThat(result.getContent()).hasSize(2);
        // assertThat(result.getContent().get(0).getPrice()).isEqualTo(200.0);
        // assertThat(result.getContent().get(1).getPrice()).isEqualTo(250.0);
    }
}
