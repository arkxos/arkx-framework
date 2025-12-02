package org.ark.framework.orm.connection;

import java.util.ArrayList;
import java.util.List;

/**
 * @class org.ark.framework.orm.connection.ServerTypes 服务器类型
 * @author Darkness
 * @date May 19, 2011 11:20:45 PM
 * @version 1.0.0
 */
public class ServerTypes {

	private String serverType;

	private ServerTypes() {
	}

	private ServerTypes(String serverType) {
		this.serverType = serverType;
	}

	public static ServerTypes valueOf(String serverType) {
		return ServerTypes.getServerType(serverType);
	}

	/**
	 * 获取数据库类型
	 * @param dbType
	 * @return DBTypes
	 * @exception
	 * @since 1.0.0
	 */
	public static ServerTypes getServerType(String serverType) {
		for (int i = 0; i < serverTypes.size(); i++) {
			ServerTypes type = serverTypes.get(i);
			if (type.toString().equals(serverType)) {
				return type;
			}
		}

		ServerTypes type = new ServerTypes(serverType);
		serverTypes.add(type);
		return type;
	}

	public static final ServerTypes Tomcat = new ServerTypes("Tomcat");

	public static final ServerTypes Weblogic = new ServerTypes("Weblogic");

	public static final ServerTypes WebSphere = new ServerTypes("WebSphere");

	public static final ServerTypes JBoss = new ServerTypes("JBoss");

	public static List<ServerTypes> serverTypes = new ArrayList<ServerTypes>();

	static {
		serverTypes.add(Tomcat);
		serverTypes.add(Weblogic);
		serverTypes.add(WebSphere);
		serverTypes.add(JBoss);
	}

	@Override
	public String toString() {
		return this.serverType;
	}

}
