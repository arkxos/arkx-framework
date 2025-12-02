package org.ark.framework.orm.spi.schemaloader;

import java.util.List;

/**
 * @class org.ark.framework.orm.spi.schemaloader.ISchemaLoader
 * @author Darkness
 * @date 2012-10-24 下午01:29:26
 * @version V1.0
 */
public interface ISchemaLoader {

	List<String> load(String path);

}
