package org.ark.framework.extend;

import org.aspectj.lang.annotation.Before;

import io.arkx.framework.extend.plugin.PluginManager;

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
