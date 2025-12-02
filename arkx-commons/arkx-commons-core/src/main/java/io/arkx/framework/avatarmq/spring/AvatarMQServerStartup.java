package io.arkx.framework.avatarmq.spring;

/**
 * @filename:AvatarMQServerStartup.java
 * @description:AvatarMQServerStartup功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class AvatarMQServerStartup {

	public static void main(String[] args) {
		new AvatarMQContainer().start();
	}

}
