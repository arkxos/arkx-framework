package io.arkx.framework.data.jpa.sqltoy;

import java.lang.reflect.Method;

import io.arkx.framework.data.common.sqltoy.SqlToyQuery;
import io.arkx.framework.data.common.sqltoy.SqlToyTemplateQuery;
import jakarta.persistence.EntityManager;

import jakarta.validation.constraints.NotNull;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.data.jpa.provider.PersistenceProvider;
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

    public SqlToyQueryLookupStrategy(QueryLookupStrategy defaultStrategy,
									 SqlToyLazyDao sqlToyLazyDao,
									 EntityManager entityManager,
									 Key key,
									 QueryExtractor extractor,
									 JpaQueryMethodFactory queryMethodFactory,
									 ValueExpressionDelegate valueExpressionDelegate,
									 QueryRewriterProvider queryRewriterProvider,
									 EscapeCharacter escapeCharacter) {
    	this.sqlToyLazyDao = sqlToyLazyDao;

		this.jpaQueryLookupStrategy = defaultStrategy;

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

    public static QueryLookupStrategy create(QueryLookupStrategy defaultStrategy,
											 SqlToyLazyDao sqlToyLazyDao,
											 EntityManager entityManager,
											 Key key,
											 QueryExtractor extractor,
											 JpaQueryMethodFactory queryMethodFactory,
											 ValueExpressionDelegate valueExpressionDelegate,
											 QueryRewriterProvider queryRewriterProvider,
											 EscapeCharacter escapeCharacter) {
        return new SqlToyQueryLookupStrategy(defaultStrategy,sqlToyLazyDao, entityManager, key, extractor, queryMethodFactory, valueExpressionDelegate,
				queryRewriterProvider, escapeCharacter);
    }

    @Override
    public RepositoryQuery resolveQuery(@NotNull Method method,
										RepositoryMetadata metadata,
										ProjectionFactory factory,
										NamedQueries namedQueries) {
        if (method.isAnnotationPresent(SqlToyQuery.class)) {
			return createSqlToyQuery(method, metadata, factory);
//        	return new SqlToyTemplateQuery(sqlToyLazyDao,
//        			method,
//            		new DefaultJpaQueryMethodFactory(extractor)
//							.build(method, metadata, factory), entityManager);
        } else {
        	return jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        }
    }

	private RepositoryQuery createSqlToyQuery(
			Method method,
			RepositoryMetadata metadata,
			ProjectionFactory factory) {

		// 使用Spring的JpaQueryMethodFactory
		JpaQueryMethod queryMethod = new DefaultJpaQueryMethodFactory(
				PersistenceProvider.fromEntityManager(entityManager)
		).build(method, metadata, factory);

		return new SqlToyTemplateQuery(sqlToyLazyDao, method, queryMethod, entityManager);
	}
}
