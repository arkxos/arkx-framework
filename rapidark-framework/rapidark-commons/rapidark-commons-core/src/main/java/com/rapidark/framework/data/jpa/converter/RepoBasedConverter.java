package com.rapidark.framework.data.jpa.converter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.support.Repositories;

import com.rapidark.framework.data.jpa.BaseRepository;
import com.rapidark.framework.data.jpa.auditing.CachingJpaRepository;

/**
 * .
 *
 * @author stormning on 16/6/16.
 */
@SuppressWarnings({"unchecked"})
public abstract class RepoBasedConverter<S, D, ID extends Serializable> extends AbstractConverter<S, D, ID> implements
        ApplicationContextAware {

    private Repositories repositories;

    private BaseRepository<S, ID> genericJpaRepository;

    private EntityInformation<S, ID> entityInformation;

    private boolean useCache = false;

    @Override
    protected ID getId(S source) {
        return entityInformation.getId(source);
    }

    @Override
    protected S internalGet(ID id) {
        Optional<S> optional = genericJpaRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    protected Map<ID, S> internalMGet(Collection<ID> ids) {
        if (useCache) {
            return genericJpaRepository.mgetOneByOne(ids);
        }
        return genericJpaRepository.mget(ids);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(this.getClass(), RepoBasedConverter.class);
        Class<?> clazz = classes[0];
        this.repositories = new Repositories(context);
        this.entityInformation = repositories.getEntityInformationFor(clazz);
        this.genericJpaRepository = (BaseRepository<S, ID>) repositories.getRepositoryFor(clazz).orElse(null);
        this.useCache = genericJpaRepository instanceof CachingJpaRepository;
    }
}
