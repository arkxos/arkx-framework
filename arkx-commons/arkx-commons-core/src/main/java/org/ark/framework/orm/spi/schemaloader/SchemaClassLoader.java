package org.ark.framework.orm.spi.schemaloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @class org.ark.framework.orm.spi.schemaloader.SchemaClassLoader
 * @author Darkness
 * @date 2012-10-24 下午01:30:04
 * @version V1.0
 */
public class SchemaClassLoader implements ISchemaLoader {

	@Override
	public List<String> load(String path) {

		List<String> result = new ArrayList<String>();

		File p = new File(path + "/org/ark/schema");
		if (p.exists()) {
			File[] fs = p.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].getName().endsWith("Schema.class")) {
					result.add("org/ark/schema/" + fs[i].getName());
				}
			}
		}

		return result;
	}

}
