package com.rapidark.framework;

import com.rapidark.framework.extend.plugin.ExtendPluginProvider;
import com.rapidark.framework.extend.plugin.PluginManager;
import org.aspectj.lang.annotation.Before;


/**   
 * 
 * @author Darkness
 * @date 2012-12-10 下午03:49:17 
 * @version V1.0   
 */
public class XTest {
	
	@Before("")
    public void before() {
		
		Config.setPluginContext(true);
		Config.withTestMode();
		
		PluginManager.initTestPlugin();
		ExtendPluginProvider.getInstance().start();
		
		init();
	}
	
	public void init(){}
}
