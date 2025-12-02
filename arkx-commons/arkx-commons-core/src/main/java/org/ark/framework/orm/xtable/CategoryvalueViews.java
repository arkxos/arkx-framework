package org.ark.framework.orm.xtable;

import java.util.Hashtable;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;

/// 本类为Categoryvalue类的静态集合类，即：通过本类提供(依主键)对Categoryvalue表的各行数据的实例化Categoryvalue对象的快速索引。
/// 关于Categoryvalue类的解释：[下拉选项，一个Category类的实例应有多个本类的实例化对象（多个下拉选项），例如在category表中存储了“性别”字典，则此表中可能存储“男、女、中性”等几行记录以供操作者录入时选择性别 特别注意：此表的globalid字段亦可作为组合主键来使用，所以在生成的CategoryvalueViews类中的Child方法等加了手工编制的代码，切记不要随便覆盖]
/// 最后修改于:2007-10-16 13:19:28
public class CategoryvalueViews {

	// #region 构造函数
	/// <summary>
	/// 本类为静态类，故不对外开放初始化构造函数
	/// </summary>
	private CategoryvalueViews() {
	}
	// #endregion 构造函数

	// #region 是否使用缓存来保存查询得到的DataTable
	/// <summary>
	/// 是否使用缓存以在内存中保留本表的所有数据
	/// 若此表为工作表，则booleanUseCache应设定为false
	/// 例如：对于工作人员表、组织机构表、下拉选框表等，此处均为true
	/// </summary>
	public static boolean booleanUseCache = true;

	// #endregion 是否使用缓存来保存查询得到的DataTable

	// #region 对集合的存取操作
	private static Object mut = new Object();

	private static Hashtable children = new Hashtable();

	/// <summary>
	/// 根据主键id字段的值取一个Categoryvalue实例化对象，注意：系统忽略大小写（例如Child("Mike")与Child("mike")取得的是同样的返回值）
	/// 注意：若 booleanUseCache==false 则系统每次均会从数据库中读取该信息，所以在.aspx.cs文件中调用时请使用
	/// “Categoryvalue categoryvalue = CategoryvalueViews.Child(m_id);”进行调用
	/// 而不要直接使用CategoryvalueViews.Child(m_id).Update()等属性或方法
	/// </summary>
	public static Categoryvalue Child(Object m_id) {
		if (booleanUseCache == false) {
			String key = MyString.SqlEncode(m_id.toString());
			return Find("id='" + key + "'");
		}
		synchronized (mut) {

			if (children.size() <= getAll().getRowCount()) {
				Add("", new Categoryvalue());
				for (int i = 0; i < getAll().getRowCount(); i++) {
					Add(all.getString(i, "id"), new Categoryvalue(all.get(i)));
					Add(all.getString(i, "categoryid").toLowerCase() + "\r" + all.getString(i, "refid").toLowerCase(),
							(Categoryvalue) children.get(all.getString(i, "id").toLowerCase()));
				}
			}
		}

		if (Exists(m_id)) {
			return (Categoryvalue) children.get(m_id.toString().toLowerCase());
		}
		return (Categoryvalue) children.get("");
	}

	/// <summary>
	/// 根据组合主键categoryid+refid(这些字段的值在Categoryvalue表中是不允许同时相同的，系统忽略大小写)取一个Categoryvalue对象
	/// </summary>
	public static Categoryvalue Child(Object m_Categoryid, Object m_Refid) {
		if (booleanUseCache == false) {
			return Find("categoryid='" + MyString.SqlEncode(m_Categoryid.toString()) + "'" + " and refid='"
					+ MyString.SqlEncode(m_Refid.toString()) + "'");
		}
		return Child(m_Categoryid.toString().trim().toLowerCase() + "\r" + m_Refid.toString().trim().toLowerCase());
	}

	/// <summary>
	/// 根据指定的m_where条件从Categoryvalue表中取得的首行数据的Categoryvalue实例化对象
	/// </summary>
	public static Categoryvalue Find(String m_where) {
		if (booleanUseCache == false) {
			return new Categoryvalue(SqlHelper.ExecuteDatarow("select  top 1 * from Categoryvalue where "
					+ MyString.RuleSqlWhereClause(m_where) + " order by categoryid,sortcode"));
		}
		DataRow[] drs = getAll().select(m_where);
		if (drs.length == 0) {
			return Child("");
		}
		else {
			return Child(drs[0].getString("id"));
		}
	}

	/// <summary>
	/// 判断集合中是否存在 Categoryvalue(id)
	/// </summary>
	private static boolean Exists(Object m_id) {
		return children.contains(m_id.toString().toLowerCase());
	}

	/// <summary>
	/// 向集合中添加一个 Categoryvalue
	/// </summary>
	private static void Add(String m_id, Categoryvalue m_Object) {
		if (Exists(m_id.toLowerCase())) {
			children.remove(m_id.toLowerCase());
		}
		children.put(m_id.toLowerCase(), m_Object);
	}

	/// <summary>
	/// 清空集合中的所有 Categoryvalue
	/// </summary>
	public static void Clear() {
		children.clear();
		all = null;
	}
	// #endregion

	// #region 取得 Categoryvalue 表的某些行信息的 DataTable
	private static DataTable all = null;

	/// <summary>
	/// 取得 Categoryvalue 表的所有信息的 DataTable
	/// </summary>
	public static DataTable getAll() {

		{
			String sql = "select * from Categoryvalue order by categoryid,sortcode";
			if (booleanUseCache) {
				if (all == null) {
					all = SqlHelper.ExecuteDatatable(sql);
				}
				return all;
			}
			return SqlHelper.ExecuteDatatable(sql);
		}
	}

	/// <summary>
	/// 根据指定的条件查找得到 Categoryvalue 表的一个DataTable
	/// </summary>
	/// <param name="m_wheres">where 子句，例如"chinaname like '张%' and
	/// lastlogintime>='2001-1-1'"，建议不包含"where"关键字</param>
	/// <param name="m_orders">order 子句，例如"loginname desc,lastlogintime
	/// asc"等，建议不包含"order by"关键字</param>
	public static DataTable DataTables(String m_wheres, String m_orders) {
		String m_where = MyString.RuleSqlWhereClause(m_wheres);
		String m_order = (m_orders == null || m_orders.trim().length() == 0) ? "" : m_orders.toLowerCase().trim();
		m_order = m_order.replace("  ", " ");
		if (m_order.indexOf("order ") == 0) {
			m_order = m_order.substring(8).trim();
		}
		if (m_order.length() == 0) {
			m_order = "categoryid,sortcode";
		}
		if (booleanUseCache) {
			DataRow[] drs = getAll().Select(m_where, m_order);
			DataTable dt = new DataTable();
			for (int i = 0; i < drs.length; i++) {
				dt.insertRow(drs[i]);
			}
			return dt;
		}
		String sql = "select * from Categoryvalue where " + m_where + " order by " + m_order;
		return SqlHelper.ExecuteDatatable(sql);
	}
	// #endregion

	// #region 取得一系列结果，将该一系列结果采用E文逗号分隔
	/// <summary>
	/// 根据m_wheres条件，取得一系列m_colname值，采用E文逗号分隔
	/// </summary>
	/// <param name="m_colname">要取的字段名</param>
	/// <param name="m_wheres">条件语句</param>
	/// <returns></returns>
	public static String GetListString(String m_colname, String m_wheres) {
		String m_where = MyString.RuleSqlWhereClause(m_wheres);
		DataRow[] drs = getAll().select(m_wheres);

		if (drs.length == 0) {
			return "";
		}
		String returnValue = drs[0].getString(m_colname);
		for (int i = 1; i < drs.length; i++) {
			returnValue += drs[i].getString(m_colname);
		}
		return returnValue;
	}

	// #endregion 取得一系列结果，将该一系列结果采用E文逗号分隔

}
