package org.ark.framework.orm.xtable;

import java.util.Date;
import java.util.Hashtable;

import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.StringUtil;


/// TableStructList，提供对所有数据库的非系统表的基本属性访问。
///		修改对sysobjects及syscolumns的查询条件，使适应sql svr 2005
///		tablestructlist 类的 init 方法修改以防出现注释无法加载的情形
public class TableStructList {

	private static boolean _HandleError = false;

	private static Object mutex = new Object();

	// / 此类为静态类，不提供实例化方法。
	private TableStructList() {
	}

	// / 本类中的静态hashtable是否已初始化
	public static boolean Inited = false;

	// / 一个静态的Hashtable，键名为“表名”，键值为 一个TableStruct对象

	private static Hashtable<String, TableStruct> hashtable = new Hashtable<String, TableStruct>();

	// / 根据指定的表名，取得其TableStruct对象
	// / 若该表名未曾存储于本类中，则返回null

	// / <param name="m_TableName">指定的表名</param>
	// / <returns>TableStruct对象</returns>
	public static TableStruct Get(String m_TableName) {
		try {
			Init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!Exists(m_TableName.toLowerCase())) {
			return null;
		}
		return (TableStruct) (hashtable.get(m_TableName.toLowerCase()));
	}

	// / 将指定的表的TableStruct实例对象存入本类中
	// private static void Set(TableStruct m_Table) {
	// try {
	// Init();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// if (Exists(m_Table.getName())) {
	// hashtable.remove(m_Table.getName());
	// }
	// hashtable.put(m_Table.getName(), m_Table);
	// }

	// / 指定的表名是否已在本类中存储其信息

	// / <param name="m_TableName">表名</param>
	// / <returns>true or false</returns>
	public static boolean Exists(String m_TableName) {
		try {
			Init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashtable.contains(m_TableName.toLowerCase());
	}

	// / 初始化本类的HashTable以及ColInfos类

	static void Init() throws Exception {
		if (_HandleError) {
			throw new Exception("可能无法连接数据库，请与管理员联系！");
		}
		synchronized (mutex) {
			if (Inited) {
				return;
			}

			TestSpeed("tablestructlist init begin at " + new Date());
			hashtable.clear();
			ColInfos.Clear();

			// 更新Tablesremark表及Colsremark表的数据
			ColsremarkViews.Clear();
			TablesremarkViews.Clear();

//			XTable.init();
			
			String m_Name;
			String m_Primary;
			String m_ColNameList = "";

			String colname;
			int xtype;
			int colType;
			int length;
			int isNullAble;
			int haveDefault;
			int isPK;
			int isIncrement;
			String remark;
			String colDefault;

			int colpos = 0;

			DataTable dt;
			DataTable dtcols;
			try {
				dt = SqlHelper.ExecuteDatatable("SELECT (SELECT isnull(syscolumns.name, '') AS colname FROM syscolumns INNER JOIN "
						+ "sysindexkeys ON syscolumns.colid = sysindexkeys.colid AND sysindexkeys.id = syscolumns.id "
						+ "WHERE syscolumns.id = sysobjects.id AND sysindexkeys.indid = 1) AS m_PkCol, * " + "FROM sysobjects WHERE (xtype = 'U')  AND name <> 'dtproperties' ORDER BY sysobjects.id");

				dtcols = SqlHelper
						.ExecuteDatatable("SELECT id, name, xtype,length,isnullable,cdefault,colstat,colid,isnull((select text from syscomments where id=syscolumns.cdefault) , '') as defvalue FROM syscolumns WHERE id in (select id from sysobjects WHERE (xtype = 'U') AND sysobjects.name <> 'dtproperties' ) ORDER BY id,syscolumns.colorder,xtype");
			} catch (Exception e) {
				_HandleError = true;
				throw e;
			}
			ColInfos.Add(new ColInfo("", "", 0, 0, 0, 0, 0, 0, "", ""));

			if (hashtable.contains("") == false) {
				hashtable.put("", new TableStruct("", "", "".split(",")));
			}
			TestSpeed("tablestructlist begin loop");
			DataTable dtcolremark;
			for (int i = 0; i < dt.getRowCount(); i++) {
				
				m_Name = dt.getString(i, "name").toLowerCase();
				m_ColNameList = "";
				m_Primary = dt.getString(i, "m_PkCol");
				if (StringUtil.isEmpty(m_Primary)) {
					throw new Exception("表 " + m_Name + " 不存在主键，请认真设计该表！");
				}
				m_Primary = m_Primary.toLowerCase();
				dtcolremark = SqlHelper.ExecuteDatatable("SELECT objname,cast(value as varchar(8000)) as remark FROM ::fn_listextendedproperty('MS_Description', 'USER', 'dbo', 'TABLE', '" + m_Name
						+ "', 'COLUMN', NULL) [::fn_listextendedproperty_1] order by objname");

				for (; colpos < dtcols.getRowCount(); colpos++) {
					if (!dtcols.getString(colpos, "id").equals(dt.getString(i, "id"))) {
						break;
					}
					colname = dtcols.getString(colpos, "name").toLowerCase();
					xtype = dtcols.getInt(colpos, "xtype");
					length = dtcols.getInt(colpos, "length");
					isNullAble = dtcols.getInt(colpos, "isnullable");
					haveDefault = dtcols.getInt(colpos, "cdefault") > 0 ? 1 : 0;
					isPK = (colname.equals(m_Primary) ? 1 : 0);
					colType = ColInfo.GetSimpleType(xtype).getCode();

					remark = "";
					for (int n = 0; n < dtcolremark.getRowCount(); n++) {
						if (dtcolremark.getString(n, "objname").toLowerCase().equals(colname)) {
							remark = dtcolremark.getString(n, "remark").trim();
							break;
						}
					}

					if (colType < 3) {
						isIncrement = 0;
					} else {
						isIncrement = (dtcols.getString(colpos, "colstat").equals("1") ? 1 : 0);
					}

					if (haveDefault == 1) {
						colDefault = dtcols.getString(colpos, "defvalue").trim();
						while (colDefault.indexOf("(") == 0) {
							colDefault = colDefault.substring(1, colDefault.length() - 2);
						}
						while (colDefault.indexOf("'") == 0) {
							colDefault = colDefault.substring(1, colDefault.length() - 2);
						}

						if (colType == 2 && colDefault.toLowerCase().equals("getdate()")) {
							colDefault = "System.DateTime.Now.ToString()";
						}
					} else {
						colDefault = "";
					}

					m_ColNameList += (m_ColNameList.length() > 0 ? "," : "") + colname;
					ColInfos.Add(new ColInfo(m_Name, colname, xtype, length, isNullAble, haveDefault, isPK, isIncrement, remark, colDefault));
				}
				hashtable.put(m_Name, new TableStruct(m_Name, m_Primary, m_ColNameList.split(",")));
			}

			Inited = true;
		}

		TestSpeed("tablestructlist init ok at " + new Date());
	}

	// / 测试速度
	private static void TestSpeed(String sql) {
		// SqlHelper.ExecuteNonQuery("insert into TablesBasicDebug (tbl , sqls) values ('tablestructlist TestSpeed' , '"
		// + MyString.SqlEncode(sql) + "')");
	}
}
