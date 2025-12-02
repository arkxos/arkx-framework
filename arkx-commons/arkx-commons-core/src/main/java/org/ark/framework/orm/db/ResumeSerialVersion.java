package org.ark.framework.orm.db;

/**
 * @class org.ark.framework.orm.db.ResumeSerialVersion
 * @author Darkness
 * @date 2012-9-7 上午11:05:07
 * @version V1.0
 */
public class ResumeSerialVersion {

    /**
     * 还原为序列号之前的 serialVersionUID
     *
     * @param strException
     *            导入数据时发生错误:java.io.InvalidClassException:
     *            com.darkness.BCategorySchema; local class incompatible: stream
     *            classdesc serialVersionUID = -273214476710709249, local class
     *            serialVersionUID = 2747767855913689267";
     */
    // public static void resume(String strException) {
    // try {
    // String strSerialVersionUID = "private static final long serialVersionUID = ";
    //
    // // find serialVersionUID
    // Pattern ptnSerialVersionUID = Pattern.compile("-?\\d+");
    // Matcher match = ptnSerialVersionUID.matcher(strException);
    // int lastIndex = 0;
    // if (match.find(lastIndex)) {
    //
    // String tmp = strException.substring(match.start(), match.end());
    // lastIndex = match.end();
    //
    // strSerialVersionUID = strSerialVersionUID + tmp + "L;";
    // }
    //
    // String className = null;
    // // find className
    // Pattern ptnClassName = Pattern.compile("java.io.InvalidClassException:
    // com.zving.schema.(\\w+);");
    // match = ptnClassName.matcher(strException);
    //
    // if (match.find()) {
    // className = match.group(1);
    // }
    //
    // if(className == null) {
    // System.err.println("未找到匹配的类");
    // return ;
    // }
    //
    // String fileName = "C:\\Projects\\JAVA.NET_B_ORM\\Java\\com\\zving\\schema\\"
    // + className + ".java";
    // String text = FileUtil.readText(fileName);
    // String newText = "";
    // if(text.contains(strSerialVersionUID)) {
    // System.out.println("类"+className+"中已存在 serialVersionUID，将进行替换");
    // newText = text.replace(strSerialVersionUID + "-?\\d+L;",
    // strSerialVersionUID);
    // } else {
    // newText = StringUtil.replaceLast(text, "}", "\t" + strSerialVersionUID +
    // "\n}");
    // }
    // FileUtil.writeText(fileName, newText);
    //
    // System.out.println("已恢复类：" + className);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    public static void main(String[] args) {
        // resume("java.io.InvalidClassException: com.zving.schema.BZCAdminGroupSet;
        // local class incompatible: stream classdesc serialVersionUID =
        // 6957439840905473708, local class serialVersionUID =
        // 1857001488077763177false");
        // resume("误:java.io.InvalidClassException: com.darkness.BCategorySchema; local
        // class incompatible: stream classdesc serialVersionUID = -273214476710709249,
        // local class serialVersionUID = 2747767855913689267");
    }
}
