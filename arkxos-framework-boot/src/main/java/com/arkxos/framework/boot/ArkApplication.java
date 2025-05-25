package com.arkxos.framework.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/19 18:59
 */
public class ArkApplication {

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
//        ProxySelector.setDefault(null);
//        System.setProperty("java.security.egd", "file:///dev/urandom");  // the 3 '/' are important to make it a URL

        ConfigurableApplicationContext context =  SpringApplication.run(primarySource, args);

        System.out.println("""
				(♥◠‿◠)ﾉﾞ  Ark App 10.0 启动成功   ლ(´ڡ`ლ)ﾞ
                  _____             _     _                 _
                 |  __ \\           (_)   | |     /\\        | |
                 | |__) |__ _ _ __  _  __| |    /  \\   _ __| | __
                 |  _  // _` | '_ \\| |/ _` |   / /\\ \\ | '__| |/ /
                 | | \\ \\ (_| | |_) | | (_| |  / ____ \\| |  |   < 
                 |_|  \\_\\__,_| .__/|_|\\__,_| /_/    \\_\\_|  |_|\\_\\
                             | |                                 
                             |_|                                 
                """);

        return context;
    }

}
