package org.ark.framework.orm.xtable;

import org.apache.tools.ant.types.resources.selectors.Date;

/// 应用程序使用过程中操作日志统一处理程序。
/// 对各表的各行数据的操作写入日志。
public class Log {
	// / 本类是静态类，故不提供实例化方法
	private Log() {
	}

	// / 确定该 tableName 的 colName 是否需要写入日志。
	// / <param name="tableName"></param>
	// / <param name="colName"></param>
	// / <returns>true or false</returns>
	public static boolean ExistsDic(String tableName, String colName) {
		if (colName.equals("0") && TablesremarkViews.Child(tableName).getTracelog() > 0) {
			// 添加数据，此时colName = "0"
			return true;
		} else if (tableName.toLowerCase().equals("usr") && colName.length() == 1 && MyString.IsInt(colName)) {
			// 登录或初始化密码等安全日志
			return true;
		} else {
			// 字段更新操作日志
			return ColsremarkViews.Child(tableName, colName).getTracelog() > 0;
		}
	}

	// / <summary>
	// / 向日志表插入一行操作日志
	// / 注意：colName若赋值为0-9为保留信息，一般不是真实的字段
	// / 其中目前已使用：0-插入该行数据
	// / </summary>
	// / <param name="operateUser">操作者ID</param>
	// / <param name="dutyUser">责任者ID</param>
	// / <param name="m_OperateGlobalId">操作序列ID</param>
	// / <param name="tableName">表名</param>
	// / <param name="rowId">行ID</param>
	// / <param name="colName">列名</param>
	// / <param name="oldValue">原值</param>
	// / <param name="newValue">新值</param>
	// / <returns></returns>
	public static boolean Add(Object operateUser, Object dutyUser, String m_OperateGlobalId, String tableName, Object rowId, String colName, Object oldValue, Object newValue) {
		if (ExistsDic(tableName, colName) == false)
			return false;
		String oldValueE = oldValue.toString().trim();
		String newValueE = newValue.toString().trim();
		if (ColInfos.Get(tableName, colName).getSimpleType() == SimpleTypes.DOUBLE || ColInfos.Get(tableName, colName).getSimpleType() == SimpleTypes.INT) {
			if (MyString.IsDouble(newValueE) == false) {
				newValueE = "0";
			}
		}

		// 细化
		if (colName.length() > 1 && ColInfos.Get(tableName, colName).getSqlType() == SqlTypes.DATETIME
				&& oldValueE.equals(newValueE)) {
			// 日期型值相等，比如传递过来2005-4-3 与 2005-4-3 0:00:00
			return false;
		} else if (colName.length() > 1 && (ColInfos.Get(tableName, colName).getSimpleType() == SimpleTypes.DOUBLE || ColInfos.Get(tableName, colName).getSimpleType() == SimpleTypes.INT)
				&& Math.abs(Double.parseDouble(oldValueE) - Double.parseDouble(newValueE)) < 0.0000001) {
			// 数字型值相等，比如传递过来0 与 0.00
			return false;
		} else if (colName.length() > 1 && MyString.booleanChanged(oldValueE, newValueE) == false) {
			return false;
		}

		String sql = "insert into operatelog (operateUser,dutyUser,OperateGlobalId,tableName,rowId,colName,oldValue,newValue,logTime) values (" + operateUser + "," + dutyUser + ",'"
				+ m_OperateGlobalId + "','" + tableName + "','" + rowId.toString() + "','" + colName + "','" + oldValueE + "','"
				+ newValueE + "','" + new Date() + "')";
		SqlHelper.executeNonQuery(sql);
		return true;
	}
}
