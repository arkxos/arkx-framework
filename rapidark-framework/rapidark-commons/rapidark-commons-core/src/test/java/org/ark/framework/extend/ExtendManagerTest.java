package org.ark.framework.extend;

import com.rapidark.framework.extend.plugin.PluginManager;
import org.aspectj.lang.annotation.Before;

/**
 * 
 * @author Darkness
 * @date 2012-12-9 下午03:00:06
 * @version V1.0
 */
public class ExtendManagerTest {

	@Before("")
    public void before() {
		PluginManager.initTestPlugin();
	}

}
