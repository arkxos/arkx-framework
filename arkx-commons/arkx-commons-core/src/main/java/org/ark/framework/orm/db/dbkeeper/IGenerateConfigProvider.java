package org.ark.framework.orm.db.dbkeeper;

/**
 * @class org.ark.framework.orm.db.dbkeeper.IGenerateConfigProvider
 * @author Darkness
 * @date 2012-9-19 上午9:52:03
 * @version V1.0
 */
public interface IGenerateConfigProvider {

    String getHost();

    String getDatabase();

    String getUserName();

    String getPassword();

    String getTableNames();

    String getPackageName();

    String getDir();

}
