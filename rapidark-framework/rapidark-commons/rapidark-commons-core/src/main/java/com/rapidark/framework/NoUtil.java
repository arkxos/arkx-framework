package com.rapidark.framework;

import org.ark.framework.orm.MaxNo;

import com.rapidark.framework.commons.collection.ConcurrentMapx;
import com.rapidark.framework.commons.util.LogUtil;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.data.db.connection.ConnectionConfig;
import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
import com.rapidark.framework.data.db.dbtype.DBTypeService;
import com.rapidark.framework.data.db.dbtype.MsSql;
import com.rapidark.framework.data.db.dbtype.MsSql2000;
import com.rapidark.framework.data.jdbc.Session;
import com.rapidark.framework.data.jdbc.SessionFactory;
import com.rapidark.framework.data.jdbc.SimpleQuery;

/**
 * @class org.ark.framework.orm.NoUtil
 * 最大号工具类
 * @author Darkness
 * @date 2013-1-31 上午11:47:17 
 * @version V1.0
 */
public class NoUtil {
	
	/**
	 * 得到类型为noType位长为length的编码
	 */
	public static String getMaxNo(String noType, int length) {
		long t = getMaxID(noType, "SN", 1);
		String no = String.valueOf(t);
		if (no.length() > length) {
			return no.substring(0, length);
		}
		return StringUtil.leftPad(no, '0', length);
	}

	/**
	 * 得到类型为noType，位长为length且前缀为prefix的编码
	 */
	public static String getMaxNo(String noType, String prefix, int length) {
		long t = getMaxID(noType, prefix, 1);
		String no = String.valueOf(t);
		if (no.length() > length) {
			LogUtil.warn("获取最大编号时发现长度超出预期：NoType=" + noType + ",Length=" + length + ",MaxValue=" + t);
			return no.substring(no.length() - length);
		}
		return prefix + StringUtil.leftPad(no, '0', length);
	}
	
	public static long getMaxID(String noType) {
		return getMaxID(noType, "ID", 1);
	}
	
	public static long getMaxID(String noType, String subType) {
		return getMaxID(noType, subType, 1);
	}

	private static ConcurrentMapx<String, long[]> idMap = new ConcurrentMapx<>();

	/**
	 * 批量获取最大ID。每次调用先占用size个ID并缓存，然后从缓存中取ID，取完后再次获取size个ID，直到程序结束。 <br>
	 * 本方法特别适用于批量导入数据的场合。
	 */
	public synchronized static long getMaxID(String noType, int size) {
		if (size < 1) {
			return getMaxID(noType, "ID", 1);
		}
		long[] p = idMap.get(noType);
		if (p == null) {
			p = new long[2];
			idMap.put(noType, p);
		}
		p[0] = p[0] + 1L;
		if (p[0] > p[1]) {
			p[1] = getMaxID(noType, "ID", size);
			p[0] = p[1] - size + 1;
		}
		return p[0];
	}
	
	/**
	 * @param noType 类型
	 * @param subType 子类型
	 * @param size 一次申请的ID数
	 * @return
	 */
	private static long getMaxID(String noType, String subType, int size) {
		if (size < 1) {
			size = 1;
		}
		
		Session session  = SessionFactory.openSession();
		session.beginTransaction();
		
		try {
			SimpleQuery q = getMaxIDQ(session, noType, subType);
			Object maxValue = q.executeOneValue();
			if (maxValue != null) {
				long t = Long.parseLong(maxValue.toString()) + size;
				q = session.createSimpleQuery("update pt_comp__maxno set NoMaxValue=? where NoType=? and NoSubType=?", t, noType, subType);
				q.executeNoQuery();
				
				session.commit();
				return t;
			} else {
				MaxNo maxno = new MaxNo();
				maxno.setNoType(noType);
				maxno.setNoSubType(subType);
				maxno.setMaxValue((long)size);
				maxno.setLength(10L);
				
				session.save(maxno);
				
				session.commit();
				return size;
			}
		} catch (Exception e) {
			try {
				session.rollback();
				session.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			throw new RuntimeException("获取最大号时发生错误:" + e.getMessage());
		}
	}
	
	/**
	 * 根据数据库类型不同构建SQL不同
	 * 
	 * @param noType
	 * @param subType
	 * @return
	 */
	private static SimpleQuery getMaxIDQ(Session session, String noType, String subType) {
		ConnectionConfig dbcc = ConnectionPoolManager.getDBConnConfig(ConnectionPoolManager.DEFAULT_POOLNAME);
		String forUpdate = DBTypeService.getInstance().get(dbcc.DBType).getForUpdate();
		SimpleQuery q = session.createSimpleQuery("select NoMaxValue from pt_comp__maxno");
		if (MsSql.ID.equals(dbcc.DBType) || MsSql2000.ID.equals(dbcc.DBType)) {
			q.append(forUpdate);
			q.append(" where NoType=? and NoSubType=?", noType, subType);
		} else {
			q.append(" where NoType=? and NoSubType=?", noType, subType);
			q.append(forUpdate);
		}
		return q;
	}
}
