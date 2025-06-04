package org.ark.framework.orm.schema;

import java.util.Arrays;
import java.util.List;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**   
 * @class org.ark.framework.orm.schema.SchemaGenerator
 * @author Darkness
 * @date 2012-10-22 下午10:40:20 
 * @version V1.0   
 */
@Slf4j
public abstract class SchemaGenerator {

//	private static Logger logger = log.getLogger(SchemaGenerator.class);
	public static final String PACKAGE = "org.ark.framework.orm.db";
	
	protected String outputDir;
	protected String namespace;
	protected boolean isOracle = false;

	protected String databaseName;

	public SchemaGenerator(String databaseName) {
		this.databaseName = databaseName;
		namespace = PACKAGE + ".t" + databaseName + ".schema";
		outputDir = getDefaultPath();
	}

	private String getDefaultPath() {
		String prefix = Config.getContextRealPath();
		prefix = prefix.substring(0, prefix.length() - 1);
		prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);
		String javapath = prefix + "Java/" + namespace.replaceAll("\\.", "/");
		FileUtil.mkdir(javapath);
		FileUtil.deleteEx(javapath + "/.+java");
		return javapath;
	}

	public SchemaGenerator(String namespace, String outputDir) {
		this.namespace = namespace;
		this.outputDir = outputDir;
	}

	public abstract SchemaGenerator.SchemaTable[] getSchemaTables();
	
	public void generate() {
		int i = 1;
		for (SchemaTable schemaTable : getSchemaTables()) {
			System.out.println("["+i+++"]生成表：" + schemaTable.tableCode);
			generateOneSechma(schemaTable.tableName, schemaTable.tableCode, schemaTable.tableComment,
					schemaTable.schemaColumns);
		}
	}
	
	protected void generateOneSechma(String tableName, String tableCode, String tableComment, SchemaColumn[] scs) {
		
		List<String> ingoreList = Arrays.asList("");
		
		
		if (ingoreList.contains(tableName)) {
			return;
		}
		
		if (!"BidProduct".equalsIgnoreCase(tableCode)) {
			return;
		}
		
		if (!checkCode(tableCode, "表代码")) {
			System.err.println("错误的表代码：" + tableName);
//			tableCode = "ARK" + tableCode;
//			return;
			throw new RuntimeException("错误的表代码：[" + tableName + "]");
		}
		
		tableCode = tableCode.substring(0, 1).toUpperCase() + tableCode.substring(1);

		StringBuffer sb = new StringBuffer();
		StringBuffer dsb = new StringBuffer();
		StringBuffer hsb = new StringBuffer();
		StringBuffer svsb = new StringBuffer();
		StringBuffer gvsb = new StringBuffer();
		StringBuffer csb = new StringBuffer();
		StringBuffer isb = new StringBuffer();
		StringBuffer insertsb = new StringBuffer();
		StringBuffer updatesb = new StringBuffer();
		StringBuffer pksb = new StringBuffer();
		StringBuffer keysb = new StringBuffer();

		sb.append("package " + namespace + ";\n\n");

		isb.append("import org.ark.framework.orm.Schema;\n");
		isb.append("import org.ark.framework.orm.SchemaColumn;\n");
		isb.append("import org.ark.framework.orm.SchemaSet;\n");
		isb.append("import com.arkxos.framework.commons.collection.DataTypes;\n");
		isb.append("import com.arkxos.framework.data.jdbc.Query;\n");

		hsb.append("\tpublic static final SchemaColumn[] _Columns = new SchemaColumn[] {\n");

		svsb.append("\tpublic void setV(int i, Object v) {\n");
		gvsb.append("\tpublic Object getV(int i) {\n");

		insertsb.append("\tprotected static final String _InsertAllSQL = \"insert into " + tableCode + " values(");
		updatesb.append("\tprotected static final String _UpdateAllSQL = \"update " + tableCode + " set ");
		pksb.append(" where ");

		boolean dateFlag = false;
		boolean firstPKFlag = true;
		for (int i = 0; i < scs.length; i++) {
			String code = scs[i].Code;
			if (i == 0) {
				insertsb.append("?");
				updatesb.append(code + "=?");
			} else {
				insertsb.append(",?");
				updatesb.append("," + code + "=?");
			}
			if (scs[i].isPrimaryKey) {
				if (firstPKFlag) {
					pksb.append(code + "=?");
					keysb.append(code);
					firstPKFlag = false;
				} else {
					pksb.append(" and " + code + "=?");
					keysb.append(", " + code);
				}
			}
			if (!checkCode(code, "表" + tableCode + "的字段")) {
				throw new RuntimeException("非法的表【" + tableCode + "】的字段【" + code+"】");
			}
			String dataType = scs[i].DataType;
			if ((dataType == null) || (dataType.equals(""))) {
				log.error("表" + tableCode + "的字段" + code + "的数据类型未定义!");
				throw new RuntimeException("表" + tableCode + "的字段" + code + "的数据类型未定义!");
			}
			String type = dataType.toLowerCase().trim();
			String ctype = null;
			String vtype = null;
			if ((type.startsWith("nvarchar")) || (type.startsWith("varchar"))
					|| (type.startsWith("char")) || (type.startsWith("nchar"))
					|| (type.startsWith("enum"))) {
				type = "String";
				ctype = "STRING";
				vtype = type;
			} else if ((type.startsWith("long varchar"))
					|| (type.startsWith("ntext")) || (type.startsWith("text"))
					|| (type.startsWith("mediumtext"))
					|| (type.startsWith("longtext"))
					|| (type.startsWith("clob"))) {
				type = "String";
				ctype = "CLOB";
				vtype = type;
			} else if ((type.startsWith("int")) || (type.startsWith("bit"))
					|| (type.startsWith("smallint"))
					|| (type.startsWith("tinyint"))
					|| (type.startsWith("mediumint"))) {
				type = "int";
				ctype = "INTEGER";
				vtype = "Integer";
				if ((isOracle) && (type.startsWith("int"))) {
					type = "long";
					ctype = "LONG";
					vtype = "Long";
				}
			} else if ((type.startsWith("long")) || (type.startsWith("bigint"))) {
				type = "long";
				ctype = "LONG";
				vtype = "Long";
				if ((isOracle) && (type.startsWith("long"))) {
					type = "String";
					ctype = "STRING";
					vtype = type;
				}
			} else if (type.startsWith("float")) {
				type = "float";
				ctype = "FLOAT";
				vtype = "Float";
			} else if ((type.startsWith("double"))
					|| (type.startsWith("decimal"))
					|| (type.startsWith("number"))
					|| (type.startsWith("money"))
					|| (type.startsWith("numeric"))) {
				type = "double";
				ctype = "DOUBLE";
				vtype = "Double";
			} else if (type.endsWith("blob") || (type.startsWith("blob")) || (type.startsWith("image"))) {
				type = "byte[]";
				ctype = "BLOB";
				vtype = type;
			} else if ((type.startsWith("date")) || (type.startsWith("time"))) {
				type = "Date";
				ctype = "DATETIME";
				vtype = type;
				dateFlag = true;
			} else {
				log.error(tableCode + "：不支持的数据类型" + type);
				throw new RuntimeException(tableCode + "：不支持的数据类型" + type);
//				return;
			}
			dsb.append("\tprivate " + vtype + " " + scs[i].Code + ";\n\n");
			if (code.length() < 1) {
				throw new RuntimeException("错误的代码：" + code);
			}
			String firstCode = code.substring(0, 1);
			String uCode = firstCode.toUpperCase()
					+ code.substring(1);

			csb.append("\t/**\n");
			csb.append("\t* 获取字段" + code + "的值，该字段的<br>\n");
			csb.append("\t* 字段名称 :" + scs[i].Name + "<br>\n");
			csb.append("\t* 数据类型 :" + scs[i].DataType + "<br>\n");
			csb.append("\t* 是否主键 :" + scs[i].isPrimaryKey + "<br>\n");
			csb.append("\t* 是否必填 :" + scs[i].Mandatory + "<br>\n");
			if (scs[i].Comment != null) {
				csb.append("\t* 备注信息 :<br>\n");
				splitComment(csb, scs[i].Comment, "\t");
			}
			csb.append("\t*/\n");
			csb.append("\tpublic " + type + " get" + uCode + "() {\n");
			if ((vtype.equals("Float")) || (vtype.equals("Integer"))
					|| (vtype.equals("Long")) || (vtype.equals("Double"))) {
				csb.append("\t\tif(" + code + "==null){return 0;}\n");
				csb.append("\t\treturn " + code + "." + type + "Value();\n");
			} else {
				csb.append("\t\treturn " + code + ";\n");
			}
			csb.append("\t}\n\n");

			csb.append("\t/**\n");
			csb.append("\t* 设置字段" + code + "的值，该字段的<br>\n");
			csb.append("\t* 字段名称 :" + scs[i].Name + "<br>\n");
			csb.append("\t* 数据类型 :" + scs[i].DataType + "<br>\n");
			csb.append("\t* 是否主键 :" + scs[i].isPrimaryKey + "<br>\n");
			csb.append("\t* 是否必填 :" + scs[i].Mandatory + "<br>\n");
			if (scs[i].Comment != null) {
				csb.append("\t* 备注信息 :<br>\n");
				splitComment(csb, scs[i].Comment, "\t");
			}
			csb.append("\t*/\n");

			String tCode = code.substring(0, 1).toLowerCase()
					+ code.substring(1);
			csb.append("\tpublic void set" + uCode + "(" + type + " " + tCode
					+ ") {\n");
			if ((vtype.equals("Float")) || (vtype.equals("Integer"))
					|| (vtype.equals("Long")) || (vtype.equals("Double")))
				csb.append("\t\tthis." + code + " = new " + vtype + "(" + tCode
						+ ");\n");
			else {
				csb.append("\t\tthis." + code + " = " + tCode + ";\n");
			}
			csb.append("    }\n\n");

			if ((vtype.equals("Float")) || (vtype.equals("Integer"))
					|| (vtype.equals("Long")) || (vtype.equals("Double"))) {
				csb.append("\t/**\n");
				csb.append("\t* 设置字段" + code + "的值，该字段的<br>\n");
				csb.append("\t* 字段名称 :" + scs[i].Name + "<br>\n");
				csb.append("\t* 数据类型 :" + scs[i].DataType + "<br>\n");
				csb.append("\t* 是否主键 :" + scs[i].isPrimaryKey + "<br>\n");
				csb.append("\t* 是否必填 :" + scs[i].Mandatory + "<br>\n");
				if (scs[i].Comment != null) {
					csb.append("\t* 备注信息 :<br>\n");
					splitComment(csb, scs[i].Comment, "\t");
				}
				csb.append("\t*/\n");
				csb.append("\tpublic void set" + uCode + "(String " + tCode
						+ ") {\n");
				csb.append("\t\tif (" + tCode + " == null){\n");
				csb.append("\t\t\tthis." + code + " = null;\n");
				csb.append("\t\t\treturn;\n");
				csb.append("\t\t}\n");
				csb.append("\t\tthis." + code + " = new " + vtype + "(" + tCode
						+ ");\n");
				csb.append("    }\n\n");
			}

			hsb.append("\t\tnew SchemaColumn(\"" + code + "\", DataTypes."
					+ ctype + ".code(), " + i + ", " + scs[i].Length + " , "
					+ scs[i].Precision + " , " + scs[i].Mandatory + " , "
					+ scs[i].isPrimaryKey + ",\""+ scs[i].Comment + "\")");
			if (i < scs.length - 1)
				hsb.append(",\n");
			else {
				hsb.append("\n");
			}

			if ((vtype.equals("Float")) || (vtype.equals("Integer"))
					|| (vtype.equals("Long")) || (vtype.equals("Double")))
				svsb.append("\t\tif (i == " + i + "){if(v==null){" + code
						+ " = null;}else{" + code + " = new " + vtype
						+ "(v.toString());}return;}\n");
			else {
				svsb.append("\t\tif (i == " + i + "){" + code + " = (" + vtype
						+ ")v;return;}\n");
			}
			gvsb.append("\t\tif (i == " + i + "){return " + code + ";}\n");
		}

		if (dateFlag) {
			isb.append("import java.util.Date;\n");
		}
		isb.append("\n");
		sb.append(isb);

		sb.append("/**\n");
		sb.append(" * 表名称：" + tableName);
		sb.append("<br>\n * 表代码：" + tableCode);
		if (tableComment != null) {
			sb.append("<br>\n * 表备注：<br>\n");
			splitComment(sb, tableComment, "");
		}
		sb.append("<br>\n * 表主键：" + keysb);
		sb.append("<br>\n */\n");
		sb.append("public class " + tableCode + "Schema extends Schema {\n");

		sb.append(dsb);

		hsb.append("\t};\n\n");

		hsb.append("\tpublic static final String _TableCode = \"" + tableCode
				+ "\";\n\n");
		hsb.append("\tpublic static final String _NameSpace = \"" + namespace + "\";\n\n");
		insertsb.append(")\";\n\n");
		updatesb.append("");
		updatesb.append(pksb);
		updatesb.append("\";\n\n");
		hsb.append(insertsb);
		hsb.append(updatesb);
		hsb.append("\tprotected static final String _DeleteSQL = \"delete from " + tableCode + " " + pksb.toString() + "\";\n\n");
		hsb.append("\tprotected static final String _FillAllSQL = \"select * from " + tableCode + " " + pksb.toString() + "\";\n\n");

		hsb.append("\tpublic " + tableCode + "Schema(){\n");
		hsb.append("\t\tTableCode = _TableCode;\n");
		hsb.append("\t\tTableComment = \""+tableComment+"\";\n");
		hsb.append("\t\tNameSpace = _NameSpace;\n");
		hsb.append("\t\tColumns = _Columns;\n");
		hsb.append("\t\tInsertAllSQL = _InsertAllSQL;\n");
		hsb.append("\t\tUpdateAllSQL = _UpdateAllSQL;\n");
		hsb.append("\t\tDeleteSQL = _DeleteSQL;\n");
		hsb.append("\t\tFillAllSQL = _FillAllSQL;\n");
		hsb.append("\t\tHasSetFlag = new boolean[" + scs.length + "];\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic Schema newInstance(){\n");
		hsb.append("\t\treturn new " + tableCode + "Schema();\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic "+tableCode + "Set newSet(){\n");
		hsb.append("\t\treturn new " + tableCode + "Set();\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic " + tableCode + "Set query() {\n");
		hsb.append("\t\treturn query(null, -1, -1);\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic " + tableCode + "Set query(Query qb) {\n");
		hsb.append("\t\treturn query(qb, -1, -1);\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic " + tableCode + "Set query(int pageSize, int pageIndex) {\n");
		hsb.append("\t\treturn query(null, pageSize, pageIndex);\n");
		hsb.append("\t}\n\n");

		hsb.append("\tpublic " + tableCode + "Set query(Query qb , int pageSize, int pageIndex){\n");
		hsb.append("\t\treturn (" + tableCode + "Set)querySet(qb , pageSize , pageIndex);\n");
		hsb.append("\t}\n\n");

		svsb.append("\t}\n\n");
		gvsb.append("\t\treturn null;\n");
		gvsb.append("\t}\n\n");
		sb.append(hsb);
		sb.append(svsb);
		sb.append(gvsb);
		sb.append(csb);
		sb.append("}");
		FileUtil.writeText(outputDir + "/" + tableCode + "Schema.java", sb.toString());
		generateSet(tableCode);
	}
	
	/**
	 * 生成SchemaSet
	 * 
	 * @param tableCode
	 */
	private void generateSet(String tableCode) {
		StringBuffer sb = new StringBuffer(1000);
		sb.append("package " + namespace + ";\n\n");
		sb.append("import " + namespace + "." + tableCode + "Schema;\n");
		sb.append("import org.ark.framework.orm.SchemaSet;\n\n");
		sb.append("public class " + tableCode + "Set extends SchemaSet<"+tableCode + "Schema"+"> {\n");
		sb.append("\tpublic " + tableCode + "Set() {\n");
		sb.append("\t\tthis(10,0);\n");
		sb.append("\t}\n\n");

		sb.append("\tpublic " + tableCode + "Set(int initialCapacity) {\n");
		sb.append("\t\tthis(initialCapacity,0);\n");
		sb.append("\t}\n\n");

		sb.append("\tpublic " + tableCode
				+ "Set(int initialCapacity,int capacityIncrement) {\n");
		sb.append("\t\tsuper(initialCapacity,capacityIncrement);\n");
	//	sb.append("\t\tTableCode = " + tableCode + "Schema._TableCode;\n");
		sb.append("\t\tColumns = " + tableCode + "Schema._Columns;\n");
	//	sb.append("\t\tNameSpace = " + tableCode + "Schema._NameSpace;\n");
	//	sb.append("\t\tInsertAllSQL = " + tableCode+ "Schema._InsertAllSQL;\n");
		//sb.append("\t\tUpdateAllSQL = " + tableCode+ "Schema._UpdateAllSQL;\n");
	//	sb.append("\t\tFillAllSQL = " + tableCode + "Schema._FillAllSQL;\n");
	//	sb.append("\t\tDeleteSQL = " + tableCode + "Schema._DeleteSQL;\n");
		sb.append("\t}\n\n");
		
		sb.append("\tpublic "+tableCode+"Schema[] createSchemaSet(int initialCapacity){"+
				"\n\t\treturn new "+tableCode+"Schema[initialCapacity];\n"
				+"\t}\n\n");

		sb.append("\tprotected "+tableCode + "Set newInstance(){\n");
		sb.append("\t\treturn new " + tableCode + "Set();\n");
		sb.append("\t}\n\n");

//		sb.append("\tpublic boolean add(" + tableCode + "Schema aSchema) {\n");
//		sb.append("\t\treturn super.add(aSchema);\n");
//		sb.append("\t}\n\n");
//		sb.append("\tpublic boolean add(" + tableCode + "Set aSet) {\n");
//		sb.append("\t\treturn super.add(aSet);\n");
//		sb.append("\t}\n\n");
//		sb.append("\tpublic boolean remove(" + tableCode
//				+ "Schema aSchema) {\n");
//		sb.append("\t\treturn super.remove(aSchema);\n");
//		sb.append("\t}\n\n");
//		sb.append("\tpublic " + tableCode + "Schema get(int index) {\n");
//		sb.append("\t\t" + tableCode + "Schema tSchema = (" + tableCode
//				+ "Schema) super.getObject(index);\n");
//		sb.append("\t\treturn tSchema;\n");
//		sb.append("\t}\n\n");
//		sb.append("\tpublic boolean set(int index, " + tableCode
//				+ "Schema aSchema) {\n");
//		sb.append("\t\treturn super.set(index, aSchema);\n");
//		sb.append("\t}\n\n");
//		sb.append("\tpublic boolean set(" + tableCode + "Set aSet) {\n");
//		sb.append("\t\treturn super.set(aSet);\n");
//		sb.append("\t}\n");
		sb.append("}\n ");
		FileUtil.writeText(outputDir + "/" + tableCode + "Set.java", sb
				.toString());
	}
	
	private void splitComment(StringBuffer sb, String comment, String tab) {
		String[] a = comment.split("\n");
		for (int i = 0; i < a.length; i++) {
			if (a[i].trim().equals("")) {
				continue;
			}
			sb.append(tab);
			sb.append(a[i].trim());
			sb.append("<br>\n");
		}
	}
	

	/**
	 * 检测是否是合法的JAVA标识符
	 * @param code
	 * @param msgPrefix
	 * @return
	 */
	private boolean checkCode(String code, String msgPrefix) {
		char[] ca = code.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			boolean isLegal = (i == 0) ? Character.isJavaIdentifierStart(ca[i]) : Character.isJavaIdentifierPart(ca[i]);

			if (!isLegal) {
				log.error(msgPrefix + code + "不是合适的Java标志名");
				return false;
			}
		}

		return true;
	}
	
	class SchemaTable {
		public String tableName;
		public String tableCode;
		public String tableComment;
		public SchemaColumn[] schemaColumns;
	}

	class SchemaColumn {
		public String ID;
		public String Name;
		public String Code;
		public String Comment;
		public String DataType;
		public int Length;
		public int Precision;
		public boolean Mandatory;
		public boolean isPrimaryKey;

		SchemaColumn() {
		}

		public void setMandatory(String mandatory) {
			if ((mandatory == null) || (mandatory.equals("")) || (mandatory.equals("0") || "NO".equalsIgnoreCase(mandatory)|| "N".equalsIgnoreCase(mandatory)))
				Mandatory = false;
			else {
				Mandatory = true;
			}
		}

		public void setPrecision(String precision) {
			try {
				if ((precision != null) && (!precision.equals("")))
					Precision = Integer.parseInt(precision);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setLength(String length) {
			try {
				if ((length != null) && (!length.equals("")))
					Length = Integer.parseInt(length);
			} catch (Exception e) {
				//e.printStackTrace();
				log.warn("列长度"+length+"超出了范围...");
			}
		}
	}
}

