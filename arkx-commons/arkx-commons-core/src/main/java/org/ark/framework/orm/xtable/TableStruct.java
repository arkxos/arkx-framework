package org.ark.framework.orm.xtable;

/// <summary>
/// 数据库的一个表的简单结构属性索引。
/// </summary>
/// <remarks>
/// CopyRight：arkx Skyinsoft. Co. Ltd.
/// 王周文 于 2006-3-3
///		初次完成
/// 陈涛 于 2006-4-17
///		添加注释
/// </remarks>
public class TableStruct {
	private String _Name;
	private String _Primary;
	private String[] _Cols;

	// / <summary>
	// / 实例化本类，使成为数据库的一个表的简单属性索引。
	// / </summary>
	// / <param name="m_Name">表名</param>
	// / <param name="m_Primary">主键字段名</param>
	// / <param name="m_Cols">字段名列表数组</param>
	public TableStruct(String m_Name, String m_Primary, String[] m_Cols) {
		_Name = m_Name.toLowerCase();
		_Primary = m_Primary.toLowerCase();
		_Cols = m_Cols;
	}

	// / <summary>
	// / 表名
	// / </summary>
	public String getName() {

		return _Name;
	}

	// / <summary>
	// / 主键字段的ColInfo对象
	// / </summary>
	public ColInfo getPrimary() {

		return Col(_Primary);
	}

	// / <summary>
	// / 字段名列表数组
	// / </summary>
	public String[] getCols() {
		return _Cols;
	}

	// / <summary>
	// / 取得本表的一列的ColInfo对象
	// / </summary>
	// / <param name="m_ColName">列名</param>
	// / <returns>ColInfo对象</returns>
	public ColInfo Col(String m_ColName) {
		return ColInfos.Get(_Name, m_ColName);
	}

	// / <summary>
	// / 判断指定的字段名在本表是否存在
	// / </summary>
	// / <param name="m_ColName"></param>
	// / <returns></returns>
	public boolean ExistsCol(String m_ColName) {
		return Col(m_ColName).getName().length() > 0;
	}

	// / <summary>
	// / 取得本表的Tablesremark对象的信息
	// / </summary>
	public Tablesremark getRemarkInfo() {

		return TablesremarkViews.Child(this.getName());
	}
}
