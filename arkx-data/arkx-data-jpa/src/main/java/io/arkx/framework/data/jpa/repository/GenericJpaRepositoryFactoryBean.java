package io.arkx.framework.data.jpa.repository;

// package io.arkx.framework.jpa.repository;
//
// import org.springframework.beans.BeansException;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.ApplicationContextAware;
// import org.springframework.data.jpa.repository.JpaRepository;
// import
// org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
// import
// org.springframework.data.repository.core.support.RepositoryFactorySupport;
//
// import io.arkx.framework.jpa.ContextHolder;
//
// import jakarta.persistence.EntityManager;
// import java.io.Serializable;
//
/// **
// * 基础Repostory简单实现 factory bean
// * 请参考 spring-data-jpa-reference [1.4.2. Adding custom behaviour to all
// repositories]
// *
// *
// * @author <a href="mailto:stormning@163.com">stormning</a>
// * @version V1.0, 2015/8/8.
// */
// public class GenericJpaRepositoryFactoryBean<R extends JpaRepository<T, I>,
// T, I extends Serializable>
// extends JpaRepositoryFactoryBean<R, T, I> implements ApplicationContextAware
// {
//
// public GenericJpaRepositoryFactoryBean(Class<? extends R>
// repositoryInterface) {
// super(repositoryInterface);
// }
//
// @Override
// protected RepositoryFactorySupport createRepositoryFactory(EntityManager
// entityManager) {
// RepositoryFactorySupport factorySupport = new
// GenericJpaRepositoryFactory(entityManager);
// factorySupport.setRepositoryBaseClass(GenericJpaRepositoryImpl.class);
// return factorySupport;
// }
//
// @Override
// public void setApplicationContext(ApplicationContext applicationContext)
// throws BeansException {
// ContextHolder.appContext = applicationContext;
// }
// }
