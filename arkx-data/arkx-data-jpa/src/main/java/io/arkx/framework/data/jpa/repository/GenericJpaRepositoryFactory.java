package io.arkx.framework.data.jpa.repository;
//package io.arkx.framework.jpa.repository;
//
//import java.lang.reflect.ParameterizedType;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.EntityManager;
//
//import org.aopalliance.intercept.MethodInterceptor;
//import org.aopalliance.intercept.MethodInvocation;
//import org.springframework.aop.AfterAdvice;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.core.annotation.OrderUtils;
//import org.springframework.data.jpa.provider.PersistenceProvider;
//import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
//import org.springframework.data.repository.query.QueryLookupStrategy;
//import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
//import org.springframework.lang.Nullable;
//import org.springframework.util.CollectionUtils;
//
//import io.arkx.framework.jpa.ContextHolder;
//import io.arkx.framework.jpa.EntityAssembler;
//import io.arkx.framework.jpa.TemplateQueryLookupStrategy;
//
///**
// * .<p>通用Jpa仓库构造工厂</p>
// *
// * @author <a href="mailto:stormning@163.com">stormning</a>
// * @version V1.0, 2015/8/9.
// */
//public class GenericJpaRepositoryFactory extends JpaRepositoryFactory {
//	
//    private final EntityManager entityManager;
//
//    private final PersistenceProvider extractor;
//
//    @SuppressWarnings("rawtypes")
//	private Map<Class<?>, List<EntityAssembler>> assemblers = new ConcurrentHashMap<>();
//
//    /**
//     * Creates a new {@link JpaRepositoryFactory}.
//     *
//     * @param entityManager must not be {@literal null}
//     */
//    GenericJpaRepositoryFactory(EntityManager entityManager) {
//        super(entityManager);
//        this.entityManager = entityManager;
//        this.extractor = PersistenceProvider.fromEntityManager(entityManager);
//
//        final AssemblerInterceptor assemblerInterceptor = new AssemblerInterceptor();
//        addRepositoryProxyPostProcessor((factory, repositoryInformation) -> factory.addAdvice(assemblerInterceptor));
//    }
//
//
//    @Override
//    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable QueryLookupStrategy.Key key,
//    		QueryMethodEvaluationContextProvider evaluationContextProvider) {
//        return Optional.of(TemplateQueryLookupStrategy.create(entityManager, key, extractor, evaluationContextProvider));
//    }
//
//    /**
//     * 获取实体对象组装器.
//     * @param clazz
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//	private List<EntityAssembler> getEntityAssemblers(Class<?> clazz) {
//        if (assemblers.isEmpty()) {
//            Collection<EntityAssembler> abs = ContextHolder.getBeansOfType(EntityAssembler.class);
//            if (abs.isEmpty()) {
//                return Collections.emptyList();
//            } else {
//                for (EntityAssembler ab : abs) {
//                    Class p0 = getGenericParameter0(ab.getClass());
//                    List<EntityAssembler> ass = this.assemblers.computeIfAbsent(p0, k -> new ArrayList<>());
//                    ass.add(ab);
//                }
//                for (List<EntityAssembler> ess : assemblers.values()) {
//                    ess.sort((o1, o2) -> OrderUtils.getOrder(o2.getClass()) - OrderUtils.getOrder(o1.getClass()));
//                }
//            }
//        }
//        return assemblers.get(clazz);
//    }
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    private void massemble(Iterable iterable) {
//        if (!iterable.iterator().hasNext()) {
//            return;
//        }
//
//        Object object = iterable.iterator().next();
//        if (isEntityObject(object)) {
//            List<EntityAssembler> entityAssemblers = getEntityAssemblers(object.getClass());
//            if (!CollectionUtils.isEmpty(entityAssemblers)) {
//                for (EntityAssembler assembler : entityAssemblers) {
//                    assembler.massemble(iterable);
//                }
//            }
//        }
//    }
//
//    /**
//     * 是否jpa实体类
//     * @param object
//     * @return
//     */
//    private boolean isEntityObject(Object object) {
//        return object != null && AnnotationUtils.findAnnotation(object.getClass(), Entity.class) != null;
//    }
//
//    /**
//     * 获取类第一个泛型参数类型
//     * @param clzz
//     * @return
//     */
//    private Class<?> getGenericParameter0(Class<?> clzz) {
//        return (Class<?>) ((ParameterizedType) clzz.getGenericSuperclass()).getActualTypeArguments()[0];
//    }
//
//    /**
//     * 对象组装拦截器.
//     * @author Darkness
//     * @date 2020-02-29 17:09:44
//     * @version V1.0
//     */
//    @SuppressWarnings("unchecked")
//    public class AssemblerInterceptor implements MethodInterceptor, AfterAdvice {
//
//        @SuppressWarnings("rawtypes")
//		@Override
//        public Object invoke(MethodInvocation invocation) throws Throwable {
//            Object proceed = invocation.proceed();
//            if (!"save".equals(invocation.getMethod().getName())) {
//                if (proceed != null) {
//                    //EntityAssembler
//                    if (proceed instanceof Iterable) {
//                        massemble((Iterable) proceed);
//                    } else if (proceed instanceof Map) {
//                        massemble(((Map) proceed).values());
//                    } else if (isEntityObject(proceed)) {
//                        List<EntityAssembler> entityAssemblers = getEntityAssemblers(proceed.getClass());
//                        if (!CollectionUtils.isEmpty(entityAssemblers)) {
//                            for (EntityAssembler assembler : entityAssemblers) {
//                                assembler.assemble(proceed);
//                            }
//                        }
//                    }
//                }
//            }
//            return proceed;
//        }
//    }
//}
