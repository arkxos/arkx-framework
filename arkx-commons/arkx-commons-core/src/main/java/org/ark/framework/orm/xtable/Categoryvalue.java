package org.ark.framework.orm.xtable;

import io.arkx.framework.commons.collection.DataRow;

public class Categoryvalue extends TablesBasic {

    // #region 构造函数
    // / <summary>
    // / 此类对应的表名
    // / </summary>
    private String CnstTableName = "categoryvalue";

    // / <summary>
    // / 实例化本类，但不是数据库中的真实数据
    // / </summary>
    public Categoryvalue() {
        Init(CnstTableName, null);
    }

    // / <summary>
    // / 实例化本类，以传入的数据库中的真实数据的主键值
    // / </summary>
    // / <param name="m_id">该行数据的主键id值</param>
    public Categoryvalue(Object m_id) {
        String sql = "select * from " + CnstTableName + " where " + TableStructList.Get(CnstTableName).getPrimary()
                + "='" + m_id.toString() + "'";
        DataRow m_dr = SqlHelper.ExecuteDatarow(sql);
        Init(CnstTableName, m_dr);
    }

    // / <summary>
    // / 实例化本类，以传入的数据库中的真实数据的System.Data.DataRow对象
    // / </summary>
    // / <param name="m_dr">该行数据数据的System.Data.DataRow对象</param>
    public Categoryvalue(DataRow m_dr) {
        Init(CnstTableName, m_dr);
    }

    // #endregion 构造函数

    // #region 字段读取方法
    // / <summary>
    // / Id : 主键 主键ID，作为唯一标识符
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 该字段是本表的主键 限制长度为40字节
    // / </summary>
    public String getId() {

        {
            return GetString(getIdColInfo());
        }
    }

    // / <summary>
    // / IdColInfo : 对 Id 字段的ColInfo对象的访问
    // / 字段Id的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 该字段是本表的主键 限制长度为40字节]
    // / </summary>
    public ColInfo getIdColInfo() {
        {
            return ColInfos.Get(CnstTableName, "id");
        }
    }

    // / <summary>
    // / Adder : 添加者
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getAdder() {
        {
            return GetInt(getAdderColInfo());
        }
    }

    // / <summary>
    // / AdderColInfo : 对 Adder 字段的ColInfo对象的访问
    // / 字段Adder的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getAdderColInfo() {
        {
            return ColInfos.Get(CnstTableName, "adder");
        }
    }

    // / <summary>
    // / Addtime : 添加时间
    // / 默认值 : System.DateTime.Now.ToString()
    // / 字段类型 : DATETIME DATETIME
    // / 字段特性 : 不可以为空
    // / </summary>
    public String getAddtime() {
        {
            return GetString(getAddtimeColInfo());
        }
    }

    // / <summary>
    // / AddtimeColInfo : 对 Addtime 字段的ColInfo对象的访问
    // / 字段Addtime的特性如下:[默认值 : System.DateTime.Now.ToString()
    // / 字段类型 : DATETIME DATETIME
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getAddtimeColInfo() {
        {
            return ColInfos.Get(CnstTableName, "addtime");
        }
    }

    // / <summary>
    // / Moder : 最后修改者
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getModer() {
        {
            return GetInt(getModerColInfo());
        }
    }

    // / <summary>
    // / ModerColInfo : 对 Moder 字段的ColInfo对象的访问
    // / 字段Moder的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getModerColInfo() {
        {
            return ColInfos.Get(CnstTableName, "moder");
        }
    }

    // / <summary>
    // / Modtime : 最后修改时间
    // / 默认值 : System.DateTime.Now.ToString()
    // / 字段类型 : DATETIME DATETIME
    // / 字段特性 : 不可以为空
    // / </summary>
    public String getModtime() {
        {
            return GetString(getModtimeColInfo());
        }
    }

    // / <summary>
    // / ModtimeColInfo : 对 Modtime 字段的ColInfo对象的访问
    // / 字段Modtime的特性如下:[默认值 : System.DateTime.Now.ToString()
    // / 字段类型 : DATETIME DATETIME
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getModtimeColInfo() {
        {
            return ColInfos.Get(CnstTableName, "modtime");
        }
    }

    // / <summary>
    // / Delstatus : 删除标识 删除（禁用）标识，为0表示正常，为1表示禁用
    // / 默认值 : 0
    // / 字段类型 : INT TINYINT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getDelstatus() {
        {
            return GetInt(getDelstatusColInfo());
        }
    }

    // / <summary>
    // / DelstatusColInfo : 对 Delstatus 字段的ColInfo对象的访问
    // / 字段Delstatus的特性如下:[默认值 : 0
    // / 字段类型 : INT TINYINT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getDelstatusColInfo() {
        {
            return ColInfos.Get(CnstTableName, "delstatus");
        }
    }

    // / <summary>
    // / Refid : 序号 实际各字段引用的本行值的标识ID，对于同一categoryid的各信息来说，此refid应是唯一的
    // / 默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为30字节
    // / </summary>
    public String getRefid() {
        {
            return GetString(getRefidColInfo());
        }
    }

    // / <summary>
    // / RefidColInfo : 对 Refid 字段的ColInfo对象的访问
    // / 字段Refid的特性如下:[默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为30字节]
    // / </summary>
    public ColInfo getRefidColInfo() {
        {
            return ColInfos.Get(CnstTableName, "refid");
        }
    }

    // / <summary>
    // / Categoryid : 外键 category表的ID，表明此行数据为哪个字典的选项
    // / 默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为40字节
    // / </summary>
    public String getCategoryid() {
        {
            return GetString(getCategoryidColInfo());
        }
    }

    // / <summary>
    // / CategoryidColInfo : 对 Categoryid 字段的ColInfo对象的访问
    // / 字段Categoryid的特性如下:[默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为40字节]
    // / </summary>
    public ColInfo getCategoryidColInfo() {
        {
            return ColInfos.Get(CnstTableName, "categoryid");
        }
    }

    // / <summary>
    // / Chinaname : 简称 中文名称，对于同一字典来说，此字段不允许重复
    // / 默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节
    // / </summary>
    public String getChinaname() {
        {
            return GetString(getChinanameColInfo());
        }
    }

    // / <summary>
    // / ChinanameColInfo : 对 Chinaname 字段的ColInfo对象的访问
    // / 字段Chinaname的特性如下:[默认值 : 无默认值
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节]
    // / </summary>
    public ColInfo getChinanameColInfo() {
        {
            return ColInfos.Get(CnstTableName, "chinaname");
        }
    }

    // / <summary>
    // / Extint1 : 扩展数字属性1 扩展数字属性1，见category表的extint1name字段描述
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getExtint1() {
        {
            return GetInt(getExtint1ColInfo());
        }
    }

    // / <summary>
    // / Extint1ColInfo : 对 Extint1 字段的ColInfo对象的访问
    // / 字段Extint1的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getExtint1ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extint1");
        }
    }

    // / <summary>
    // / Extint2 : 扩展数字属性2 扩展数字属性2，解释同extint1
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getExtint2() {
        {
            return GetInt(getExtint2ColInfo());
        }
    }

    // / <summary>
    // / Extint2ColInfo : 对 Extint2 字段的ColInfo对象的访问
    // / 字段Extint2的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getExtint2ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extint2");
        }
    }

    // / <summary>
    // / Extint3 : 扩展数字属性3 扩展数字属性3，解释同extint1
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getExtint3() {
        {
            return GetInt(getExtint3ColInfo());
        }
    }

    // / <summary>
    // / Extint3ColInfo : 对 Extint3 字段的ColInfo对象的访问
    // / 字段Extint3的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getExtint3ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extint3");
        }
    }

    // / <summary>
    // / Extchar1 : 扩展字符属性1 扩展字符属性1，解释同extint1
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节
    // / </summary>
    public String getExtchar1() {
        {
            return GetString(getExtchar1ColInfo());
        }
    }

    // / <summary>
    // / Extchar1ColInfo : 对 Extchar1 字段的ColInfo对象的访问
    // / 字段Extchar1的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节]
    // / </summary>
    public ColInfo getExtchar1ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extchar1");
        }
    }

    // / <summary>
    // / Extchar2 : 扩展字符属性2 扩展字符属性2，解释同extint1
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节
    // / </summary>
    public String getExtchar2() {
        {
            return GetString(getExtchar2ColInfo());
        }
    }

    // / <summary>
    // / Extchar2ColInfo : 对 Extchar2 字段的ColInfo对象的访问
    // / 字段Extchar2的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节]
    // / </summary>
    public ColInfo getExtchar2ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extchar2");
        }
    }

    // / <summary>
    // / Extchar3 : 扩展字符属性3 扩展字符属性3，解释同extint1
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节
    // / </summary>
    public String getExtchar3() {
        {
            return GetString(getExtchar3ColInfo());
        }
    }

    // / <summary>
    // / Extchar3ColInfo : 对 Extchar3 字段的ColInfo对象的访问
    // / 字段Extchar3的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为255字节]
    // / </summary>
    public ColInfo getExtchar3ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extchar3");
        }
    }

    // / <summary>
    // / Extchar4 : 扩展字符属性4 扩展字符属性3，解释同extint1
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 可以为空 限制长度为255字节
    // / </summary>
    public String getExtchar4() {
        {
            return GetString(getExtchar4ColInfo());
        }
    }

    // / <summary>
    // / Extchar4ColInfo : 对 Extchar4 字段的ColInfo对象的访问
    // / 字段Extchar4的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 可以为空 限制长度为255字节]
    // / </summary>
    public ColInfo getExtchar4ColInfo() {
        {
            return ColInfos.Get(CnstTableName, "extchar4");
        }
    }

    // / <summary>
    // / Parentid : 父节点
    // 父节点ID，对于一级节点（或如果一个字典仅有一级节点，例如性别），则此处值为-1，否则存储相应的父节点的本表中的id，例如采矿方式中如果既有地表采矿方式的三种子分类，也有地下采矿方式的三种分类，或最简单的例子如单位部门字典中的二级、三级单位，此处即为其父节点ID（注意，不是与refid，而是与id进行外键关联）
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为40字节
    // / </summary>
    public String getParentid() {
        {
            return GetString(getParentidColInfo());
        }
    }

    // / <summary>
    // / ParentidColInfo : 对 Parentid 字段的ColInfo对象的访问
    // / 字段Parentid的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为40字节]
    // / </summary>
    public ColInfo getParentidColInfo() {
        {
            return ColInfos.Get(CnstTableName, "parentid");
        }
    }

    // / <summary>
    // / Indexid : 排序号 排序ID，此字段用于表明亲兄弟节点中此节点的排列位置，此字段与parentid组合可用于生成sortcode构造树
    // / 默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getIndexid() {

        {
            return GetInt(getIndexidColInfo());
        }
    }

    // / <summary>
    // / IndexidColInfo : 对 Indexid 字段的ColInfo对象的访问
    // / 字段Indexid的特性如下:[默认值 : 0
    // / 字段类型 : INT INT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getIndexidColInfo() {
        {
            return ColInfos.Get(CnstTableName, "indexid");
        }
    }

    // / <summary>
    // / Sortcode : 排序串
    // 排序字符串，形如“0001000003”则表示第10个一级节点下的第3个二级子节点（由indexid及parentid确定），此字段不由操作者填写，而是依据indexid及parentid字段冗余存储的信息，这是为了方便在前台展现数据时避免使用递归方法，而是采用一个数据库查询语句即可得到按目录及顺序排列的所有字典项
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为100字节
    // / </summary>
    public String getSortcode() {

        {
            return GetString(getSortcodeColInfo());
        }
    }

    // / <summary>
    // / SortcodeColInfo : 对 Sortcode 字段的ColInfo对象的访问
    // / 字段Sortcode的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为100字节]
    // / </summary>
    public ColInfo getSortcodeColInfo() {
        {
            return ColInfos.Get(CnstTableName, "sortcode");
        }
    }

    // / <summary>
    // / Remark : 备注 备注信息，关于字典项的备注描述
    // / 默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为1000字节
    // / </summary>
    public String getRemark() {
        {
            return GetString(getRemarkColInfo());
        }
    }

    // / <summary>
    // / RemarkColInfo : 对 Remark 字段的ColInfo对象的访问
    // / 字段Remark的特性如下:[默认值 :
    // / 字段类型 : VARCHAR VARCHAR
    // / 字段特性 : 不可以为空 限制长度为1000字节]
    // / </summary>
    public ColInfo getRemarkColInfo() {
        {
            return ColInfos.Get(CnstTableName, "remark");
        }
    }

    // / <summary>
    // / Isleaf : 是否可选叶节点 是否可选叶节点，参见category表中useleaf字段的描述
    // / 默认值 : 1
    // / 字段类型 : INT TINYINT
    // / 字段特性 : 不可以为空
    // / </summary>
    public int getIsleaf() {
        {
            return GetInt(getIsleafColInfo());
        }
    }

    // / <summary>
    // / IsleafColInfo : 对 Isleaf 字段的ColInfo对象的访问
    // / 字段Isleaf的特性如下:[默认值 : 1
    // / 字段类型 : INT TINYINT
    // / 字段特性 : 不可以为空 ]
    // / </summary>
    public ColInfo getIsleafColInfo() {
        {
            return ColInfos.Get(CnstTableName, "isleaf");
        }
    }

    // #endregion 字段读取方法
    // / <summary>
    // / 取得本选项的根级选项（即一级选项）的值
    // / </summary>
    public String getMyRootRefid() {
        {
            if (this.getIsExists() == false) {
                return "";
            }
            if (this.getSortcode().length() <= 5) {
                return this.getRefid();
            }
            return CategoryvalueViews.Child(this.getParentid()).getMyRootRefid();
        }
    }

    // / <summary>
    // / 取得递归的名称显示
    // / </summary>
    // / <param name="m_split">分隔字符串</param>
    // / <param name="m_reverse_order">true采用子-父序,false采用父-子序</param>
    // / <returns></returns>
    public String RecursionName(String m_split, Boolean m_reverse_order) {
        if (this.getIsExists() == false) {
            return "";
        }
        String s = this.getChinaname();
        Categoryvalue m_categoryvalue = CategoryvalueViews.Child(this.getParentid());
        if (m_categoryvalue.getIsExists() == false) {
            return s;
        }
        if (m_reverse_order) {
            return s + m_split + m_categoryvalue.RecursionName(m_split, m_reverse_order);
        } else {
            return m_categoryvalue.RecursionName(m_split, m_reverse_order) + m_split + s;
        }
    }

    // / <summary>
    // / 取得递归的名称显示 采用父-子序
    // / </summary>
    // / <param name="m_split">分隔字符串</param>
    // / <returns></returns>
    public String RecursionName(String m_split) {
        return RecursionName(m_split, false);
    }

    // / <summary>
    // / 取得递归的名称显示 采用父-子序，中间分隔采用“ - ”
    // / </summary>
    public String RecursionName() {
        return RecursionName(" - ", false);
    }

    // / <summary>
    // / 取得本节点的所有子孙级节点的refid（包含已删除的），各refid间用“,”号进行分隔
    // / </summary>
    public String getAllChildRefid() {
        {
            return CategoryViews.Child(this.getCategoryid()).GetChildrenRefid(this, true, true);
        }
    }

    // / <summary>
    // / 取得本节点的所有未删除的子孙级节点的refid，各refid间用“,”号进行分隔
    // / </summary>
    public String getAllChildRefidNoDeleted() {
        {
            return CategoryViews.Child(this.getCategoryid()).GetChildrenRefid(this, true, true);
        }
    }

    // / <summary>
    // / 所有直系儿子节点的refid（包含已删除的），各refid间用“,”号进行分隔
    // / </summary>
    public String getAllSonRefid() {
        {
            return CategoryViews.Child(this.getCategoryid()).GetChildrenRefid(this, false, true);
        }
    }

    // / <summary>
    // / 所有未删除的直系儿子节点的refid，各refid间用“,”号进行分隔
    // / </summary>
    public String getAllSonRefidNoDeleted() {
        {
            return CategoryViews.Child(this.getCategoryid()).GetChildrenRefid(this, false, false);
        }
    }

    // / <summary>
    // / 是否Delstatus == 0，此属性递归检测该选项的所有父、爷、祖、……级节点是否都可用
    // / </summary>
    public boolean getIsUsing() {
        {
            if (this.getDelstatus() > 0) {
                return false;
            } else {
                if (this.getParentid().length() == 0) {
                    return true;
                } else {
                    return CategoryvalueViews.Child(this.getParentid()).getIsUsing();
                }
            }
        }
    }

    // / <summary>
    // / 取得本节点的所有父节点（含自己）的refid
    // / </summary>
    public String getAllParentRefid() {
        {
            if (this.getParentid().length() == 0) {
                return getRefid();
            }
            return getRefid() + "," + CategoryvalueViews.Child(this.getParentid()).getAllParentRefid();
        }
    }

}
