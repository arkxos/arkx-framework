package org.ark.framework.orm.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.util.FileUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * @class org.ark.framework.orm.schema.PdmSchemaGenerator
 * @author Darkness
 * @date 2011-10-17 下午02:58:34
 * @version V1.0
 */
@Slf4j
public class PdmSchemaGenerator extends SchemaGenerator {
	
//	private static Logger logger = log.getLogger(PdmSchemaGenerator.class);
	
	private String fileName;
	private String aID = "ID";
	private Namespace nso;
	private Namespace nsc;
	private Namespace nsa;

	public PdmSchemaGenerator(String fileName, String namespace, String outputDir) {
		super(namespace, outputDir);
		this.fileName = fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setOutputDir(String dir) {
		this.outputDir = dir;
	}

	private SchemaColumn[] getSchemaColumns(Element table) {

		String tableCode = table.elementText(new QName("Code", this.nsa));

		// 获取所有列
		Element eColumns = table.element(new QName("Columns", this.nsc));
		if (eColumns == null) {
			log.error("没有为表" + tableCode + "定义列!");
			return null;
		}
		List<?> columns = eColumns.elements();
		SchemaColumn[] scs = new SchemaColumn[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			SchemaColumn sc = new SchemaColumn();
			Element column = (Element) columns.get(i);
			sc.ID = column.attributeValue(this.aID);
			sc.Name = column.elementText(new QName("Name", this.nsa));
			sc.Code = column.elementText(new QName("Code", this.nsa));
			sc.Comment = column.elementText(new QName("Comment", this.nsa));
			sc.DataType = column.elementText(new QName("DataType", this.nsa));
			sc.setLength(column.elementText(new QName("Length", this.nsa)));
			sc.setPrecision(column.elementText(new QName("Precision", this.nsa)));
			sc.setMandatory(column.elementText(new QName("Mandatory", this.nsa)));

			scs[i] = sc;
		}

		Element primaryKey = table.element(new QName("PrimaryKey", this.nsc));
		String keyRef = null;
		if (primaryKey != null) {
			primaryKey = primaryKey.element(new QName("Key", this.nso));
			if (primaryKey != null) {
				keyRef = primaryKey.attributeValue("Ref");
			}
		}
		if (keyRef != null) {
			List<?> keys = table.element(new QName("Keys", this.nsc)).elements();
			boolean keyFlag = false;
			for (int i = 0; i < keys.size(); i++) {
				Element key = (Element) keys.get(i);
				if (keyRef.equals(key.attributeValue(this.aID))) {
					Element eKeyColumn = key.element(new QName("Key.Columns", this.nsc));
					if (eKeyColumn != null) {
						List<?> keyColumns = eKeyColumn.elements();
						for (int j = 0; j < keyColumns.size(); j++) {
							String columnID = ((Element) keyColumns.get(j)).attributeValue("Ref");
							for (int k = 0; k < scs.length; k++) {
								if (scs[k].ID.equals(columnID)) {
									scs[k].isPrimaryKey = true;
								}
							}
						}
						keyFlag = true;
						break;
					}
				}
			}
			if (!keyFlag) {
				log.error("表" + tableCode + "未找到主键!");
			}
		}

		return scs;
	}

	@Override
	public SchemaGenerator.SchemaTable[] getSchemaTables() {
		File f = new File(this.fileName);
		if (!f.exists()) {
			throw new RuntimeException(f.getAbsolutePath() + "文件不存在");
		}
		SAXReader reader = new SAXReader(false);
		Document doc = null;
		try {
			doc = reader.read(f);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Element root = doc.getRootElement();

		String txt = FileUtil.readText(f);

		this.isOracle = (txt.toLowerCase().indexOf("target=\"oracle") > 0);
		this.nso = root.getNamespaceForPrefix("o");
		this.nsc = root.getNamespaceForPrefix("c");
		this.nsa = root.getNamespaceForPrefix("a");
		Element rootObject = root.element(new QName("RootObject", this.nso));
		Element children = rootObject.element(new QName("Children", this.nsc));
		Element model = children.element(new QName("Model", this.nso));
		if (model.attributeValue("ID") == null) {
			if (model.attributeValue("Id") != null)
				this.aID = "Id";
			else {
				throw new RuntimeException("ID属性名称未定，PDM版本不正确");
			}
		}

		List<SchemaGenerator.SchemaTable> schemaTables = new ArrayList<SchemaGenerator.SchemaTable>();

		List<?> tables = model.element(new QName("Tables", this.nsc)).elements();
		for (int i = 0; i < tables.size(); i++) {
			try {
				Element table = (Element) tables.get(i);
				String tableComment = table.elementText(new QName("Comment", this.nsa));
				String tableName = table.elementText(new QName("Name", this.nsa));
				String tableCode = table.elementText(new QName("Code", this.nsa));

				SchemaTable schemaTable = new SchemaTable();
				schemaTable.tableName = tableName;
				schemaTable.tableCode = tableCode;
				schemaTable.tableComment = tableComment;
				schemaTable.schemaColumns = getSchemaColumns(table);

				schemaTables.add(schemaTable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return schemaTables.toArray(new SchemaGenerator.SchemaTable[0]);
	}

	public static void main(String[] args) throws Exception {

		String packageStr = "com.rapidark.schema";
		String str = Config.getValue("App.PDM");
		String[] files = str.split("\\,");

		String prefix = Config.getContextRealPath();
		prefix = prefix.substring(0, prefix.length() - 1);
		prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);
		String javapath = prefix + "Java/" + packageStr.replaceAll("\\.", "/");
		FileUtil.mkdir(javapath);
		FileUtil.deleteEx(javapath + "/.+java");

		for (int i = 0; i < files.length; i++) {
			String fileName = "DB/" + files[i] + ".pdm";// pdm file path
			new PdmSchemaGenerator(fileName, packageStr, javapath).generate();
		}

		// todo pdm backup file
		// BackupTableGenerator btg = new BackupTableGenerator();
		// btg.setFileName(fileName);
		// try {
		// btg.toBackupTable();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// todo generate backup pdm file schema
		// og.setFileName(fileName.substring(0, fileName.length() - 4) +
		// "_B.pdm");
		// og.setOutputDir(outputDir);
		// og.setNamespace(namespace);
		// try {
		// og.generate();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
