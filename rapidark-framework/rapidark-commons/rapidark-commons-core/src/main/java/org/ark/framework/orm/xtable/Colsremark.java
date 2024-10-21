package org.ark.framework.orm.xtable;

import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.commons.collection.DataTable;

/// <summary>
	/// 各表的各字段的中文释意及英文释意及字段的填写特征等扩展属性，此表与sql server数据库中的系统表syscolumns表作用相似，其存储的信息是对syscolumns表的扩展（syscolumns表中不存在或调用不方便，但本软件中需要用到——例如“字段别名”——，则在此表中存储）
	/// 组合主键为 “tablename+colname”  
	/// 第二组合主键为 “tablename+alias”  
	/// 最后修改于:2007-10-16 13:23:04 
	/// </summary>
	/// <remarks>
	/// CopyRight：Rapidark SinoSoft. Co. Ltd.
	/// 此类由代码生成器根据表结构生成 2007-10-16 13:23:04
	/// XXX 于 2007-10-16
	///	添加注释
	/// </remarks>
	public  class Colsremark extends TablesBasic
	{
		////#region 构造函数
		/// <summary>
		/// 此类对应的表名
		/// </summary>
		private  String CnstTableName = "colsremark";
		/// <summary>
		/// 实例化本类，但不是数据库中的真实数据
		/// </summary>
		public Colsremark()
		{
			Init(CnstTableName, null);
		}

		/// <summary>
		/// 实例化本类，以传入的数据库中的真实数据的主键值
		/// </summary>
		/// <param name="m_id">该行数据的主键id值</param>
		public Colsremark(Object m_id)
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
		public Colsremark(DataRow m_dr)
		{
			Init(CnstTableName, m_dr);
		}
		////#endregion 构造函数

		//#region 字段读取方法
		/// <summary>
		/// Id : 主键 表名 + char(13) + 字段名 作为主键
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为125字节
		/// </summary>
		public String getId()
		{
			
			{
				return GetString(getIdColInfo());
			}
		}

		/// <summary>
		/// IdColInfo : 对 Id 字段的ColInfo对象的访问
		/// 字段Id的特性如下:[默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为125字节]
		/// </summary>
		public ColInfo getIdColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "id");
			}
		}

		/// <summary>
		/// Addtime : 添加时间
		/// 默认值 : System.DateTime.Now.toString()
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
		/// 字段Addtime的特性如下:[默认值 : System.DateTime.Now.toString()
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
		/// 默认值 : System.DateTime.Now.toString()
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
		/// 字段Modtime的特性如下:[默认值 : System.DateTime.Now.toString()
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
		/// Tablename : 表名 表名，对应于sysobjects表中的name
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
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
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getTablenameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tablename");
			}
		}

		/// <summary>
		/// Colname : 字段名 字段名，对应于sysobjects表中的name
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getColname()
		{
			
			{
				return GetString(getColnameColInfo());
			}
		}

		/// <summary>
		/// ColnameColInfo : 对 Colname 字段的ColInfo对象的访问
		/// 字段Colname的特性如下:[默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getColnameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "colname");
			}
		}

		/// <summary>
		/// Alias : 别名 别名，例如对于“tablename=usr and colname=sex”的一行记录（usr表的sex字段），此处可能存储为“性别”，这使得每一表在被读取到页面上进行显示时，可能不需开发人员输入“性别”两字，而是直接调用ColsremarkViews.Child("usr" , "sex").Alias来得到“性别”两字，这样就为系统中对同一字段的各处命名统一提供了可能
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节
		/// </summary>
		public String getAlias()
		{
			
			{
				return GetString(getAliasColInfo());
			}
		}

		/// <summary>
		/// AliasColInfo : 对 Alias 字段的ColInfo对象的访问
		/// 字段Alias的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为100字节]
		/// </summary>
		public ColInfo getAliasColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "alias");
			}
		}

		/// <summary>
		/// Categoryid : 下拉选框 绑定何下拉选框，例如对于“tablename=usr and colname=sex”的一行记录（usr表的sex字段），此处可能存储为“23”，表明此字段绑定到键值为23的字典，这种方式提供了在界面上表现时，可能提供给操作者一个输入框而不是文本方式进行数据录入
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节
		/// </summary>
		public String getCategoryid()
		{
			
			{
				return GetString(getCategoryidColInfo());
			}
		}

		/// <summary>
		/// CategoryidColInfo : 对 Categoryid 字段的ColInfo对象的访问
		/// 字段Categoryid的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节]
		/// </summary>
		public ColInfo getCategoryidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "categoryid");
			}
		}

		/// <summary>
		/// Minvalue : 最小值 最小值，一般对于数字型字段且未绑定下拉选框的字段有意义
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节
		/// </summary>
		public String getMinvalue()
		{
			
			{
				return GetString(getMinvalueColInfo());
			}
		}

		/// <summary>
		/// MinvalueColInfo : 对 Minvalue 字段的ColInfo对象的访问
		/// 字段Minvalue的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节]
		/// </summary>
		public ColInfo getMinvalueColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "minvalue");
			}
		}

		/// <summary>
		/// Maxvalue : 最大值 最大值，解释见minvalue
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节
		/// </summary>
		public String getMaxvalue()
		{
			
			{
				return GetString(getMaxvalueColInfo());
			}
		}

		/// <summary>
		/// MaxvalueColInfo : 对 Maxvalue 字段的ColInfo对象的访问
		/// 字段Maxvalue的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节]
		/// </summary>
		public ColInfo getMaxvalueColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "maxvalue");
			}
		}

		/// <summary>
		/// Multilines : 多行 当为varchar或text时，此值为1表示是多行文本填写
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getMultilines()
		{
			
			{
				return GetInt(getMultilinesColInfo());
			}
		}

		/// <summary>
		/// MultilinesColInfo : 对 Multilines 字段的ColInfo对象的访问
		/// 字段Multilines的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getMultilinesColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "multilines");
			}
		}

		/// <summary>
		/// Rigor : 精度 当为double时，此字段表示显示时小数点后保留几位
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getRigor()
		{
			
			{
				return GetInt(getRigorColInfo());
			}
		}

		/// <summary>
		/// RigorColInfo : 对 Rigor 字段的ColInfo对象的访问
		/// 字段Rigor的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getRigorColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "rigor");
			}
		}

		/// <summary>
		/// Fmttypetime : 时间格式 当为datetime时，此值表示采用何种格式显示时间（下拉列表，可选项为MyDate类中的Fmttype枚举所存储的类型）
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getFmttypetime()
		{
			
			{
				return GetInt(getFmttypetimeColInfo());
			}
		}

		/// <summary>
		/// FmttypetimeColInfo : 对 Fmttypetime 字段的ColInfo对象的访问
		/// 字段Fmttypetime的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getFmttypetimeColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "fmttypetime");
			}
		}

		/// <summary>
		/// Openout : 业务数据 是否是展现给操作者的业务数据（0表示否，即此字段在业务表中不作为可用的填写元素，例如主键列，一般由系统生成，所以其openout属性为0）
		/// 默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getOpenout()
		{
			
			{
				return GetInt(getOpenoutColInfo());
			}
		}

		/// <summary>
		/// OpenoutColInfo : 对 Openout 字段的ColInfo对象的访问
		/// 字段Openout的特性如下:[默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getOpenoutColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "openout");
			}
		}

		/// <summary>
		/// Colchanged : 已改变 开发标识，说明是否是新的未经设定的字段，1表示是，0表示否，此字段在application on start时若发现colsremark表中未能存储syscolumns表中的相应记录，则添加相应记录，colchanged属性即为1
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
		/// Required : 必填项 varchar或text是否要大于0长度,数字或时间是否必须填写,0表示否，1表示是
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getRequired()
		{
			
			{
				return GetInt(getRequiredColInfo());
			}
		}

		/// <summary>
		/// RequiredColInfo : 对 Required 字段的ColInfo对象的访问
		/// 字段Required的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getRequiredColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "required");
			}
		}

		/// <summary>
		/// Validexp : 校验表达式 校验字符串（正则表达式）
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节
		/// </summary>
		public String getValidexp()
		{
			
			{
				return GetString(getValidexpColInfo());
			}
		}

		/// <summary>
		/// ValidexpColInfo : 对 Validexp 字段的ColInfo对象的访问
		/// 字段Validexp的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节]
		/// </summary>
		public ColInfo getValidexpColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "validexp");
			}
		}

		/// <summary>
		/// Multiselected : 多选框 对于下拉选框来说，是否是多选框（对于本系统来说，本字段为一保留字段，暂不会使用之，但对诸如案件的“违法乱纪行为”这样的内容，则可能是“违法乱纪行为多个”）
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getMultiselected()
		{
			
			{
				return GetInt(getMultiselectedColInfo());
			}
		}

		/// <summary>
		/// MultiselectedColInfo : 对 Multiselected 字段的ColInfo对象的访问
		/// 字段Multiselected的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getMultiselectedColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "multiselected");
			}
		}

		/// <summary>
		/// Devinfo : 开发者备注 开发备注，此字段的备注信息，例如，本段文字即存储于本表的“tablename=colsremark and colname=devinfo”行的devinfo字段中
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为1000字节
		/// </summary>
		public String getDevinfo()
		{
			
			{
				return GetString(getDevinfoColInfo());
			}
		}

		/// <summary>
		/// DevinfoColInfo : 对 Devinfo 字段的ColInfo对象的访问
		/// 字段Devinfo的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为1000字节]
		/// </summary>
		public ColInfo getDevinfoColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "devinfo");
			}
		}

		/// <summary>
		/// Inputtype : 业务表字段输入方式 业务表字段输入方式，诸如字符型输入、数字型输入、选择型输入、日期型输入等
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getInputtype()
		{
			
			{
				return GetInt(getInputtypeColInfo());
			}
		}

		/// <summary>
		/// InputtypeColInfo : 对 Inputtype 字段的ColInfo对象的访问
		/// 字段Inputtype的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getInputtypeColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "inputtype");
			}
		}

		/// <summary>
		/// Colorder : 同表中排序号 同一张表中的排序号，此值默认得到的是syscolumns表中的colorder字段值，但允许操作者对其作更改以使表现视图更便于理解
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getColorder()
		{
			
			{
				return GetInt(getColorderColInfo());
			}
		}

		/// <summary>
		/// ColorderColInfo : 对 Colorder 字段的ColInfo对象的访问
		/// 字段Colorder的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getColorderColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "colorder");
			}
		}

		/// <summary>
		/// Tracelog : 启用日志跟踪 启用日志跟踪，当此处存储值为1时，表明此字段的值被更新时，其更新日志被记录于operatelog表中（当然，手工的更新或不通过Sky.Util.TablesBasic类的Update()方法来进行的更新无法被跟踪）
		/// 默认值 : 0
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
		/// 字段Tracelog的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getTracelogColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "tracelog");
			}
		}

		/// <summary>
		/// Usedrefid : 使用该下拉选框的某个子节点以下选框（如果该字段是绑定到下拉选框的）
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节
		/// </summary>
		public String getUsedrefid()
		{
			
			{
				return GetString(getUsedrefidColInfo());
			}
		}

		/// <summary>
		/// UsedrefidColInfo : 对 Usedrefid 字段的ColInfo对象的访问
		/// 字段Usedrefid的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为20字节]
		/// </summary>
		public ColInfo getUsedrefidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "usedrefid");
			}
		}

		/// <summary>
		/// Usedrefidroot : 包含该下拉选框的useRefid的根节点
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getUsedrefidroot()
		{
			
			{
				return GetInt(getUsedrefidrootColInfo());
			}
		}

		/// <summary>
		/// UsedrefidrootColInfo : 对 Usedrefidroot 字段的ColInfo对象的访问
		/// 字段Usedrefidroot的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getUsedrefidrootColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "usedrefidroot");
			}
		}

		/// <summary>
		/// Dynamicbind : 动态绑定0为不绑
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getDynamicbind()
		{
			
			{
				return GetInt(getDynamicbindColInfo());
			}
		}

		/// <summary>
		/// DynamicbindColInfo : 对 Dynamicbind 字段的ColInfo对象的访问
		/// 字段Dynamicbind的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getDynamicbindColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "dynamicbind");
			}
		}
		//#endregion 字段读取方法
		
		/// <summary>
				/// 返回本字段所能使用的下拉选项的DataTable
				/// 若本字段未绑定到下拉选框，则返回new DataTable()
				/// 此属性读取静态类CategoryvalueViews的DataTable
				/// 又因本表是属于静态存储的
				/// 所以即使在调用时不去实例化一个DataTable dt = colsremark.DtCategoryvalue
				/// 也不会影响性能
				/// 因为其最多仅在首次调用时查询一次数据库
				/// </summary>
				public DataTable getDtCategoryvalue()
				{
					
					{
						if (this.getCategoryid().length() < 36)
						{
							return new DataTable();
						}
						String strfilter = "categoryid=" + this.getCategoryid().toString();
						Categoryvalue categoryobj;
						if (this.getUsedrefid().equals("") == false)
						{
							categoryobj = CategoryvalueViews.Child(this.getCategoryid(), this.getUsedrefid());
							if (this.getUsedrefidroot() == 1)
							{
								strfilter += " and sortcode like '" + categoryobj.getSortcode() + "%'";
							}
							else
							{
								strfilter += " and sortcode like '" + categoryobj.getSortcode() + "_%'";
							}
						}
						return CategoryvalueViews.DataTables(strfilter, "sortcode");
					}
				}
}