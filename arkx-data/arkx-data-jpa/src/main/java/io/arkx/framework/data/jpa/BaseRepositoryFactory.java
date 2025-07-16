package io.arkx.framework.data.jpa;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.aop.AfterAdvice;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.arkx.framework.boot.spring.IocBeanRegister;
import io.arkx.framework.data.jpa.entity.BaseEntity;
import io.arkx.framework.data.jpa.sqltoy.SqlToyRepositoryFactory;

/**
 * .<p>通用Jpa仓库构造工厂</p>
 * @author Darkness
 * @date 2020-02-29 23:12:43
 * @version V1.0
 */
public class BaseRepositoryFactory<T extends BaseEntity, I extends Serializable> extends SqlToyRepositoryFactory {

	public BaseRepositoryFactory(SqlToyLazyDao sqlToyLazyDao, EntityManager entityManager) {
		super(sqlToyLazyDao, entityManager);

		final AssemblerInterceptor assemblerInterceptor = new AssemblerInterceptor();
		addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(assemblerInterceptor));
	}

	@Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key,
																   ValueExpressionDelegate valueExpressionDelegate) {
		// 获取默认策略
		QueryLookupStrategy defaultStrategy = super.getQueryLookupStrategy(key, valueExpressionDelegate)
				.orElseThrow(() -> new IllegalStateException("No default query strategy found"));

		return Optional.of(
        		TemplateQueryLookupStrategy.create(
						defaultStrategy,
						sqlToyLazyDao,
						entityManager,
						key,
						extractor,
						new DefaultJpaQueryMethodFactory(extractor),
						valueExpressionDelegate,
						QueryRewriterProvider.simple(),
						EscapeCharacter.DEFAULT));
    }

	
	//设置=实现类是BaseRepositoryImpl
	@Override
	protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = this.getEntityInformation(information.getDomainType());
        Object repository = this.getTargetRepositoryViaReflection(information, new Object[]{entityInformation, entityManager});
        Assert.isInstanceOf(BaseRepositoryImpl.class, repository);
        return (JpaRepositoryImplementation<?, ?>)repository;
	}
	
	//设置自定义实现类class
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return BaseRepositoryImpl.class;
	}
}

