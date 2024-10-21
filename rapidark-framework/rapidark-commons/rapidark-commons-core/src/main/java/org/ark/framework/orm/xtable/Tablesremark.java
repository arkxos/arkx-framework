package org.ark.framework.orm.xtable;

import com.rapidark.framework.commons.collection.DataRow;

/// <summary>
	/// 此表类似于SQL SERVER数据库的sysobjects表，是sysobjects表的扩展，此表用于存储数据库中各个表反映在本系统中的类的备注信息，即public class 上面的summary注释（本注释即存储于tablesremark表中tablename=tablesremark那一行的remark字段中）
	/// 另可存储如组合主键，第一排序字段，是否静态存储，表类型等表的特征信息
	/// 组合主键为 “alias”   
	/// 最后修改于:2007-10-16 13:22:35 
	/// </summary>
	/// <remarks>
	/// CopyRight：Rapidark SinoSoft. Co. Ltd.
	/// 此类由代码生成器根据表结构生成 2007-10-16 13:22:35
	/// XXX 于 2007-10-16
	///	添加注释
	/// </remarks>
	public  class Tablesremark extends TablesBasic
	{
		//#region 构造函数
		/// <summary>
		/// 此类对应的表名
		/// </summary>
		private  String CnstTableName = "tablesremark";
		/// <summary>
		/// 实例化本类，但不是数据库中的真实数据
		/// </summary>
		public Tablesremark()
		{
			Init(CnstTableName, null);
		}

		/// <summary>
		/// 实例化本类，以传入的数据库中的真实数据的主键值
		/// </summary>
		/// <param name="m_id">该行数据的主键tablename值</param>
		public Tablesremark(Object m_id)
		{
			String sql = "select * from " + CnstTableName + " where "
				+ TableStructList.Get(CnstTableName).getPrimary() + "='" + m_id.toString() + "'";
			DataRow m_dr = SqlHelper.ExecuteDatarow(sql);
			Init(CnstTableName, m_dr);
		}

		/// <summary>
		/// 实例化本类，以传入的数据库中的真实数据的System.Data.DataRow对象
		/// </summary>
		/// <param name="m_dr">该行数据数据的System.Data.DataRow对象</param>
		public Tablesremark(DataRow m_dr)
		{
			Init(CnstTableName, m_dr);
		}
		//#endregion 构造函数

		//#region 字段读取方法
		/// <summary>
		/// Tablename : 表名
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为50字节
		/// </summary>
		public String getTablename()
		{
			
			{
				return GetString(getTablenameColInfo());
			}
		}

		/// <summary>
		/// TablenameColInfo : 对 Tablename 字段的ColInfo对象的访问
		/// 字段Tablename的特性如下:[默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为50字节]
		/// </summary>
		public ColInfo getTablenameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tablename");
			}
		}

		/// <summary>
		/// Addtime : 添加时间
		/// 默认值 : System.DateTime.Now.ToString()
		/// 字段类型 : DATETIME DATETIME
		/// 字段特性 : 不可以为空 
		/// </summary>
		public String getAddtime()
		{
			
			{
				return GetString(getAddtimeColInfo());
			}
		}

		/// <summary>
		/// AddtimeColInfo : 对 Addtime 字段的ColInfo对象的访问
		/// 字段Addtime的特性如下:[默认值 : System.DateTime.Now.ToString()
		/// 字段类型 : DATETIME DATETIME
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getAddtimeColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "addtime");
			}
		}

		/// <summary>
		/// Moder : 最后修改者
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getModer()
		{
			
			{
				return GetInt(getModerColInfo());
			}
		}

		/// <summary>
		/// ModerColInfo : 对 Moder 字段的ColInfo对象的访问
		/// 字段Moder的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getModerColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "moder");
			}
		}

		/// <summary>
		/// Modtime : 最后修改时间
		/// 默认值 : System.DateTime.Now.ToString()
		/// 字段类型 : DATETIME DATETIME
		/// 字段特性 : 不可以为空 
		/// </summary>
		public String getModtime()
		{
			
			{
				return GetString(getModtimeColInfo());
			}
		}

		/// <summary>
		/// ModtimeColInfo : 对 Modtime 字段的ColInfo对象的访问
		/// 字段Modtime的特性如下:[默认值 : System.DateTime.Now.ToString()
		/// 字段类型 : DATETIME DATETIME
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getModtimeColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "modtime");
			}
		}

		/// <summary>
		/// Alias : 别名 别名，例如对于tablename=usr的那一行记录来说，此处可能存储“系统用户”
		/// 默认值 : 未定义
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节
		/// </summary>
		public String getAlias()
		{
			
			{
				return GetString(getAliasColInfo());
			}
		}

		/// <summary>
		/// AliasColInfo : 对 Alias 字段的ColInfo对象的访问
		/// 字段Alias的特性如下:[默认值 : 未定义
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节]
		/// </summary>
		public ColInfo getAliasColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "alias");
			}
		}

		/// <summary>
		/// Remark : 备注 生成类时的注释内容(即对该表的作用解释)
		/// 默认值 : 
		/// 字段类型 : TEXT TEXT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public String getRemark()
		{
			
			{
				return GetString(getRemarkColInfo());
			}
		}

		/// <summary>
		/// RemarkColInfo : 对 Remark 字段的ColInfo对象的访问
		/// 字段Remark的特性如下:[默认值 : 
		/// 字段类型 : TEXT TEXT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getRemarkColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "remark");
			}
		}

		/// <summary>
		/// Primaryex1 : 组合主键1 组合主键1，通过一至多个字段的组合，可能进行对该表的一行数据对应，即相应的列的值不允许同时相同
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节
		/// </summary>
		public String getPrimaryex1()
		{
			
			{
				return GetString(getPrimaryex1ColInfo());
			}
		}

		/// <summary>
		/// Primaryex1ColInfo : 对 Primaryex1 字段的ColInfo对象的访问
		/// 字段Primaryex1的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节]
		/// </summary>
		public ColInfo getPrimaryex1ColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "primaryex1");
			}
		}

		/// <summary>
		/// Primaryex2 : 组合主键2 组合主键2，见组合主键1解释
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节
		/// </summary>
		public String getPrimaryex2()
		{
			
			{
				return GetString(getPrimaryex2ColInfo());
			}
		}

		/// <summary>
		/// Primaryex2ColInfo : 对 Primaryex2 字段的ColInfo对象的访问
		/// 字段Primaryex2的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节]
		/// </summary>
		public ColInfo getPrimaryex2ColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "primaryex2");
			}
		}

		/// <summary>
		/// Ordercol : 第一排序字段 主排序字段，例如“deptid,indexid desc”这样的一个不包含“order by ”的排序字符串
		/// 默认值 : id
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节
		/// </summary>
		public String getOrdercol()
		{
			
			{
				return GetString(getOrdercolColInfo());
			}
		}

		/// <summary>
		/// OrdercolColInfo : 对 Ordercol 字段的ColInfo对象的访问
		/// 字段Ordercol的特性如下:[默认值 : id
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节]
		/// </summary>
		public ColInfo getOrdercolColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "ordercol");
			}
		}

		/// <summary>
		/// Boolusecache : 需静态存储 是否使用静态类存储其所有行的内容，如果使用静态存储，则对此表的Views类来说，频繁读取同一记录不会多次边接数据库，因为静态加载到内存中
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getBoolusecache()
		{
			
			{
				return GetInt(getBoolusecacheColInfo());
			}
		}

		/// <summary>
		/// BoolusecacheColInfo : 对 Boolusecache 字段的ColInfo对象的访问
		/// 字段Boolusecache的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getBoolusecacheColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "boolusecache");
			}
		}

		/// <summary>
		/// Tabletype : 表类型 表类型（下拉选框），参见字典“表类型”的选项意义
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getTabletype()
		{
			
			{
				return GetInt(getTabletypeColInfo());
			}
		}

		/// <summary>
		/// TabletypeColInfo : 对 Tabletype 字段的ColInfo对象的访问
		/// 字段Tabletype的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getTabletypeColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tabletype");
			}
		}

		/// <summary>
		/// Categoryid : 所处目录 所处目录，参见字典项（表所处目录）的意义
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getCategoryid()
		{
			
			{
				return GetInt(getCategoryidColInfo());
			}
		}

		/// <summary>
		/// CategoryidColInfo : 对 Categoryid 字段的ColInfo对象的访问
		/// 字段Categoryid的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getCategoryidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "categoryid");
			}
		}

		/// <summary>
		/// Summarytable : 主表名 summarytable表名（当tabletype=0时此字段忽略），此表对应的summarytable，这个字段在本系统中是保留字段，暂不会使用之
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getSummarytable()
		{
			
			{
				return GetString(getSummarytableColInfo());
			}
		}

		/// <summary>
		/// SummarytableColInfo : 对 Summarytable 字段的ColInfo对象的访问
		/// 字段Summarytable的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getSummarytableColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "summarytable");
			}
		}

		/// <summary>
		/// Summarycol : 与主表关联的外键 本表关联主表(summarytable)的字段，这个字段在本系统中是保留字段，暂不会使用之
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getSummarycol()
		{
			
			{
				return GetString(getSummarycolColInfo());
			}
		}

		/// <summary>
		/// SummarycolColInfo : 对 Summarycol 字段的ColInfo对象的访问
		/// 字段Summarycol的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getSummarycolColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "summarycol");
			}
		}

		/// <summary>
		/// Indexid : 排序号
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getIndexid()
		{
			
			{
				return GetInt(getIndexidColInfo());
			}
		}

		/// <summary>
		/// IndexidColInfo : 对 Indexid 字段的ColInfo对象的访问
		/// 字段Indexid的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getIndexidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "indexid");
			}
		}

		/// <summary>
		/// Tablechanged : 表已改变 开发标识，说明是否是新的未经设定的表(或添加修改过字段的表)，1表示是，0表示否
		/// 默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getTablechanged()
		{
			
			{
				return GetInt(getTablechangedColInfo());
			}
		}

		/// <summary>
		/// TablechangedColInfo : 对 Tablechanged 字段的ColInfo对象的访问
		/// 字段Tablechanged的特性如下:[默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getTablechangedColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tablechanged");
			}
		}

		/// <summary>
		/// Colchanged : 列已改变 开发标识，说明是可能有新的修改过字段的表，1表示是，0表示否
		/// 默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getColchanged()
		{
			
			{
				return GetInt(getColchangedColInfo());
			}
		}

		/// <summary>
		/// ColchangedColInfo : 对 Colchanged 字段的ColInfo对象的访问
		/// 字段Colchanged的特性如下:[默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getColchangedColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "colchanged");
			}
		}

		/// <summary>
		/// Tracelog : 添加时写入日志 向该表添加数据时是否写入日志
		/// 默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getTracelog()
		{
			
			{
				return GetInt(getTracelogColInfo());
			}
		}

		/// <summary>
		/// TracelogColInfo : 对 Tracelog 字段的ColInfo对象的访问
		/// 字段Tracelog的特性如下:[默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getTracelogColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tracelog");
			}
		}
		//#endregion 字段读取方法
}