package org.ark.framework.orm.xtable;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;

/// <summary>
	/// 下拉选框表，系统中对每一处可能用到的下拉选框，应对应一个本类，这些下拉选框在客户处的说法可能是“指标体系”，但下拉选框中存储了一些开发级定制的字典信息，这些信息对客户来说是透明的，但系统需要，例如“小数位数”，这并不是指标体系的内容，但系统中要用到这样的字典，则这样的内容均利用category及categoryvalue表进行存储，category与categoryvalue的关系，例如“性别”与“男”的关系，即category表中的一行数据，在categoryvalue表中可能有多行记录
	/// 组合主键为 “constname”  
	/// 第二组合主键为 “chinaname”  
	public  class Category extends TablesBasic
	{
		//#region 构造函数
		/// <summary>
		/// 此类对应的表名
		/// </summary>
		private  String CnstTableName = "category";
		/// <summary>
		/// 实例化本类，但不是数据库中的真实数据
		/// </summary>
		public Category()
		{
			Init(CnstTableName, null);
		}

		/// <summary>
		/// 实例化本类，以传入的数据库中的真实数据的主键值
		/// </summary>
		/// <param name="m_id">该行数据的主键id值</param>
		public Category(Object m_id)
		{
			String sql = "select * from " + CnstTableName + " where "
				+ TableStructList.Get(CnstTableName).getPrimary() + "='" + m_id + "'";
			DataRow m_dr = SqlHelper.ExecuteDatarow(sql);
			Init(CnstTableName, m_dr);
		}

		/// <summary>
		/// 实例化本类，以传入的数据库中的真实数据的System.Data.DataRow对象
		/// </summary>
		/// <param name="m_dr">该行数据数据的System.Data.DataRow对象</param>
		public Category(DataRow m_dr)
		{
			Init(CnstTableName, m_dr);
		}
		//#endregion 构造函数

		//#region 字段读取方法
		/// <summary>
		/// Id : 主键 主键,作为该类别的唯一标识符
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为40字节
		/// </summary>
		public String getId()
		{
			
			{
				return GetString(getIdColInfo());
			}
		}

		/// <summary>
		/// IdColInfo : 对 Id 字段的ColInfo对象的访问
		/// 字段Id的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  该字段是本表的主键  限制长度为40字节]
		/// </summary>
		public ColInfo getIdColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "id");
			}
		}

		/// <summary>
		/// Adder : 添加者
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getAdder()
		{
			
			{
				return GetInt(getAdderColInfo());
			}
		}

		/// <summary>
		/// AdderColInfo : 对 Adder 字段的ColInfo对象的访问
		/// 字段Adder的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getAdderColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "adder");
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
		/// Delstatus : 删除标识
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getDelstatus()
		{
			
			{
				return GetInt(getDelstatusColInfo());
			}
		}

		/// <summary>
		/// DelstatusColInfo : 对 Delstatus 字段的ColInfo对象的访问
		/// 字段Delstatus的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getDelstatusColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "delstatus");
			}
		}

		/// <summary>
		/// Constname : 预定义常量 预定义常量，例如对于性别字典，此处可能存储为“USR_SEX”
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getConstname()
		{
			
			{
				return GetString(getConstnameColInfo());
			}
		}

		/// <summary>
		/// ConstnameColInfo : 对 Constname 字段的ColInfo对象的访问
		/// 字段Constname的特性如下:[默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getConstnameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "constname");
			}
		}

		/// <summary>
		/// Chinaname : 选框的中文名称 中文名称，例如对于性别字典，此处可能存储“性别”
		/// 默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节
		/// </summary>
		public String getChinaname()
		{
			
			{
				return GetString(getChinanameColInfo());
			}
		}

		/// <summary>
		/// ChinanameColInfo : 对 Chinaname 字段的ColInfo对象的访问
		/// 字段Chinaname的特性如下:[默认值 : 无默认值
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为255字节]
		/// </summary>
		public ColInfo getChinanameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "chinaname");
			}
		}

		/// <summary>
		/// Optionname : 选项变量名 选项变量名，例如对于性别字典，此处可能存储“员工性别”
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getOptionname()
		{
			
			{
				return GetString(getOptionnameColInfo());
			}
		}

		/// <summary>
		/// OptionnameColInfo : 对 Optionname 字段的ColInfo对象的访问
		/// 字段Optionname的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getOptionnameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "optionname");
			}
		}

		/// <summary>
		/// Maxdepth : 最大层深 最大层次,即以此行记录为根结点,则其下允许几层子节点,取值仅能为1-5
		/// 默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getMaxdepth()
		{
			
			{
				return GetInt(getMaxdepthColInfo());
			}
		}

		/// <summary>
		/// MaxdepthColInfo : 对 Maxdepth 字段的ColInfo对象的访问
		/// 字段Maxdepth的特性如下:[默认值 : 1
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getMaxdepthColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "maxdepth");
			}
		}

		/// <summary>
		/// Remark : 备注 该字典的意义，此处提供一些备注信息，例如性别字典，此处可能描述为“当业务表中的一个字段存储人员性别时，该字段需要绑定到本字典”
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为1000字节
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
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为1000字节]
		/// </summary>
		public ColInfo getRemarkColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "remark");
			}
		}

		/// <summary>
		/// Diclevel : 字典级别 字典级别，开发级、系统级还是业务级，级别的意义见“字典级别”字典中相应项的描述
		/// 默认值 : 3
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getDiclevel()
		{
			
			{
				return GetInt(getDiclevelColInfo());
			}
		}

		/// <summary>
		/// DiclevelColInfo : 对 Diclevel 字段的ColInfo对象的访问
		/// 字段Diclevel的特性如下:[默认值 : 3
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getDiclevelColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "diclevel");
			}
		}

		/// <summary>
		/// Folderid : 字典目录 字典目录，字典本身所存放的目录，该目录本身也是一个字典
		/// 默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getFolderid()
		{
			
			{
				return GetInt(getFolderidColInfo());
			}
		}

		/// <summary>
		/// FolderidColInfo : 对 Folderid 字段的ColInfo对象的访问
		/// 字段Folderid的特性如下:[默认值 : 0
		/// 字段类型 : INT INT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getFolderidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "folderid");
			}
		}

		/// <summary>
		/// Extchar1name : 扩展字符属性1 扩展字符属性1，当一个字典本身并不仅是“键值+名称”对时，在此处需表明categoryvalue表中extchar1字段是有用的并提供给用户extchar1字段的输入框供维护（注意，当exttablename字段中存储了一个表名时，此extchar1name应为对应的exttablename表中的某个字段名），这种情况下，当用户维护本字典信息时，系统会同时更新exttablename表中的相应字段的信息
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtchar1name()
		{
			
			{
				return GetString(getExtchar1nameColInfo());
			}
		}

		/// <summary>
		/// Extchar1nameColInfo : 对 Extchar1name 字段的ColInfo对象的访问
		/// 字段Extchar1name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtchar1nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extchar1name");
			}
		}

		/// <summary>
		/// Extchar2name : 扩展字符属性2 扩展字符属性2，见扩展字符属性1
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtchar2name()
		{
			
			{
				return GetString(getExtchar2nameColInfo());
			}
		}

		/// <summary>
		/// Extchar2nameColInfo : 对 Extchar2name 字段的ColInfo对象的访问
		/// 字段Extchar2name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtchar2nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extchar2name");
			}
		}

		/// <summary>
		/// Extchar3name : 扩展字符属性3 扩展字符属性3，见扩展字符属性1
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtchar3name()
		{
			
			{
				return GetString(getExtchar3nameColInfo());
			}
		}

		/// <summary>
		/// Extchar3nameColInfo : 对 Extchar3name 字段的ColInfo对象的访问
		/// 字段Extchar3name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtchar3nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extchar3name");
			}
		}

		/// <summary>
		/// Extchar4name : 扩展字符属性4
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 可以为空   限制长度为50字节
		/// </summary>
		public String getExtchar4name()
		{
			
			{
				return GetString(getExtchar4nameColInfo());
			}
		}

		/// <summary>
		/// Extchar4nameColInfo : 对 Extchar4name 字段的ColInfo对象的访问
		/// 字段Extchar4name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 可以为空   限制长度为50字节]
		/// </summary>
		public ColInfo getExtchar4nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extchar4name");
			}
		}

		/// <summary>
		/// Extint1name : 扩展数字属性1 扩展数字属性1，见扩展字符属性1
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtint1name()
		{
			
			{
				return GetString(getExtint1nameColInfo());
			}
		}

		/// <summary>
		/// Extint1nameColInfo : 对 Extint1name 字段的ColInfo对象的访问
		/// 字段Extint1name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtint1nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint1name");
			}
		}

		/// <summary>
		/// Extint2name : 扩展数字属性2 扩展数字属性2，见扩展字符属性1
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtint2name()
		{
			
			{
				return GetString(getExtint2nameColInfo());
			}
		}

		/// <summary>
		/// Extint2nameColInfo : 对 Extint2name 字段的ColInfo对象的访问
		/// 字段Extint2name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtint2nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint2name");
			}
		}

		/// <summary>
		/// Extint3name : 扩展数字属性3 扩展数字属性3，见扩展字符属性1
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExtint3name()
		{
			
			{
				return GetString(getExtint3nameColInfo());
			}
		}

		/// <summary>
		/// Extint3nameColInfo : 对 Extint3name 字段的ColInfo对象的访问
		/// 字段Extint3name的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExtint3nameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint3name");
			}
		}

		/// <summary>
		/// Extint1categoryid : 扩展数字属性1本身所引用的下拉选框ID 扩展数字属性1本身所引用的下拉选框ID（当extint1name字段本身所存储的是一个映射到某字典的值时，此处选择其绑定到的字典）
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节
		/// </summary>
		public String getExtint1categoryid()
		{
			
			{
				return GetString(getExtint1categoryidColInfo());
			}
		}

		/// <summary>
		/// Extint1categoryidColInfo : 对 Extint1categoryid 字段的ColInfo对象的访问
		/// 字段Extint1categoryid的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节]
		/// </summary>
		public ColInfo getExtint1categoryidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint1categoryid");
			}
		}

		/// <summary>
		/// Extint2categoryid : 扩展数字属性2本身所引用的下拉选框ID 扩展数字属性2本身所引用的下拉选框ID，见extint1categoryid字段描述
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节
		/// </summary>
		public String getExtint2categoryid()
		{
			
			{
				return GetString(getExtint2categoryidColInfo());
			}
		}

		/// <summary>
		/// Extint2categoryidColInfo : 对 Extint2categoryid 字段的ColInfo对象的访问
		/// 字段Extint2categoryid的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节]
		/// </summary>
		public ColInfo getExtint2categoryidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint2categoryid");
			}
		}

		/// <summary>
		/// Extint3categoryid : 扩展数字属性3本身所引用的下拉选框ID 扩展数字属性3本身所引用的下拉选框ID，见extint1categoryid字段描述
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节
		/// </summary>
		public String getExtint3categoryid()
		{
			
			{
				return GetString(getExtint3categoryidColInfo());
			}
		}

		/// <summary>
		/// Extint3categoryidColInfo : 对 Extint3categoryid 字段的ColInfo对象的访问
		/// 字段Extint3categoryid的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为40字节]
		/// </summary>
		public ColInfo getExtint3categoryidColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "extint3categoryid");
			}
		}

		/// <summary>
		/// Refidorder : 非杂凑字典 是否使用refid的order（属于杂凑字典还是级别字典），如果此处值为1，则表明按照字典项键值的排序是有意义的（例如行政区划字典），否则是无意义的（例如系统配置项字典）
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getRefidorder()
		{
			
			{
				return GetInt(getRefidorderColInfo());
			}
		}

		/// <summary>
		/// RefidorderColInfo : 对 Refidorder 字段的ColInfo对象的访问
		/// 字段Refidorder的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getRefidorderColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "refidorder");
			}
		}

		/// <summary>
		/// Refidint : 使用数字型键名 使用数字型键名，如果此处存储值为1，则表明存储于categoryvalue表的refid字段的值为一个int型的值，注意，如果客户提供的字典中有形如“01”的代码值，则此处应是“否”，即取值为“0”
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getRefidint()
		{
			
			{
				return GetInt(getRefidintColInfo());
			}
		}

		/// <summary>
		/// RefidintColInfo : 对 Refidint 字段的ColInfo对象的访问
		/// 字段Refidint的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getRefidintColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "refidint");
			}
		}

		/// <summary>
		/// Leafuse : 使用叶节点标识 使用叶节点标识，如果此处存储值为1，表明在字典项列表中，有些选项仅为了表明其层级关系，而不应是被选中的
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getLeafuse()
		{
			
			{
				return GetInt(getLeafuseColInfo());
			}
		}

		/// <summary>
		/// LeafuseColInfo : 对 Leafuse 字段的ColInfo对象的访问
		/// 字段Leafuse的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getLeafuseColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "leafuse");
			}
		}

		/// <summary>
		/// Exttablename : 扩展存储表名 扩展存储表名（此表名不为空时，系统对extchar及extint将映射到相应的表中对应的字段上，此exttablename中应至少有id,chinaname,sortcode,indexid,delstatus字段，如果此字典的extchar1字段不为空，则该exttablename中应存在相应名称的字符型字段，对于extchar2……等字段也一样）
		/// 默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节
		/// </summary>
		public String getExttablename()
		{
			
			{
				return GetString(getExttablenameColInfo());
			}
		}

		/// <summary>
		/// ExttablenameColInfo : 对 Exttablename 字段的ColInfo对象的访问
		/// 字段Exttablename的特性如下:[默认值 : 
		/// 字段类型 : VARCHAR VARCHAR
		/// 字段特性 : 不可以为空  限制长度为50字节]
		/// </summary>
		public ColInfo getExttablenameColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "exttablename");
			}
		}

		/// <summary>
		/// Sortby : 默认排序依据 默认排序依据，可能为用户输入的排序号，也可能为键名，也可能为键中文名
		/// 默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 
		/// </summary>
		public int getSortby()
		{
			
			{
				return GetInt(getSortbyColInfo());
			}
		}

		/// <summary>
		/// SortbyColInfo : 对 Sortby 字段的ColInfo对象的访问
		/// 字段Sortby的特性如下:[默认值 : 0
		/// 字段类型 : INT TINYINT
		/// 字段特性 : 不可以为空 ]
		/// </summary>
		public ColInfo getSortbyColInfo()
		{
			
			{
				return ColInfos.Get(CnstTableName, "sortby");
			}
		}
		//#endregion 字段读取方法
		/// <summary>
				/// 取得本Category的所有位于Categoryvalue表的下拉选项的DataTable(无论是否删除)
				/// </summary>
				public DataTable getDtValues()
				{
					
					{
						return CategoryvalueViews.DataTables("categoryid='" + this.getId() + "'", "sortcode");
					}
				}

				/// <summary>
				/// 取得本Category的所有位于Categoryvalue表的下拉选项的DataTable(仅未删除的)
				/// </summary>
				public DataTable getDtValuesNoDeleted()
				{
					
					{
						return CategoryvalueViews.DataTables("categoryid='" + this.getId() + "' and delstatus=0", "sortcode");
					}
				}

				/// <summary>
				/// 取得指定节点的所有子节点的refid
				/// </summary>
				/// <param name="m_Refid">“指定节点”的refid</param>
				/// <param name="blnRecursion">是否递归所有子节点</param>
				/// <param name="blnContainDeleted">是否包含已删除的节点</param>
				/// <returns>形如“1,2,7”的refid字符串序列</returns>
				public String GetChildrenRefid(int m_Refid, boolean blnRecursion, boolean blnContainDeleted)
				{
					return GetChildrenRefid(this.Value(m_Refid).getSortcode(), blnRecursion, blnContainDeleted);
				}

				/// <summary>
				/// 取得指定节点的所有子节点的refid
				/// </summary>
				/// <param name="m_Categoryvalue">指定的节点的Categoryvalue对象实例</param>
				/// <param name="blnRecursion">是否递归所有子节点</param>
				/// <param name="blnContainDeleted">是否包含已删除的节点</param>
				/// <returns>形如“1,2,7”的refid字符串序列</returns>
				public String GetChildrenRefid(Categoryvalue m_Categoryvalue, boolean blnRecursion, boolean blnContainDeleted)
				{
					return GetChildrenRefid(m_Categoryvalue.getSortcode(), blnRecursion, blnContainDeleted);
				}

				/// <summary>
				/// 取得指定节点的所有子节点的refid
				/// </summary>
				/// <param name="m_Sortcode">该节点的Sortcode</param>
				/// <param name="blnRecursion">是否递归所有子节点</param>
				/// <param name="blnContainDeleted">是否包含已删除的节点</param>
				/// <returns>形如“1,2,7”的refid字符串序列</returns>
				private String GetChildrenRefid(String m_Sortcode, boolean blnRecursion, boolean blnContainDeleted)
				{
					String m_OutValue = null;
					int m_SortcodeLength = m_Sortcode.length();
					String m_where;
					String m_where_delete = "";
					if (blnContainDeleted == false)
					{
						m_where_delete = "delstatus = 0 and ";
					}
					int i = 1;
					if (blnRecursion)
					{
						i = 1;
						m_where = m_where_delete + "categoryid='" + this.getId() + "' and sortcode like '" + m_Sortcode + "%'";
					}
					else
					{
						i = 0;
						m_where = m_where_delete + "categoryid='" + this.getId() + "' and sortcode like '" + m_Sortcode + "%' and len(sortcode) = " + (m_Sortcode.length() + 5);
					}
					DataRow[] drs = CategoryvalueViews.getAll().Select(m_where, "sortcode");
					for (; i < drs.length; i++)
					{
						if (m_OutValue == null)
						{
							m_OutValue = drs[i].getString("refid");
						}
						else
						{
							m_OutValue += "," + drs[i].getString("refid");
						}
					}
					return m_OutValue == null ? "-1" : m_OutValue;
				}

				/// <summary>
				/// 根据下拉选项的值，取得一个Categoryvalue对象
				/// </summary>
				/// <param name="m_Refid">该下拉选项的值</param>
				/// <returns>Categoryvalue对象</returns>
				public Categoryvalue Value(Object m_Refid)
				{
					return CategoryvalueViews.Child(this.getId().toString(), m_Refid.toString());
				}

				/// <summary>
				/// 取得多选时(0至多个)选项的中文名称显示(采用“,”列出多个值)
				/// </summary>
				/// <param name="m_Refids">多个选项的字符串，形如“1,3,2”</param>
				/// <returns>一个字符串，形如“市办,A县办,B县办”</returns>
				public String ValuesName(Object m_Refids)
				{
					return ValuesName(m_Refids, ",");
				}

				/// <summary>
				/// 取得多选时(0至多个)选项的名称显示
				/// </summary>
				/// <param name="m_Refids">多个选项的字符串，形如“1,3,2”</param>
				/// <param name="m_SplitChar">采用的分隔多个选项的字符，例如“,”</param>
				/// <returns>一个字符串，形如“市办、A县办、B县办”</returns>
				public String ValuesName(Object m_Refids, String m_SplitChar)
				{
					String m_ReturnValue = "";
					DataTable dt = this.getDtValues();
					//String refids = "," + m_Refids.toString().Replace(" ", "") + ",";
					//for (int i = 0; i < dt.Rows.Count; i++)
					//{
					//    if (refids.IndexOf("," + dt.Rows[i]["refid"].toString() + ",") >= 0)
					//    {
					//        m_ReturnValue += (m_ReturnValue.Length > 0 ? m_SplitChar : "") + dt.Rows[i]["chinaname"].toString();
					//    }
					//}
					String[] arr = MyString.RuleSplit(MyString.TrimHtml(m_Refids.toString()), ",").split(",");
					for (int i = 0; i < arr.length; i++)
					{
						DataRow[] drs = dt.select("refid='" + arr[i] + "'");
						if (drs.length > 0)
						{
							m_ReturnValue += (m_ReturnValue.length() > 0 ? m_SplitChar : "") + drs[0].getString("chinaname");
						}
						else if (arr[i].replace("0" , "").length() > 0 && arr[i].indexOf("-") != 0)
						{
							m_ReturnValue += (m_ReturnValue.length() > 0 ? m_SplitChar : "") + arr[i];
						}
					}
					return m_ReturnValue;
				}

				/// <summary>
				/// 联带更新本下拉列表表示一主键的表的数据
				/// 未完成，需针对各扩展属性同步到相应表中去
				/// </summary>
				/// <returns></returns>
				public boolean UpdateRelation()
				{
					//只有扩展存储表不为空，并且使用数字型节点值，才需要更新相应表数据
					if (this.getExttablename().trim().equals("") || this.getRefidint() == 0)
					{
						return true;
					}
					this._ErrorInfo = null;
					CategoryvalueViews.Clear();
					DataTable dt = this.getDtValues();
					DataRow dr;
					String args;
					Categoryvalue obj;
					for (int i = 0; i < dt.getRowCount(); i++)
					{
						obj = CategoryvalueViews.Child(dt.get(i, "id"));
						dr = SqlHelper.ExecuteDatarow("select id,chinaname,parentid,indexid,sortcode,delstatus from " + this.getExttablename() + " where id=" + obj.getRefid().toString());

						if (dr == null)
						{
							args = "insert into " + this.getExttablename()
								+ " (id,chinaname,parentid,indexid,sortcode,delstatus"
								+ (this.getExtchar1name().length() > 0 ? ("," + this.getExtchar1name()) : "")
								+ (this.getExtchar2name().length() > 0 ? ("," + this.getExtchar2name()) : "")
								+ (this.getExtchar3name().length() > 0 ? ("," + this.getExtchar3name()) : "")
								+ (this.getExtchar4name().length() > 0 ? ("," + this.getExtchar4name()) : "")
								+ (this.getExtint1name().length() > 0 ? ("," + this.getExtint1name()) : "")
								+ (this.getExtint2name().length() > 0 ? ("," + this.getExtint2name()) : "")
								+ (this.getExtint3name().length() > 0 ? ("," + this.getExtint3name()) : "")
								+ ") values ("
								+ obj.getRefid().toString() + ",'"
								+ obj.getChinaname() + "',"
								+ (CategoryvalueViews.Child(obj.getParentid()).getRefid().length() > 0 ? CategoryvalueViews.Child(obj.getParentid()).getRefid() : "-1") + ","
								+ obj.getIndexid() + ",'"
								+ obj.getSortcode() + "',"
								+ obj.getDelstatus()
								+ (this.getExtchar1name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtchar1()) + "'") : "")
								+ (this.getExtchar2name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtchar2()) + "'") : "")
								+ (this.getExtchar3name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtchar3()) + "'") : "")
								+ (this.getExtchar4name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtchar4()) + "'") : "")
								+ (this.getExtint1name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtint1()+"") + "'") : "")
								+ (this.getExtint2name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtint2()+"") + "'") : "")
								+ (this.getExtint3name().length() > 0 ? (",'" + MyString.SqlEncode(obj.getExtint3()+"") + "'") : "")
								+ ")";
						}
						else
						{
							args = "update " + this.getExttablename() + " set "
								+ "chinaname = '" + obj.getChinaname() + "',"
								+ "parentid = " + (CategoryvalueViews.Child(obj.getParentid()).getRefid().length() > 0 ? ("'" + CategoryvalueViews.Child(obj.getParentid()).getRefid() + "'") : "-1") + ","
								+ "indexid = " + obj.getIndexid() + ","
								+ "sortcode = '" + obj.getSortcode() + "',"
								+ "delstatus = " + obj.getDelstatus()
								+ (this.getExtchar1name().length() > 0 ? ("," + this.getExtchar1name() + "= '" + MyString.SqlEncode(obj.getExtchar1()) + "'") : "")
								+ (this.getExtchar2name().length() > 0 ? ("," + this.getExtchar2name() + "= '" + MyString.SqlEncode(obj.getExtchar2()) + "'") : "")
								+ (this.getExtchar3name().length() > 0 ? ("," + this.getExtchar3name() + "= '" + MyString.SqlEncode(obj.getExtchar3()) + "'") : "")
								+ (this.getExtchar4name().length() > 0 ? ("," + this.getExtchar4name() + "= '" + MyString.SqlEncode(obj.getExtchar4()) + "'") : "")
								+ (this.getExtint1name().length() > 0 ? ("," + this.getExtint1name() + "= '" + MyString.SqlEncode(obj.getExtint1()+"") + "'") : "")
								+ (this.getExtint2name().length() > 0 ? ("," + this.getExtint2name() + "= '" + MyString.SqlEncode(obj.getExtint2()+"") + "'") : "")
								+ (this.getExtint3name().length() > 0 ? ("," + this.getExtint3name() + "= '" + MyString.SqlEncode(obj.getExtint3()+"") + "'") : "")
								+ " where id = " + obj.getRefid().toString();
						}
						SqlHelper.executeNonQuery(args);
					}
					CategoryvalueViews.Clear();
					return true;
				}

				/// <summary>
				/// AllSonRefid的后置变量
				/// </summary>
				private String allSonRefid = null;
				/// <summary>
				/// 所有直系儿子节点的refid，各refid间用“,”号进行分隔
				/// </summary>
				public String getAllSonRefid()
				{
					
					{
						if (allSonRefid != null)
						{
							return allSonRefid;
						}
						DataTable dt = CategoryvalueViews.DataTables("categoryid='" + this.getId() + "' and len(sortcode)=5", "sortcode");
						if (dt.getRowCount() == 0)
						{
							allSonRefid = "-1";
							return allSonRefid;
						}
						allSonRefid = dt.getString(0, "refid");
						for (int i = 1; i < dt.getRowCount(); i++)
						{
							allSonRefid += "," + dt.getString(i, "refid");
						}
						return allSonRefid;
					}
				}
	}
