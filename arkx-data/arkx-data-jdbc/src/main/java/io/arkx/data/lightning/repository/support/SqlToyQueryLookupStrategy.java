package io.arkx.data.lightning.repository.support;

import java.lang.reflect.Method;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.data.jdbc.repository.query.JdbcQueryMethod;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import io.arkx.framework.data.common.sqltoy.SqlToyQuery;
import io.arkx.framework.data.common.sqltoy.SqlToyTemplateQuery;

import jakarta.validation.constraints.NotNull;

/**
 * @author Nobody
 * @date 2025-07-25 22:59
 * @since 1.0
 */
public class SqlToyQueryLookupStrategy implements QueryLookupStrategy {

    protected SqlToyLazyDao sqlToyLazyDao;

    protected QueryLookupStrategy jdbcQueryLookupStrategy;

    private final RelationalMappingContext context;

    public SqlToyQueryLookupStrategy(QueryLookupStrategy defaultStrategy, SqlToyLazyDao sqlToyLazyDao,
            RelationalMappingContext context) {
        this.context = context;
        this.sqlToyLazyDao = sqlToyLazyDao;

        this.jdbcQueryLookupStrategy = defaultStrategy;

    }

    public static QueryLookupStrategy create(QueryLookupStrategy defaultStrategy, SqlToyLazyDao sqlToyLazyDao,
            RelationalMappingContext context) {
        return new SqlToyQueryLookupStrategy(defaultStrategy, sqlToyLazyDao, context);
    }

    @Override
    public RepositoryQuery resolveQuery(@NotNull Method method, RepositoryMetadata metadata, ProjectionFactory factory,
            NamedQueries namedQueries) {
        if (method.isAnnotationPresent(SqlToyQuery.class)) {
            return createSqlToyQuery(method, metadata, factory, namedQueries);
        } else {
            return jdbcQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
        }
    }

    private RepositoryQuery createSqlToyQuery(Method method, RepositoryMetadata repositoryMetadata,
            ProjectionFactory projectionFactory, NamedQueries namedQueries) {

        // 使用Spring的JpaQueryMethodFactory
        JdbcQueryMethod queryMethod = new JdbcQueryMethod(method, repositoryMetadata, projectionFactory, namedQueries,
                this.context);

        return new SqlToyTemplateQuery(sqlToyLazyDao, method, queryMethod);
    }

}
