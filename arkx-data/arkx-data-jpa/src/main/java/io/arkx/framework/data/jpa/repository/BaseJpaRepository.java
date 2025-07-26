package io.arkx.framework.data.jpa.repository;

import io.arkx.framework.data.common.repository.ExtBaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

/**
 * @author Nobody
 * @date 2025-07-26 22:10
 * @since 1.0
 */
public interface BaseJpaRepository<T, ID extends Serializable>
	extends JpaRepository<T, ID>,
				JpaSpecificationExecutor<T>,
		ExtBaseRepository<T, ID> {
}
