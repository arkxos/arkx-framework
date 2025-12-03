package org.ark.framework.orm.xtable;

/**
 * @author Darkness
 * @date 2012-4-7 上午10:46:56
 * @version V1.0
 */
public class MyString {

    /// <summary>
    /// 规范字符串中的分隔字符
    /// 注意：规范后的字符串中将不包含空格
    /// 例如：分隔符为“,”则
    /// “,张三 , , 李四,王五,,”==〉“张三,李四,王五”
    /// </summary>
    /// <param name="StrForSplit">待规范的字符串</param>
    /// <param name="SplitChr">分隔符，不能是空格，长度应为1</param>
    /// <returns>规范后的字符串</returns>
    public static String RuleSplit(String StrForSplit, String SplitChr) {
        if (StrForSplit == null || StrForSplit.length() == 0 || SplitChr == null || SplitChr.equals(" ")
                || (SplitChr.length() != 1))
            return StrForSplit;
        String strSrc = StrForSplit.replace(" ", "").replace(SplitChr, " ").trim();

        while (strSrc.indexOf("  ") >= 0)
            strSrc = strSrc.replace("  ", " ");
        return strSrc.replace(" ", SplitChr);
    }

    /// <summary>
    /// 过滤and符号，左尖括号，单引号，双引号，及右下斜线
    /// </summary>
    /// <param name="str">待过滤字符串</param>
    /// <returns>过滤之后的字符串</returns>
    public static String TrimHtml(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("&", "").replace("<", "").replace("'", "").replace("\"", "").replace("\\", "");
    }

    // / <summary>
    // / 将sql的where子句中的"where "去除，仅包含条件部分
    // / 此操作的作用为，使where适合DataTable的FilterExpression参数规则
    // / </summary>
    // / <param name="m_wheres"></param>
    // / <returns></returns>
    public static String RuleSqlWhereClause(String m_wheres) {
        String m_where = (m_wheres == null || m_wheres.length() <= 3) ? "1=1" : m_wheres.toLowerCase().trim();
        if (m_where.indexOf("where ") == 0) {
            m_where = m_where.substring(5).trim();
        }
        return m_where;
    }

    // / 是否可转换为整型
    public static boolean IsInt(Object s) {
        try {
            Integer.parseInt(s.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // / 是否可转换为double
    // / 如果是IsInt，则肯定也IsDouble
    public static boolean IsDouble(Object s) {
        try {
            Double.parseDouble(s.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // / <summary>
    // / 判断所传入的两个字符串除了空格、换行符之外有无不同，有不同则返回true
    // / </summary>
    // / <param name="s1">被比较字串一</param>
    // / <param name="s2">被比较字串二</param>
    // / <returns>true or false</returns>
    public static boolean booleanChanged(String s1, String s2) {
        return false == s1.trim().replace("\r\n", "").replace(" ", "").replace("　", "")
                .equals(s2.trim().replace("\r\n", "").replace(" ", "").replace("　", ""));
    }

    /// <summary>
    /// 编码单引号、双引号、AND符号及左尖括号用于HTML显示
    /// </summary>
    /// <param name="str">待编码字符串</param>
    /// <returns>编码之后的字符串</returns>
    public static String HtmlEncode(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("&", "&#38;").replace("<", "&#60;").replace("'", "&#39;").replace("\"", "&#34;");
    }

    /// <summary>
    /// HtmlEncode(str) 后 将回车符进行编码，再将双空格替换为HTML空格
    /// </summary>
    /// <param name="str">待编码字符串</param>
    /// <returns>编码之后的字符串</returns>
    public static String HtmlEncodeBR(String str) {
        if (str == null) {
            return "";
        }
        return HtmlEncode(str).replace("\r\n", "<BR>").replace("  ", "&nbsp; ");
    }/// <summary>
     /// 保证字符串中含有单引号时也能正确进行TranSql语句的执行
     /// </summary>
     /// <param name="str">待编码字符串</param>
     /// <returns>编码之后的字符串</returns>

    public static String SqlEncode(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "''");
    }

}
