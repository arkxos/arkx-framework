package com.arkxos.framework;

import org.aspectj.lang.annotation.Before;

import io.arkx.framework.extend.plugin.ExtendPluginProvider;
import io.arkx.framework.extend.plugin.PluginManager;


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
