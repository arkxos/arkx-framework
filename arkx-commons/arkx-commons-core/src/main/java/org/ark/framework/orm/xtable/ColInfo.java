package org.ark.framework.orm.xtable;

/// <summary>
/// 存储Sql Server数据库中的表的一列的信息(每一列作为一个实例)
/// </summary>
/// <remarks>
/// CopyRight：arkx Skyinsoft. Co. Ltd.
/// 王周文 于 2006-2-28
///		初次完成
/// 陈涛 于 2006-4-17
///		添加注释
/// </remarks>
public class ColInfo {

	// / <summary>
	// / 根据传入的xtype，得到SimpleType
	// / </summary>
	// / <param name="m_xtype">xtype</param>
	// / <returns>SimpleType</returns>
	public static SimpleTypes GetSimpleType(int m_xtype) {
		switch (m_xtype) {
			case 167:
			case 175:
				return SimpleTypes.VARCHAR;
			case 35:
				return SimpleTypes.TEXT;
			case 61:
				return SimpleTypes.DATETIME;
			case 48:
			case 56:
			case 104:
			case 127:
				return SimpleTypes.INT;
			case 59:
			case 60:
			case 62:
			case 106:
			case 108:
				return SimpleTypes.DOUBLE;
			default:
				return SimpleTypes.VARCHAR;
		}
	}

	// #endregion SimpleTypes，提供对数据库中的某列表现在类当中以何种类型变量来体现

	// #region 私有变量，作为ColInfo的公开属性的后置变量
	private String tableName;

	private String name;

	private int xtype;

	private int length;

	private boolean isNullAble;

	private boolean haveDefault;

	private boolean isPK;

	private boolean isIncrement;

	private String remark;

	private String _default;

	// #endregion 私有变量，作为ColInfo的公开属性的后置变量

	// / <summary>
	// / 初始化一个本类的实例
	// / 参数为Sql Server 的 syscolumns 中该行的信息.
	// / 下略.
	// / </summary>
	// / <param name="m_tablename"></param>
	// / <param name="m_name"></param>
	// / <param name="m_xtype"></param>
	// / <param name="m_length"></param>
	// / <param name="m_isnullable"></param>
	// / <param name="m_havedefault"></param>
	// / <param name="m_ispk"></param>
	// / <param name="m_isincrement"></param>
	// / <param name="m_remark"></param>
	// / <param name="m_default"></param>
	public ColInfo(String m_tablename, String m_name, int m_xtype, int m_length, int m_isnullable, int m_havedefault,
			int m_ispk, int m_isincrement, String m_remark, String m_default) {
		tableName = m_tablename.toLowerCase();
		name = m_name.toLowerCase();
		xtype = m_xtype;
		length = m_length;
		isNullAble = (m_isnullable == 1);
		haveDefault = (m_havedefault == 1);
		isPK = (m_ispk == 1);
		isIncrement = (m_isincrement == 1);
		remark = m_remark;
		_default = m_default;
	}

	// / <summary>
	// / 本列所从属的表的名称
	// / </summary>
	public String getTableName() {
		{
			return tableName;
		}
	}

	// / <summary>
	// / 本列名称
	// / </summary>
	public String getName() {
		return name;
	}

	// / <summary>
	// / 本列类型代码，存储于syscolumns表中的xtype列值
	// / </summary>
	public int getXtype() {
		{
			return xtype;
		}
	}

	// / <summary>
	// / 依Xtype取得该列表现在数据库中时，其以何种类型来存储
	// / </summary>
	public SqlTypes getSqlType() {

		{
			try {
				return SqlTypes.code(getXtype());
			}
			catch (Exception e) {
				return SqlTypes.VARCHAR;
			}
		}
	}

	private String _ShowType;

	// / <summary>
	// / 页面上显示的类型 add by dabao 2006-08-16 条件筛选器用到
	// /
	// 可能返回的类型为：DROPDOWNLIST，FILEFIELD，MULTITEXTEDIT，NUMERICTEXTEDIT，DATETEXTEDIT，TEXTEDIT
	// / </summary>
	public String getShowType() {

		{
			if (_ShowType != null) {
				return _ShowType;
			}
			if (this.getObjRemark().getCategoryid().trim().length() > 0) {
				_ShowType = EnumShowType_DROPDOWNLIST;
				return _ShowType;
			}
			if (this.name.indexOf("upload") == 0) {
				_ShowType = EnumShowType_FILEFIELD;
				return _ShowType;
			}
			if (getSqlType() == SqlTypes.TEXT) {
				_ShowType = EnumShowType_MULTITEXTEDIT;
				return _ShowType;
			}
			if (getSqlType() == SqlTypes.TINYINT || getSqlType() == SqlTypes.INT || getSqlType() == SqlTypes.REAL
					|| getSqlType() == SqlTypes.FLOAT || getSqlType() == SqlTypes.BIT
					|| getSqlType() == SqlTypes.DECIMAL || getSqlType() == SqlTypes.NUMERIC
					|| getSqlType() == SqlTypes.BIGINT) {
				_ShowType = EnumShowType_NUMERICTEXTEDIT;
				return _ShowType;
			}
			if (getSqlType() == SqlTypes.DATETIME) {
				_ShowType = EnumShowType_DATETEXTEDIT;
				return _ShowType;
			}
			if (this.getObjRemark().getMultilines() > 0)
				_ShowType = EnumShowType_MULTITEXTEDIT;
			else
				_ShowType = EnumShowType_TEXTEDIT;
			return _ShowType;
		}
	}

	// / <summary>
	// / EnumShowType_DROPDOWNLIST = "DROPDOWNLIST"
	// / </summary>
	public String EnumShowType_DROPDOWNLIST = "DROPDOWNLIST";

	// / <summary>
	// / EnumShowType_FILEFIELD = "FILEFIELD"
	// / </summary>
	public String EnumShowType_FILEFIELD = "FILEFIELD";

	// / <summary>
	// / EnumShowType_MULTITEXTEDIT = "MULTITEXTEDIT"
	// / </summary>
	public String EnumShowType_MULTITEXTEDIT = "MULTITEXTEDIT";

	// / <summary>
	// / EnumShowType_NUMERICTEXTEDIT = "NUMERICTEXTEDIT"
	// / </summary>
	public String EnumShowType_NUMERICTEXTEDIT = "NUMERICTEXTEDIT";

	// / <summary>
	// / EnumShowType_DATETEXTEDIT = "DATETEXTEDIT"
	// / </summary>
	public String EnumShowType_DATETEXTEDIT = "DATETEXTEDIT";

	// / <summary>
	// / EnumShowType_TEXTEDIT = "TEXTEDIT"
	// / </summary>
	public String EnumShowType_TEXTEDIT = "TEXTEDIT";

	// / <summary>
	// / 依Xtype取得该列表现在类中时，一般以何种类型来体现
	// / </summary>
	public SimpleTypes getSimpleType() {

		{
			return GetSimpleType(getXtype());
		}
	}

	// / <summary>
	// / 本列数据长度,一般情况下仅当本列的Xtype值为167或175时您需要关注此值
	// / </summary>
	public int getLength() {
		{
			return length;
		}
	}

	// / <summary>
	// / 允许为空？true：false
	// / </summary>
	public boolean getIsNullAble() {
		{
			return isNullAble;
		}
	}

	// / <summary>
	// / 有默认值？true：false
	// / </summary>
	public boolean getHaveDefault() {
		{
			return haveDefault;
		}
	}

	// / <summary>
	// / 是本表的主键？true：false
	// / </summary>
	public boolean getIsPK() {
		{
			return isPK;
		}
	}

	// / <summary>
	// / 是自增长字段？true：false
	// / </summary>
	public boolean getIsIncrement() {
		{
			return isIncrement;
		}
	}

	// / <summary>
	// / 本列的备注信息
	// / </summary>
	public String getRemark() {
		{
			return remark;
		}
	}

	// / <summary>
	// / 本列的默认值信息
	// / </summary>
	public String getDefault() {
		{
			return _default;
		}
	}

	// / <summary>
	// / 重载的ToString()方法
	// / </summary>
	// / <returns>返回本Columns的Name</returns>
	public String toString() {
		return name;
	}

	// / <summary>
	// / 本字段的Colsremark对象
	// / </summary>
	public Colsremark getObjRemark() {

		{
			return ColsremarkViews.Child(tableName, name);
		}
	}

}
