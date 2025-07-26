package io.arkx.framework.data.jpa;

import java.io.Serializable;

import jakarta.persistence.EntityManager;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import io.arkx.framework.data.common.entity.BaseEntity;
import io.arkx.framework.data.common.ContextHolder;
import io.arkx.framework.data.jpa.sqltoy.SqlToyJpaRepositoryFactoryBean;

/**
 * 基础Repostory简单实现 factory bean
 * 请参考 spring-data-jpa-reference [1.4.2. Adding custom behaviour to all repositories]
 *
 * @author Darkness
 * @date 2019-07-19 14:16:51
 * @version V1.0
 */
public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T extends BaseEntity, I extends Serializable>
		extends SqlToyJpaRepositoryFactoryBean<R, T, I> {

	public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		SqlToyLazyDao sqlToyLazyDao = ContextHolder.getBean(SqlToyLazyDao.class);
		return new BaseRepositoryFactory(sqlToyLazyDao, em);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ContextHolder.appContext = applicationContext;
	}

}
