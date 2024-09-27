package com.rapidark.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/19 18:59
 */
public class RapidArkApplication {

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        ConfigurableApplicationContext context =  SpringApplication.run(primarySource, args);

        System.out.println("(♥◠‿◠)ﾉﾞ  RapidArk 12.0 启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "  _____             _     _                 _    \r\n" +
                " |  __ \\           (_)   | |     /\\        | |   \r\n" +
                " | |__) |__ _ _ __  _  __| |    /  \\   _ __| | __\r\n" +
                " |  _  // _` | '_ \\| |/ _` |   / /\\ \\ | '__| |/ /\r\n" +
                " | | \\ \\ (_| | |_) | | (_| |  / ____ \\| |  |   < \r\n" +
                " |_|  \\_\\__,_| .__/|_|\\__,_| /_/    \\_\\_|  |_|\\_\\\r\n" +
                "             | |                                 \r\n" +
                "             |_|                                 ");

        return context;
    }

}
