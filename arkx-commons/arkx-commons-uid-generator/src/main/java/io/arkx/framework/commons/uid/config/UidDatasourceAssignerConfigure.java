package io.arkx.framework.commons.uid.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import io.arkx.framework.commons.uid.constant.Mode;
import io.arkx.framework.commons.uid.worker.DatasourceWorkerIdAssigner;
import io.arkx.framework.commons.uid.worker.WorkerIdAssigner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 线上环境依赖于数据库生成WorkId,用于自动生成其对应的WorkId以及避免重复，
 * Spring容器中必须存在SQLSessionFactory相关Bean，才能启用以下两种环境
 *
 * @author MrDJun 2021/5/6
 */
@Slf4j
@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(MybatisPlusProperties.class)
@ConditionalOnProperty(value = "arkx.uid.assigner-mode", havingValue = Mode.DB)
public class UidDatasourceAssignerConfigure {

    @ConditionalOnMissingBean(SqlSessionFactory.class)
    @Bean(name = "sqlSessionFactory")
    @ConditionalOnBean(DataSource.class)
    public SqlSessionFactory sqlSessionFactory(DataSource ds, MybatisPlusProperties properties) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(ds);
        // 解决 application.properties 中 配置mybatis.mapper-locations 失效的问题
        sqlSessionFactory.setMapperLocations(ArrayUtils.addAll(properties.resolveMapperLocations(),
                new PathMatchingResourcePatternResolver().getResources("classpath*:/META-INF/mapper/*.xml")));
        return sqlSessionFactory.getObject();
    }

    /**
     * 基于DB生成节点ID（主选）
     */
    @Bean
    @ConditionalOnBean(SqlSessionFactory.class)
    @ConditionalOnMissingBean(DatasourceWorkerIdAssigner.class)
    public WorkerIdAssigner disposableWorkerIdAssigner(SqlSessionFactory sqlSessionFactory) {
        log.info("WorkerIdAssigner turn on datasource");
        return new DatasourceWorkerIdAssigner(sqlSessionFactory);
    }
}