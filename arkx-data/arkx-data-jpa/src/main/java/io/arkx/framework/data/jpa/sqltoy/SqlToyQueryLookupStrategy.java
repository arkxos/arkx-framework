package io.arkx.framework.data.jpa.sqltoy;

import java.lang.reflect.Method;

import jakarta.persistence.EntityManager;

import jakarta.validation.constraints.NotNull;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.*;

/**
 * <p>模板查询策略</p>
 * @author Darkness
 * @date 2020年10月25日 下午1:46:19
 * @version V1.0
 */
public class SqlToyQueryLookupStrategy implements QueryLookupStrategy {

	protected SqlToyLazyDao sqlToyLazyDao;
	protected final EntityManager entityManager;

	protected QueryLookupStrategy jpaQueryLookupStrategy;

	protected QueryExtractor extractor;

    public SqlToyQueryLookupStrategy(SqlToyLazyDao sqlToyLazyDao, EntityManager entityManager, Key key, QueryExtractor extractor,
									 JpaQueryMethodFactory queryMethodFactory,
									 ValueExpressionDelegate valueExpressionDelegate,
									 QueryRewriterProvider queryRewriterProvider,
									 EscapeCharacter escapeCharacter) {
    	this.sqlToyLazyDao = sqlToyLazyDao;

		this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(
				entityManager,
				queryMethodFactory,
				key,
				new CachingValueExpressionDelegate(valueExpressionDelegate),
				queryRewriterProvider,
				escapeCharacter);

//        this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(
//        		entityManager,
//				new DefaultJpaQueryMethodFactory(extractor),
//				key,
//				evaluationContextProvider,
//				queryRewriterProvider,
//				EscapeCharacter.DEFAULT);
        this.extractor = extractor;
        this.entityManager = entityManager;
    }

    public static QueryLookupStrategy create(SqlToyLazyDao sqlToyLazyDao,
											 EntityManager entityManager,
											 Key key,
											 QueryExtractor extractor,
											 JpaQueryMethodFactory queryMethodFactory,
											 ValueExpressionDelegate valueExpressionDelegate,
											 QueryRewriterProvider queryRewriterProvider,
											 EscapeCharacter escapeCharacter) {
        return new SqlToyQueryLookupStrategy(sqlToyLazyDao, entityManager, key, extractor, queryMethodFactory, valueExpressionDelegate,
				queryRewriterProvider, escapeCharacter);
    }

    @Override
    public RepositoryQuery resolveQuery(@NotNull Method method, RepositoryMetadata metadata, ProjectionFactory factory,
										NamedQueries namedQueries) {
        if (method.getAnnotation(SqlToyQuery.class) != null) {
        	return new SqlToyTemplateQuery(sqlToyLazyDao,
        			method,
            		new DefaultJpaQueryMethodFactory(extractor).build(method, metadata, factory), entityManager);
        } else {
        	return jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        }
    }
}
