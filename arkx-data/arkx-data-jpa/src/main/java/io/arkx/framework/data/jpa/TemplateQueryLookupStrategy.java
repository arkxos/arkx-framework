package io.arkx.framework.data.jpa;

import java.lang.reflect.Method;

import jakarta.persistence.EntityManager;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.QueryRewriterProvider;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import io.arkx.framework.data.jpa.annotation.TemplateQuery;
import io.arkx.framework.data.jpa.sqltemplate.freemarker.FreemarkerTemplateQuery;
import io.arkx.framework.data.jpa.sqltoy.SqlToyQueryLookupStrategy;
import org.springframework.data.repository.query.ValueExpressionDelegate;

/**
 * <p>模板查询策略</p>
 * @author Darkness
 * @date 2020年10月25日 下午1:46:19
 * @version V1.0
 */
public class TemplateQueryLookupStrategy extends SqlToyQueryLookupStrategy {

    public TemplateQueryLookupStrategy(SqlToyLazyDao sqlToyLazyDao, EntityManager entityManager, Key key, QueryExtractor extractor,
									   JpaQueryMethodFactory queryMethodFactory,
									   ValueExpressionDelegate valueExpressionDelegate,
									   QueryRewriterProvider queryRewriterProvider,
									   EscapeCharacter escapeCharacter) {
        super(sqlToyLazyDao, entityManager, key, extractor, queryMethodFactory, valueExpressionDelegate,
				queryRewriterProvider, escapeCharacter);
    }

    public static QueryLookupStrategy create(SqlToyLazyDao sqlToyLazyDao, EntityManager entityManager, Key key, QueryExtractor extractor,
											 JpaQueryMethodFactory queryMethodFactory,
											 ValueExpressionDelegate valueExpressionDelegate,
											 QueryRewriterProvider queryRewriterProvider,
											 EscapeCharacter escapeCharacter) {
        return new TemplateQueryLookupStrategy(sqlToyLazyDao, entityManager, key, extractor, queryMethodFactory, valueExpressionDelegate,
				queryRewriterProvider, escapeCharacter);
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
                                        NamedQueries namedQueries) {
        if (method.getAnnotation(TemplateQuery.class) != null) {
        	return new FreemarkerTemplateQuery(
            		new DefaultJpaQueryMethodFactory(extractor).build(method, metadata, factory), entityManager);
        } else {
        	return super.resolveQuery(method, metadata, factory, namedQueries);
        }
    }
}
