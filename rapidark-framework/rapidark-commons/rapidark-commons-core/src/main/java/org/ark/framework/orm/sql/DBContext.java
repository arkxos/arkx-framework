package org.ark.framework.orm.sql;

/**
 * @class org.ark.framework.orm.sql.DBContext
 * @author Darkness
 * @date 2011-5-27 下午04:54:22
 * @version V1.0
 */
public class DBContext {

	/**
	 * ThreadLocal object for storing object in current thread.
	 */
	private static ThreadLocal<DBContext> tl = new ThreadLocal<DBContext>();

	/**
	 * Set current context
	 */
	static public void setCurrentContext(String poolName) {
		DBContext c = getCurrentContext();
		if (c == null) {
			c = new DBContext(poolName);
			tl.set(c);
		} else {
			c.poolName = poolName;
		}
	}
	
	public String getPoolName() {
		return this.poolName;
	}

	/**
	 * Get current context value
	 * 
	 * @return The current context
	 */
	static public DBContext getCurrentContext() {
		return (DBContext) tl.get();
	}

	// ----------------------------------------------------------
	//
	// Class members
	//
	// ----------------------------------------------------------

	private String poolName;

	/**
	 * The constructor is private, to get an instance of the Context, please
	 * use getCurrentContext() method.
	 */
	private DBContext(String poolName) {
		this.poolName = poolName;
	}

}

