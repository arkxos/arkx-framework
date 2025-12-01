package org.ark.framework.orm.sync.source;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:30
 * @since 1.0
 */

import io.arkx.framework.data.jdbc.Session;
import org.ark.framework.orm.Schema;
import org.ark.framework.orm.sync.TableSyncConfig;

import java.sql.Timestamp;
import java.util.List;

/**
 * 数据源接口
 * 抽象不同的数据来源，支持从数据库或已加载的SchemaSet获取数据
 */
public interface DataSource {

    /**
     * 获取数据源名称
     *
     * @return 数据源名称
     */
    String getName();

    /**
     * 从数据源获取增量数据
     *
     * @param tableCode    表代码
     * @param config       同步配置
     * @param session
     * @param lastSyncTime 上次同步时间
     * @return 增量数据列表
     */
    List<Schema> fetchIncrementalData(String tableCode, TableSyncConfig config, Session session, Timestamp lastSyncTime);

    /**
     * 检查数据源中是否包含指定表
     *
     * @param tableCode 表代码
     * @return 是否包含
     */
    boolean containsTable(String tableCode);

    /**
     * 初始化数据源
     */
    void initialize();

    /**
     * 关闭数据源
     */
    void close();
}
