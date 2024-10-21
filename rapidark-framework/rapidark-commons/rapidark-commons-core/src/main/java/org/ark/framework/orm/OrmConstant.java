package org.ark.framework.orm;

import java.lang.reflect.Method;

import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.data.db.connection.ConnectionPoolManager;


/**   
 * @class org.ark.framework.orm.OrmConstant
 * @author Darkness
 * @date 2012-3-8 下午2:13:49 
 * @version V1.0   
 */
public class OrmConstant {

	public static final String GlobalCharset = "UTF-8";
	
	public static String getContainerInfo() {
		return getValue("System.ContainerInfo");
	}

	private static String getValue(String string) {
		// TODO Auto-generated method stub
		return "";
	}

	public static String getContainerVersion() {
		String str = getValue("System.ContainerInfo");
		if (str.indexOf("/") > 0) {
			return str.substring(str.lastIndexOf("/") + 1);
		}
		return "0";
	}
	
	public static boolean isDB2() {
		return ConnectionPoolManager.getDBConnConfig().getDatabaseType().equals("DB2");
	}

	public static boolean isOracle() {
		return ConnectionPoolManager.getDBConnConfig().getDatabaseType().equals("ORACLE");
	}

	public static boolean isMysql() {
		return ConnectionPoolManager.getDBConnConfig().getDatabaseType().equals("MYSQL");
	}

	public static boolean isSQLServer() {
		return ConnectionPoolManager.getDBConnConfig().getDatabaseType().equals("MSSQL");
	}

	public static boolean isSybase() {
		return ConnectionPoolManager.getDBConnConfig().getDatabaseType().equals("SYBASE");
	}

	public static boolean isTomcat() {
		if (StringUtil.isEmpty(getContainerInfo())) {
			getJBossInfo();
		}
		return getContainerInfo().toLowerCase().indexOf("tomcat") >= 0;
	}

	

	public static boolean isJboss() {
		if (StringUtil.isEmpty(getContainerInfo())) {
			getJBossInfo();
		}
		return getContainerInfo().toLowerCase().indexOf("jboss") >= 0;
	}

	public static boolean isWeblogic() {
		return getContainerInfo().toLowerCase().indexOf("weblogic") >= 0;
	}

	public static boolean isWebSphere() {
		return getContainerInfo().toLowerCase().indexOf("websphere") >= 0;
	}
	
	public static void getJBossInfo() {
		String jboss = System.getProperty("jboss.home.dir");
		if (StringUtil.isNotEmpty(jboss))
			try {
				Class c = Class.forName("org.jboss.Version");
				Method m = c.getMethod("getInstance", null);
				Object o = m.invoke(null, null);
				m = c.getMethod("getMajor", null);
				Object major = m.invoke(o, null);
				m = c.getMethod("getMinor", null);
				Object minor = m.invoke(o, null);
				m = c.getMethod("getRevision", null);
				Object revision = m.invoke(o, null);
				m = c.getMethod("getTag", null);
				Object tag = m.invoke(o, null);
				configMap.put("System.ContainerInfo", "JBoss/" + major + "."
						+ minor + "." + revision + "." + tag);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static Mapx<String, String> configMap = new Mapx<String, String>();
	
}

