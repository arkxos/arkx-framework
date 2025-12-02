package io.arkx.data.lightning.repository.support;

import java.util.Optional;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import io.arkx.data.lightning.repository.BaseJdbcRepositoryImpl;
import io.arkx.framework.data.common.ContextHolder;

/**
 * @author Nobody
 * @date 2025-07-25 22:56
 * @since 1.0
 */
public class SqlToyRepositoryFactory extends JdbcRepositoryFactory {

    private RelationalMappingContext context;

    /**
     * Creates a new {@link JdbcRepositoryFactory} for the given
     * {@link DataAccessStrategy}, {@link RelationalMappingContext} and
     * {@link ApplicationEventPublisher}.
     *
     * @param dataAccessStrategy
     *            must not be {@literal null}.
     * @param context
     *            must not be {@literal null}.
     * @param converter
     *            must not be {@literal null}.
     * @param dialect
     *            must not be {@literal null}.
     * @param publisher
     *            must not be {@literal null}.
     * @param operations
     *            must not be {@literal null}.
     */
    public SqlToyRepositoryFactory(DataAccessStrategy dataAccessStrategy, RelationalMappingContext context,
            JdbcConverter converter, Dialect dialect, ApplicationEventPublisher publisher,
            NamedParameterJdbcOperations operations) {
        super(dataAccessStrategy, context, converter, dialect, publisher, operations);
        this.context = context;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
        return BaseJdbcRepositoryImpl.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key,
            ValueExpressionDelegate valueExpressionDelegate) {
        // 获取默认策略
        QueryLookupStrategy defaultStrategy = super.getQueryLookupStrategy(key, valueExpressionDelegate)
                .orElseThrow(() -> new IllegalStateException("No default query strategy found"));

        SqlToyLazyDao sqlToyLazyDao = ContextHolder.getBean(SqlToyLazyDao.class);

        QueryLookupStrategy queryLookupStrategy = SqlToyQueryLookupStrategy.create(defaultStrategy, sqlToyLazyDao,
                this.context);
        return Optional.of(queryLookupStrategy);
    }

}
