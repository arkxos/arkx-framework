package org.ark.framework.orm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.data.jdbc.SessionFactory;


/**   
 * @class org.ark.framework.orm.Mysql2Oracle
 * @author Darkness
 * @date 2012-3-9 下午4:21:01 
 * @version V1.0   
 */
public class Mysql2Oracle {
	public static String generateOneTableSQL(String tableName) {
		StringBuffer sb = new StringBuffer();
		try {
			Class clz = Class.forName("org.ark.schema." + tableName + "Schema");
			Schema sc = (Schema) clz.newInstance();

			SchemaColumn[] cols = SchemaUtil.getColumns(sc);

			ArrayList pkList = new ArrayList();

			sb.append("drop table " + tableName + " cascade constraints;\n");
			sb.append("create table " + tableName + " (\n");
			for (int i = 0; i < cols.length; i++) {
				SchemaColumn col = cols[i];
				String dataType = "";
				if ((col.getColumnType() == 8) || (col.getColumnType() == 7))
					dataType = "integer";
				else if (col.getColumnType() == 1) {
					if ((col.getLength() < 8) && (col.getLength() > 0))
						dataType = "varchar(" + col.getLength() + ")";
					else if (col.getLength() >= 8)
						dataType = "varchar2(" + col.getLength() + ")";
					else {
						dataType = "long";
					}
				} else if (col.getColumnType() == 5)
					dataType = "float";
				else if (col.getColumnType() == 0)
					dataType = "date";
				else {
					dataType = "varchar2(" + col.getLength() + ")";
				}

				if (col.isPrimaryKey()) {
					pkList.add(col.getColumnName());
				}

				sb.append(col.getColumnName());
				sb.append(" " + dataType);
				sb.append(col.isMandatory() ? " not null" : " ");
				sb.append(",\n");
			}

			sb.append("constraint PK_" + tableName + " primary key (" + StringUtil.join(pkList.toArray(), ",") + ")\n");
			sb.append(");\n");

			System.out.println(sb.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void dealFile(String fileName) {
//		BackupTableGenerator btg = new BackupTableGenerator();
//		btg.setFileName(fileName);
//		try {
//			btg.toBackupTable();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			generateSQL(fileName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		fileName = fileName.substring(0, fileName.length() - 4) + "_B.pdm";
//		try {
//			generateSQL(fileName);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static void generateSQL(String fileName) throws Exception {
		File f = new File(fileName);
		if (!f.exists()) {
			throw new RuntimeException("文件不存在");
		}
		SAXReader reader = new SAXReader(false);
		Document doc = reader.read(f);
		Element root = doc.getRootElement();
		Namespace nso = root.getNamespaceForPrefix("o");
		Namespace nsc = root.getNamespaceForPrefix("c");
		Namespace nsa = root.getNamespaceForPrefix("a");
		Element rootObject = root.element(new QName("RootObject", nso));
		Element children = rootObject.element(new QName("Children", nsc));
		Element model = children.element(new QName("Model", nso));

		List tables = model.element(new QName("Tables", nsc)).elements();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tables.size(); i++) {
			Element table = (Element) tables.get(i);
			String tableName = table.elementText(new QName("Code", nsa));
			sb.append(generateOneTableSQL(tableName));
		}
		fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
		FileUtil.writeText("DB/Oracle/" + fileName.substring(0, fileName.length() - 4) + ".sql", sb.toString());
	}

	public static void importData() {
		DataTable dt = SessionFactory.openSession().readOnly().createQuery("show tables").executeDataTable();

		for (int i = 0; i < dt.getRowCount(); i++) {
			StringBuffer valueSQL = new StringBuffer();

			String tableName = dt.getString(i, 0);

			valueSQL.append("truncate table " + tableName + ";\n");

			if ((!tableName.startsWith("b")) && (!"zcarticle".equals(tableName))) {
				DataTable dtValue = SessionFactory.openSession().readOnly().createQuery("select * from " + dt.getString(i, 0)).executeDataTable();

				for (int j = 0; j < dtValue.getRowCount(); j++) {
					StringBuffer sb = new StringBuffer();
					sb.append("insert into ");
					sb.append(tableName);
					sb.append(" values(");
					for (int k = 0; k < dtValue.getColCount(); k++) {
						if (k != 0)
							sb.append(",'" + dtValue.getString(j, k) + "'");
						else {
							sb.append("'" + dtValue.getString(j, k) + "'");
						}
					}
					sb.append(")");

					valueSQL.append(sb + ";\n");
				}

			}

			System.out.println(valueSQL.toString());
		}
	}

	public static void main(String[] args) {
		String str = "arkcms";
		String[] files = str.split("\\,");

		for (int i = 0; i < files.length; i++) {
			String fileName = "DB/" + files[i] + ".pdm";
			dealFile(fileName);
		}
	}
}

