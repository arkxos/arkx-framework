package org.ark.framework.orm.xtable;

import java.util.Hashtable;

/// <summary>
/// ColInfos ，
/// 一个静态的Hashtable，键名为“表名 字段名”，键值为一个ColInfo类的实例化对象
/// 提供对某表的某字段的属性的读取
/// </summary>
/// <remarks>
/// CopyRight：arkx Skyinsoft. Co. Ltd.
/// 王周文 于 2006-2-28
///		初次完成
/// 陈涛 于 2006-4-17
///		添加注释
/// </remarks>
public class ColInfos {

    /// <summary>
    /// 清除hashtable
    /// </summary>
    static void Clear() {
        hashtable.clear();
    }

    /// <summary>
    /// 一个静态的Hashtable，键名为“表名 字段名”，键值为一个ColInfo类的实例化对象
    /// </summary>
    private static Hashtable hashtable = new Hashtable();

    /// <summary>
    /// 根据指定的表名、指定的字段名的一个ColInfo类的实例化对象
    /// 若该信息未曾存储于本类中，则返回null
    /// </summary>
    /// <param name="m_TableName">指定的表名</param>
    /// <param name="m_ColName">指定的列名</param>
    /// <returns>一个ColInfo类的实例化对象</returns>
    public static ColInfo Get(String m_TableName, String m_ColName) {
        if (!Exists(m_TableName, m_ColName)) {
            ///////////////////////////////////////////////
            // 待修改:当第一次加载时,hashtable[" "]没有初始化,所以返回一个未定义的colinfo,导致错误
            /////////////////////////////////////////////
            // if (!Exists("",""))
            // {
            // hashtable.Add(" ",new ColInfo("","",0,0,0,0,0,0,"",""));
            // }

            return (ColInfo) (hashtable.get(" "));
        }
        return (ColInfo) (hashtable.get(m_TableName.toLowerCase() + " " + m_ColName.toLowerCase()));
    }

    /// <summary>
    /// 将指定的表、指定的字段的ColInfo类的实例存入本类中
    /// </summary>
    /// <param name="m_ColInfo">ColInfo类的实例</param>
    public static void Add(ColInfo m_ColInfo) {
        if (hashtable.contains(m_ColInfo.getTableName().toLowerCase() + " " + m_ColInfo.getName().toLowerCase())) {
            hashtable.remove(m_ColInfo.getTableName().toLowerCase() + " " + m_ColInfo.getName().toLowerCase());
        }
        hashtable.put(m_ColInfo.getTableName().toLowerCase() + " " + m_ColInfo.getName().toLowerCase(), m_ColInfo);
    }

    /// <summary>
    /// 指定的表名、指定的列名是否已在本类中存储其ColInfo类的实例化对象
    /// </summary>
    /// <param name="m_TableName">表名</param>
    /// <param name="m_ColName">字段名</param>
    /// <returns>true or false</returns>
    public static boolean Exists(String m_TableName, String m_ColName) {
        if (TableStructList.Inited == false) {
            try {
                TableStructList.Init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((m_TableName.trim() + m_ColName.trim()).length() == 0) {
            return false;
        }
        return hashtable.contains(m_TableName.toLowerCase() + " " + m_ColName.toLowerCase());
    }

}
