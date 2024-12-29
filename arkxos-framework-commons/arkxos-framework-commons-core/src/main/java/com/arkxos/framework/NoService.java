//package com.rapidark.framework;
//
//import java.util.List;
//
//import org.ark.framework.orm.MaxNo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import com.rapidark.framework.commons.collection.ConcurrentMapx;
//import com.rapidark.framework.commons.util.LogUtil;
//import com.rapidark.framework.commons.util.StringUtil;
//import com.rapidark.framework.commons.util.UuidUtil;
//import com.rapidark.framework.data.db.connection.ConnectionPoolManager;
//import com.rapidark.framework.data.db.dbtype.DBTypeService;
//import com.rapidark.framework.data.db.dbtype.MsSql;
//import com.rapidark.framework.data.db.dbtype.MsSql2000;
//
//@Service
//public class NoService {
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	/**
//	 * 得到类型为noType位长为length的编码
//	 */
//	public String getMaxNo(String noType, int length) {
//		return getMaxNo(noType, "SN", length);
//	}
//
//	/**
//	 * 得到类型为noType，位长为length且前缀为prefix的编码
//	 */
//	public String getMaxNo(String noType, String prefix, int length) {
//		long t = getMaxID(noType, prefix, 1);
//		String no = String.valueOf(t);
//		if (no.length() > length) {
//			LogUtil.warn("获取最大编号时发现长度超出预期：NoType=" + noType + ",Length=" + length + ",MaxValue=" + t);
//			return prefix + no.substring(no.length() - length);
//		}
//		return prefix + StringUtil.leftPad(no, '0', length);
//	}
//
//	public long getMaxID(String noType) {
//		return getMaxID(noType, "ID", 1);
//	}
//
//	public long getMaxID(String noType, String subType) {
//		return getMaxID(noType, subType, 1);
//	}
//
//	private ConcurrentMapx<String, long[]> idMap = new ConcurrentMapx<>();
//
//	/**
//	 * 批量获取最大ID。每次调用先占用size个ID并缓存，然后从缓存中取ID，取完后再次获取size个ID，直到程序结束。 <br>
//	 * 本方法特别适用于批量导入数据的场合。
//	 */
//	public synchronized long getMaxID(String noType, int size) {
//		if (size < 1) {
//			return getMaxID(noType, "ID", 1);
//		}
//		long[] p = idMap.get(noType);
//		if (p == null) {
//			p = new long[2];
//			idMap.put(noType, p);
//		}
//		p[0] = p[0] + 1L;
//		if (p[0] > p[1]) {
//			p[1] = getMaxID(noType, "ID", size);
//			p[0] = p[1] - size + 1;
//		}
//		return p[0];
//	}
//
//	/**
//	 * @param noType 类型
//	 * @param subType 子类型
//	 * @param size 一次申请的ID数
//	 * @return
//	 */
//	private long getMaxID(String noType, String subType, int size) {
//		if (size < 1) {
//			size = 1;
//		}
//
//		try {
//			String selectForUpdateSql = getMaxIDQ();
//			List<Long> values = jdbcTemplate.queryForList(selectForUpdateSql, new Object[] {noType, subType}, Long.class);
//			if (values != null && !values.isEmpty()) {
//				long maxValue = values.get(0);
//				long t = maxValue + size;
//				jdbcTemplate.update("update pt_comp__maxno set NoMaxValue=? where NoType=? and NoSubType=?", t, noType, subType);
//				return t;
//			} else {
//				String insertSql = "insert into pt_comp__maxno set id=?, noType=?, noSubType=?, NoMaxValue=?, length=?";
//				MaxNo maxno = new MaxNo();
//				maxno.setNoType(noType);
//				maxno.setNoSubType(subType);
//				maxno.setMaxValue((long)size);
//				maxno.setLength(10L);
//
//				jdbcTemplate.update(insertSql, UuidUtil.base58Uuid(), noType, subType, size, 10L);
//				return size;
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("获取最大号时发生错误:" + e.getMessage());
//		}
//	}
//
//	/**
//	 * 根据数据库类型不同构建SQL不同
//	 *
//	 * @param noType
//	 * @param subType
//	 * @return
//	 */
//	private String getMaxIDQ() {
//		String dbType = ConnectionPoolManager.getDBConnConfig().DBType;
//		String forUpdate = DBTypeService.getInstance().get(dbType).getForUpdate();
//		String sql = "select NoMaxValue from pt_comp__maxno";
//		if (MsSql.ID.equals(dbType) || MsSql2000.ID.equals(dbType)) {
//			sql += forUpdate;
//			sql += " where NoType=? and NoSubType=?";
//		} else {
//			sql += " where NoType=? and NoSubType=?";
//			sql += forUpdate;
//		}
//		return sql;
//	}
//
//}
package com;


