package org.ark.framework.extend;

import io.arkx.framework.XTest;
import io.arkx.framework.extend.plugin.PluginConfig;
import io.arkx.framework.extend.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**   
 * 
 * @author Darkness
 * @date 2012-11-23 下午04:13:49 
 * @version V1.0   
 */
public class PluginTest extends XTest {

	
	
	/**
	 * 获取所有的插件配置
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午05:25:09 
	 * @version V1.0
	 */
	@Test
	public void getAllPluginConfig(){
		List<PluginConfig> pluginConfigs = PluginManager.getInstance().getAllPluginConfig();
		assertNotNull(pluginConfigs);
	}
}
