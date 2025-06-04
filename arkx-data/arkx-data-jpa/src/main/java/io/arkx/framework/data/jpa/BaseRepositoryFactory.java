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

    @SuppressWarnings("rawtypes")
	private Map<Class<?>, List<EntityAssembler>> assemblers = new ConcurrentHashMap<>();

	public BaseRepositoryFactory(SqlToyLazyDao sqlToyLazyDao, EntityManager entityManager) {
		super(sqlToyLazyDao, entityManager);

		final AssemblerInterceptor assemblerInterceptor = new AssemblerInterceptor();
		addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(assemblerInterceptor));
	}

	@Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key,
																   ValueExpressionDelegate valueExpressionDelegate) {
        return Optional.of(
        		TemplateQueryLookupStrategy.create(sqlToyLazyDao,
						entityManager,
						key,
						extractor,
						new DefaultJpaQueryMethodFactory(extractor),
						valueExpressionDelegate,
						QueryRewriterProvider.simple(),
						EscapeCharacter.DEFAULT));
    }
	
	/**
     * 获取实体对象组装器.
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
	private List<EntityAssembler> getEntityAssemblers(Class<?> clazz) {
        if (assemblers.isEmpty()) {
            Collection<EntityAssembler> abs = IocBeanRegister.getBeansOfType(EntityAssembler.class);
            if (abs.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (EntityAssembler ab : abs) {
                    Class p0 = getGenericParameter0(ab.getClass());
                    List<EntityAssembler> ass = this.assemblers.computeIfAbsent(p0, k -> new ArrayList<>());
                    ass.add(ab);
                }
                for (List<EntityAssembler> ess : assemblers.values()) {
                    ess.sort((o1, o2) -> OrderUtils.getOrder(o2.getClass()) - OrderUtils.getOrder(o1.getClass()));
                }
            }
        }
        return assemblers.get(clazz);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void massemble(Iterable iterable) {
        if (!iterable.iterator().hasNext()) {
            return;
        }

        Object object = iterable.iterator().next();
        if (isEntityObject(object)) {
            List<EntityAssembler> entityAssemblers = getEntityAssemblers(object.getClass());
            if (!CollectionUtils.isEmpty(entityAssemblers)) {
                for (EntityAssembler assembler : entityAssemblers) {
                    assembler.massemble(iterable);
                }
            }
        }
    }

    /**
     * 是否jpa实体类
     * @param object
     * @return
     */
    private boolean isEntityObject(Object object) {
        return object != null && AnnotationUtils.findAnnotation(object.getClass(), Entity.class) != null;
    }

    /**
     * 获取类第一个泛型参数类型
     * @param clzz
     * @return
     */
    private Class<?> getGenericParameter0(Class<?> clzz) {
        return (Class<?>) ((ParameterizedType) clzz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 对象组装拦截器.
     * @author Darkness
     * @date 2020-02-29 17:09:44
     * @version V1.0
     */
    @SuppressWarnings("unchecked")
    public class AssemblerInterceptor implements MethodInterceptor, AfterAdvice {

        @SuppressWarnings("rawtypes")
		@Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Object proceed = invocation.proceed();
            if (!"save".equals(invocation.getMethod().getName())) {
                if (proceed != null) {
                    //EntityAssembler
                    if (proceed instanceof Iterable) {
                        massemble((Iterable) proceed);
                    } else if (proceed instanceof Map) {
                        massemble(((Map) proceed).values());
                    } else if (isEntityObject(proceed)) {
                        List<EntityAssembler> entityAssemblers = getEntityAssemblers(proceed.getClass());
                        if (!CollectionUtils.isEmpty(entityAssemblers)) {
                            for (EntityAssembler assembler : entityAssemblers) {
                                assembler.assemble(proceed);
                            }
                        }
                    }
                }
            }
            return proceed;
        }
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

