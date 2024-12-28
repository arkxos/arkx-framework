package org.ark.framework.orm.xtable;

import java.util.Hashtable;

import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.commons.collection.DataTable;


	/// <summary>
	/// 本类为Tablesremark类的静态集合类，即：通过本类提供(依主键)对Tablesremark表的各行数据的实例化Tablesremark对象的快速索引。
	/// 关于Tablesremark类的解释：[此表类似于SQL SERVER数据库的sysobjects表，是sysobjects表的扩展，此表用于存储数据库中各个表反映在本系统中的类的备注信息，即public class 上面的summary注释（本注释即存储于tablesremark表中tablename=tablesremark那一行的remark字段中） 另可存储如组合主键，第一排序字段，是否静态存储，表类型等表的特征信息]
	/// 最后修改于:2007-10-16 13:22:50 
	/// </summary>
	/// <remarks>
	/// CopyRight：Rapidark Skyinsoft. Co. Ltd.
	/// XXX 于 2007-10-16
	///	初次完成
	/// XXX 于 2007-10-16
	///	添加注释
	/// </remarks>
	public  class TablesremarkViews
	{
		//#region 构造函数
		/// <summary>
		/// 本类为静态类，故不对外开放初始化构造函数
		/// </summary>
		private TablesremarkViews()
		{
		}
		//#endregion 构造函数

		//#region 是否使用缓存来保存查询得到的DataTable
		/// <summary>
		/// 是否使用缓存以在内存中保留本表的所有数据
		/// 若此表为工作表，则booleanUseCache应设定为false
		/// 例如：对于工作人员表、组织机构表、下拉选框表等，此处均为true
		/// </summary>
		public static  boolean booleanUseCache = true;
		//#endregion 是否使用缓存来保存查询得到的DataTable

		//#region 对集合的存取操作
		private static Object mut = new Object();
		private static Hashtable children = new Hashtable();
		/// <summary>
		/// 根据主键tablename字段(或者alias字段)的值取一个Tablesremark实例化对象，注意：系统忽略大小写（例如Child("Mike")与Child("mike")取得的是同样的返回值）
		/// 注意：若 booleanUseCache==false 则系统每次均会从数据库中读取该信息，所以在.aspx.cs文件中调用时请使用
		/// “Tablesremark tablesremark = TablesremarkViews.Child(m_id);”进行调用
		/// 而不要直接使用TablesremarkViews.Child(m_id).Update()等属性或方法
		/// </summary>
		public static Tablesremark Child(Object m_id)
		{
			if (booleanUseCache == false)
			{
				String key = m_id.toString();
				return Find("tablename='" + key + "' or alias='" + key + "'");
			}
			synchronized (mut) {
				if (children.size() <= getAll().getRowCount())
				{
					Add("", new Tablesremark());
					for (int i = 0; i < getAll().getRowCount(); i++)
					{
						Add(all.getString(i, "tablename"), new Tablesremark(all.get(i)));
						Add(all.getString(i, "alias").toLowerCase(), (Tablesremark)children.get(all.getString(i, "tablename").toLowerCase()));
					}
				}
			}
			
			if (Exists(m_id))
			{
				return (Tablesremark)children.get(m_id.toString().toLowerCase());
			}
			return (Tablesremark)children.get("");
		}

		/// <summary>
		/// 根据指定的m_where条件从Tablesremark表中取得的首行数据的Tablesremark实例化对象
		/// </summary>
		public static Tablesremark Find(String m_where)
		{
			if (booleanUseCache == false)
			{
				return new Tablesremark(SqlHelper.ExecuteDatarow("select  top 1 * from Tablesremark where " + MyString.RuleSqlWhereClause(m_where) + " order by tablename"));
			}
			DataRow[] drs = getAll().select(m_where);
			if (drs.length == 0)
			{
				return Child("");
			}
			else
			{
				return Child(drs[0].get("tablename").toString());
			}
		}

		/// <summary>
		/// 判断集合中是否存在 Tablesremark(id)
		/// </summary>
		private static boolean Exists(Object m_id)
		{
			return children.contains(m_id.toString().toLowerCase());
		}

		/// <summary>
		/// 向集合中添加一个 Tablesremark 
		/// </summary>
		private static void Add(String m_id, Tablesremark m_object)
		{
			if (Exists(m_id.toLowerCase()))
			{
				children.remove(m_id.toLowerCase());
			}
			children.put(m_id.toLowerCase(), m_object);
		}

		/// <summary>
		/// 清空集合中的所有 Tablesremark 
		/// </summary>
		public static void Clear()
		{
			children.clear();
			all = null;
		}
		//#endregion


		//#region 取得 Tablesremark 表的某些行信息的 DataTable
		private static DataTable all = null;

		/// <summary>
		/// 取得 Tablesremark 表的所有信息的 DataTable
		/// </summary>
		public static DataTable getAll()
		{
			
			{
				String sql = "select * from Tablesremark order by tablename";
				if (booleanUseCache)
				{
					if (all == null)
					{
						all = SqlHelper.ExecuteDatatable(sql);
					}
					return all;
				}
				return SqlHelper.ExecuteDatatable(sql);
			}
		}

		/// <summary>
		/// 根据指定的条件查找得到 Tablesremark 表的一个DataTable
		/// </summary>
		/// <param name="m_wheres">where 子句，例如"chinaname like '张%' and lastlogintime>='2001-1-1'"，建议不包含"where"关键字</param>
		/// <param name="m_orders">order 子句，例如"loginname desc,lastlogintime asc"等，建议不包含"order by"关键字</param>
		public static DataTable DataTables(String m_wheres, String m_orders)
		{
			String m_where = MyString.RuleSqlWhereClause(m_wheres);
			String m_order = (m_orders == null || m_orders.trim().length() == 0) ? "" : m_orders.toLowerCase().trim();
			m_order = m_order.replace("  ", " ");
			if (m_order.indexOf("order ") == 0)
			{
				m_order = m_order.substring(8).trim();
			}
			if (m_order.length() == 0)
			{
				m_order = "tablename";
			}
			if (booleanUseCache)
			{
				DataRow[] drs = getAll().Select(m_where, m_order);
				DataTable dt = new DataTable();
				for (int i = 0; i < drs.length; i++)
				{
					dt.insertRow(drs[i]);
				}
				return dt;
			}
			String sql = "select * from Tablesremark where " + m_where + " order by " + m_order;
			return SqlHelper.ExecuteDatatable(sql);
		}
		//#endregion

		//#region 取得一系列结果，将该一系列结果采用E文逗号分隔
		/// <summary>
		/// 根据m_wheres条件，取得一系列m_colname值，采用E文逗号分隔
		/// </summary>
		/// <param name="m_colname">要取的字段名</param>
		/// <param name="m_wheres">条件语句</param>
		/// <returns></returns>
		public static String GetListString(String m_colname, String m_wheres)
		{
			String m_where = MyString.RuleSqlWhereClause(m_wheres);
			DataRow[] drs = getAll().select(m_wheres);

			if (drs.length == 0)
			{
				return "";
			}
			String returnValue = drs[0].getString(m_colname);
			for (int i = 1; i < drs.length; i++)
			{
				returnValue += drs[i].getString(m_colname);
			}
			return returnValue;
		}

		//#endregion 取得一系列结果，将该一系列结果采用E文逗号分隔
}