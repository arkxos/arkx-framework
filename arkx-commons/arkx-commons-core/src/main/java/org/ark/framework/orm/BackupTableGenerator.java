package org.ark.framework.orm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import io.arkx.framework.commons.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.orm.BackupTableGenerator
 * @author Darkness
 * @date 2012-3-9 下午4:19:57
 * @version V1.0
 */
@Slf4j
public class BackupTableGenerator {
    // private static Logger logger = log.getLogger(BackupTableGenerator.class);

    private String fileName;
    private String aID = "ID";
    private Namespace nso;
    private Namespace nsc;
    private Namespace nsa;
    private int CurrentObjectID = 90000;

    private boolean isOracle = false;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void toBackupTable() throws Exception {
        File f = new File(this.fileName);
        if (!f.exists()) {
            throw new RuntimeException("文件不存在");
        }
        String txt = FileUtil.readText(f);
        this.isOracle = (txt.toLowerCase().indexOf("target=\"oracle") > 0);

        SAXReader reader = new SAXReader(false);
        Document doc = reader.read(f);
        Element root = doc.getRootElement();
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
        List tables = model.element(new QName("Tables", this.nsc)).elements();
        for (int i = 0; i < tables.size(); i++) {
            try {
                dealOneTable((Element) tables.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            String backupFileName = this.fileName.substring(0, this.fileName.lastIndexOf('.')) + "_B"
                    + this.fileName.substring(this.fileName.lastIndexOf('.'));
            XMLWriter output = new XMLWriter(new FileOutputStream(backupFileName), format);
            output.write(doc);
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private int getObjectID() {
        return this.CurrentObjectID++;
    }

    private void dealOneTable(Element table) {
        String tableCode = table.elementText(new QName("Code", this.nsa));

        checkCode(tableCode, "表代码");
        log.info(tableCode);
        table.element(new QName("Name", this.nsa)).setText(table.elementText(new QName("Name", this.nsa)) + "备份");
        table.element(new QName("Code", this.nsa)).setText("B" + table.elementText(new QName("Code", this.nsa)));

        String CreationDate = table.elementText(new QName("CreationDate", this.nsa));
        String Creator = table.elementText(new QName("Creator", this.nsa));
        String ModificationDate = table.elementText(new QName("ModificationDate", this.nsa));
        String Modifier = table.elementText(new QName("Modifier", this.nsa));

        Element eColumns = table.element(new QName("Columns", this.nsc));
        if (eColumns == null) {
            log.error("没有为表" + tableCode + "定义列!");
            return;
        }
        List columns = eColumns.elements();
        Element template = (Element) columns.get(0);
        String templateObjectID = template.elementText(new QName("ObjectID", this.nsa));
        templateObjectID = templateObjectID.substring(0, templateObjectID.length() - 5);

        Element ele = eColumns.addElement(new QName("Column", this.nso));
        String objectID = "o" + getObjectID();
        ele.addAttribute(this.aID, objectID);
        ele.addElement(new QName("Name", this.nsa)).setText("备份编号");
        ele.addElement(new QName("Code", this.nsa)).setText("BackupNo");
        ele.addElement(new QName("CreationDate", this.nsa)).setText(CreationDate);
        ele.addElement(new QName("Creator", this.nsa)).setText(Creator);
        ele.addElement(new QName("ModificationDate", this.nsa)).setText(ModificationDate);
        ele.addElement(new QName("Modifier", this.nsa)).setText(Modifier);
        if (this.isOracle)
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar2(15)");
        else {
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar(15)");
        }
        ele.addElement(new QName("Length", this.nsa)).setText("15");
        ele.addElement(new QName("Mandatory", this.nsa)).setText("1");
        ele.addElement(new QName("ObjectID", this.nsa)).setText(templateObjectID + getObjectID());

        ele = eColumns.addElement(new QName("Column", this.nso));
        ele.addAttribute(this.aID, "o" + getObjectID());
        ele.addElement(new QName("Name", this.nsa)).setText("备份人");
        ele.addElement(new QName("Code", this.nsa)).setText("BackupOperator");
        ele.addElement(new QName("CreationDate", this.nsa)).setText(CreationDate);
        ele.addElement(new QName("Creator", this.nsa)).setText(Creator);
        ele.addElement(new QName("ModificationDate", this.nsa)).setText(ModificationDate);
        ele.addElement(new QName("Modifier", this.nsa)).setText(Modifier);
        if (this.isOracle)
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar2(200)");
        else {
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar(200)");
        }
        ele.addElement(new QName("Length", this.nsa)).setText("200");
        ele.addElement(new QName("Mandatory", this.nsa)).setText("1");
        ele.addElement(new QName("ObjectID", this.nsa)).setText(templateObjectID + getObjectID());

        ele = eColumns.addElement(new QName("Column", this.nso));
        ele.addAttribute(this.aID, "o" + getObjectID());
        ele.addElement(new QName("Name", this.nsa)).setText("备份时间");
        ele.addElement(new QName("Code", this.nsa)).setText("BackupTime");
        ele.addElement(new QName("CreationDate", this.nsa)).setText(CreationDate);
        ele.addElement(new QName("Creator", this.nsa)).setText(Creator);
        ele.addElement(new QName("ModificationDate", this.nsa)).setText(ModificationDate);
        ele.addElement(new QName("Modifier", this.nsa)).setText(Modifier);
        if (this.isOracle)
            ele.addElement(new QName("DataType", this.nsa)).setText("date");
        else {
            ele.addElement(new QName("DataType", this.nsa)).setText("datetime");
        }
        ele.addElement(new QName("Mandatory", this.nsa)).setText("1");
        ele.addElement(new QName("ObjectID", this.nsa)).setText(templateObjectID + getObjectID());

        ele = eColumns.addElement(new QName("Column", this.nso));
        ele.addAttribute(this.aID, "o" + getObjectID());
        ele.attribute(this.aID).setValue("o" + getObjectID());
        ele.addElement(new QName("Name", this.nsa)).setText("备份备注");
        ele.addElement(new QName("Code", this.nsa)).setText("BackupMemo");
        ele.addElement(new QName("CreationDate", this.nsa)).setText(CreationDate);
        ele.addElement(new QName("Creator", this.nsa)).setText(Creator);
        ele.addElement(new QName("ModificationDate", this.nsa)).setText(ModificationDate);
        ele.addElement(new QName("Modifier", this.nsa)).setText(Modifier);
        if (this.isOracle)
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar2(50)");
        else {
            ele.addElement(new QName("DataType", this.nsa)).setText("varchar(50)");
        }
        ele.addElement(new QName("Length", this.nsa)).setText("50");
        ele.addElement(new QName("Mandatory", this.nsa)).setText("0");
        ele.addElement(new QName("ObjectID", this.nsa)).setText(templateObjectID + getObjectID());

        Element primaryKey = table.element(new QName("PrimaryKey", this.nsc));
        String keyRef = null;
        if (primaryKey != null) {
            primaryKey = primaryKey.element(new QName("Key", this.nso));
            if (primaryKey != null) {
                keyRef = primaryKey.attributeValue("Ref");
            }
        }
        if (keyRef != null) {
            List keys = table.element(new QName("Keys", this.nsc)).elements();
            boolean keyFlag = false;
            for (int i = 0; i < keys.size(); i++) {
                Element key = (Element) keys.get(i);
                if (keyRef.equals(key.attributeValue(this.aID))) {
                    Element eKeyColumns = key.element(new QName("Key.Columns", this.nsc));
                    if (eKeyColumns != null) {
                        List keyColumns = eKeyColumns.elements();
                        Element keyTemplate = (Element) keyColumns.get(0);
                        Element backupIDKey = keyTemplate.createCopy();
                        backupIDKey.attribute("Ref").setValue(objectID);
                        eKeyColumns.add(backupIDKey);
                        keyFlag = true;
                        break;
                    }
                }
            }
            if (!keyFlag)
                log.error("表" + tableCode + "未找到主键!");
        }
    }

    private boolean checkCode(String code, String msgPrefix) {
        char[] ca = code.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (i == 0) {
                if (!Character.isJavaIdentifierStart(ca[i])) {
                    log.info(msgPrefix + code + "不是合适的Java标志名");
                    return false;
                }
            } else if (!Character.isJavaIdentifierPart(ca[i])) {
                log.info(msgPrefix + code + "不是合适的Java标志名");
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        BackupTableGenerator btg = new BackupTableGenerator();
        btg.setFileName("H:/Projects/SportYou/Doc/DataBase/MiddingCMS.pdm");
        try {
            btg.toBackupTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
