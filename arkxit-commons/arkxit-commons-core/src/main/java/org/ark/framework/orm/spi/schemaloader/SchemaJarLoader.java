package org.ark.framework.orm.spi.schemaloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.ZipUtil;


/**   
 * @class org.ark.framework.orm.spi.schemaloader.SchemaJarLoader
 * @author Darkness
 * @date 2012-10-24 下午01:30:15 
 * @version V1.0   
 */
public class SchemaJarLoader implements ISchemaLoader {

	@Override
	public List<String> load(String path) {
		
		List<String> result = new ArrayList<String>();
		
		if (new File(path).exists()) {
			File[] fs = new File(path).listFiles();
			for (File f : fs) {
				if (f.getName().indexOf("-plugin-") < 0)
					continue;
				if(!f.getName().endsWith(".jar")) {
					continue;
				}
				try {
					 Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
					for (String fileName : files.keyArray())
						if ((fileName.startsWith("org/ark/schema")) && (fileName.endsWith("Schema.class")))
							result.add(fileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

}
