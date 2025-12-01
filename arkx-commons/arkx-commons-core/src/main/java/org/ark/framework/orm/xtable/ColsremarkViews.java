package org.ark.framework.orm.xtable;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;

import java.util.Hashtable;


	/// <summary>
	/// 本类为Colsremark类的静态集合类，即：通过本类提供(依主键)对Colsremark表的各行数据的实例化Colsremark对象的快速索引。
	/// 关于Colsremark类的解释：[各表的各字段的中文释意及英文释意及字段的填写特征等扩展属性，此表与sql server数据库中的系统表syscolumns表作用相似，其存储的信息是对syscolumns表的扩展（syscolumns表中不存在或调用不方便，但本软件中需要用到——例如“字段别名”——，则在此表中存储）]
	/// 最后修改于:2007-10-16 13:23:22 
	/// </summary>
	/// <remarks>
	/// CopyRight：Rapidark Skyinsoft. Co. Ltd.
	/// XXX 于 2007-10-16
	///	初次完成
	/// XXX 于 2007-10-16
	///	添加注释
	/// </remarks>
	public  class ColsremarkViews
	{
		//#region 构造函数
		/// <summary>
		/// 本类为静态类，故不对外开放初始化构造函数
		/// </summary>
		private ColsremarkViews()
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
		/// 根据主键id字段的值取一个Colsremark实例化对象，注意：系统忽略大小写（例如Child("Mike")与Child("mike")取得的是同样的返回值）
		/// 注意：若 booleanUseCache==false 则系统每次均会从数据库中读取该信息，所以在.aspx.cs文件中调用时请使用
		/// “Colsremark colsremark = ColsremarkViews.Child(m_id);”进行调用
		/// 而不要直接使用ColsremarkViews.Child(m_id).Update()等属性或方法
		/// </summary>
		public static Colsremark Child(Object m_id)
		{
			if (booleanUseCache == false)
			{
				String key = (m_id.toString());
				return Find("id='" + key + "'");
			}
			synchronized (mut) {
				if (children.size() <= getAll().getRowCount())
				{
					Add("", new Colsremark());
					for (int i = 0; i < getAll().getRowCount(); i++)
					{
						Add(all.getString(i, "id"), new Colsremark(all.get(i)));
						Add(all.getString(i, "tablename").toLowerCase() + "\r" + all.getString(i, "colname").toLowerCase(), (Colsremark)children.get(all.getString(i, "id").toLowerCase()));
						Add(all.getString(i, "tablename").toLowerCase() + "\r" + all.getString(i, "alias").toLowerCase(), (Colsremark)children.get(all.getString(i, "id").toLowerCase()));
					}
				}
			}
			
			if (Exists(m_id))
			{
				return (Colsremark)children.get(m_id.toString().toLowerCase());
			}
			return (Colsremark)children.get("");
		}

		/// <summary>
		/// 根据组合主键tablename+colname或第二组合主键tablename+alias(这些字段的值在Colsremark表中是不允许同时相同的，系统忽略大小写)取一个Colsremark对象
		///</summary>
		public static Colsremark Child(Object m_Tablename, Object m_Colname)
		{
			if (booleanUseCache == false)
			{
				return Find("tablename='" + (m_Tablename.toString()) + "'" + " and colname='" + (m_Colname.toString()) + "'");
			}
			return Child(m_Tablename.toString().trim().toLowerCase() + "\r" + m_Colname.toString().trim().toLowerCase());
		}

		/// <summary>
		/// 根据指定的m_where条件从Colsremark表中取得的首行数据的Colsremark实例化对象
		/// </summary>
		public static Colsremark Find(String m_where)
		{
			if (booleanUseCache == false)
			{
				return new Colsremark(SqlHelper.ExecuteDatarow("select  top 1 * from Colsremark where " + MyString.RuleSqlWhereClause(m_where) + " order by tablename,colorder"));
			}
			DataRow[] drs = getAll().select(m_where);
			if (drs.length == 0)
			{
				return Child("");
			}
			else
			{
				return Child(drs[0].getString("id"));
			}
		}

		/// <summary>
		/// 判断集合中是否存在 Colsremark(id)
		/// </summary>
		private static boolean Exists(Object m_id)
		{
			return children.contains(m_id.toString().toLowerCase());
		}

		/// <summary>
		/// 向集合中添加一个 Colsremark 
		/// </summary>
		private static void Add(String m_id, Colsremark m_Object)
		{
			if (Exists(m_id.toLowerCase()))
			{
				children.remove(m_id.toLowerCase());
			}
			children.put(m_id.toLowerCase(), m_Object);
		}

		/// <summary>
		/// 清空集合中的所有 Colsremark 
		/// </summary>
		public static void Clear()
		{
			children.clear();
			all = null;
		}
		//#endregion


		//#region 取得 Colsremark 表的某些行信息的 DataTable
		private static DataTable all = null;

		/// <summary>
		/// 取得 Colsremark 表的所有信息的 DataTable
		/// </summary>
		public static DataTable getAll()
		{
			
			{
				String sql = "select * from Colsremark order by tablename,colorder";
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
		/// 根据指定的条件查找得到 Colsremark 表的一个DataTable
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
				m_order = "tablename,colorder";
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
			String sql = "select * from Colsremark where " + m_where + " order by " + m_order;
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
