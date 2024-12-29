package com.arkxos.framework.data.jdbc;

import com.arkxos.framework.annotation.Column;
import com.arkxos.framework.annotation.Ingore;

/**   
 * @class org.ark.framework.infrastructure.domainbase.TreeEntity
 * @extends org.ark.framework.infrastructure.domainbase.Entity
 * 自身树状结构实体基类
 * 
 * @author Darkness
 * @date 2012-12-2 下午04:43:18 
 * @version V1.0 
 * @see com.arkxos.framework.data.jpa.entity.TreeEntity
 */
@Deprecated
public class TreeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Ingore
	public static final String INNER_CODE = "INNERCODE";// 编码
	
	@Ingore
	public static final String PARENT_INNER_CODE = "PARENTINNERCODE";// 父编码
	
	/**
	 * 编码[字段名称常量]
	 * 
	 * @property InnerCode
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String InnerCode = "INNER_CODE";// 编码

	/**
	 * 父编码[字段名称常量]
	 * 
	 * @property ParentInnerCode
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String ParentInnerCode = "PARENT_INNER_CODE";// 父编码
	
	/**
	 * 级别[字段名称常量]
	 * 
	 * @property TreeLevel
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String TreeLevel = "TREE_LEVEL";// 级别
	
	/**
	 * 是否是叶子节点[字段名称常量]
	 * 
	 * @property IsLeaf
	 * @type {String}
	 * @static
	 */
	@Ingore
	public static final String IsLeaf = "IS_LEAF";// 是否是叶子节点

	@Column(name = "INNER_CODE")
	private String innerCode;

	@Column(name = "PARENT_INNER_CODE")
	private String parentInnerCode = "0";
	
	@Column(name = "TREE_LEVEL")
	private Long treeLevel = 1L;
	
	@Column(name = "IS_LEAF")
	private String isLeaf = "Y";
	
	/**
	 * 是否是顶级节点
	 * @method isTop
	 * @return {boolean}
	 * @author Darkness
	 * @date 2012-9-11 下午1:38:55
	 * @version V1.0
	 */
	public boolean isTop() {

		return this.getParentInnerCode().equals(this.getInnerCode());
	}
	
	/**
	 * 获取内部编码
	 * @method getInnerCode
	 * @return {String}
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:46:10 
	 * @version V1.0
	 */
	public String getInnerCode() {
		return innerCode;
	}

	/**
	 * 设置内部编码
	 * @method setInnerCode
	 * @param {String} innerCode
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:46:36 
	 * @version V1.0
	 */
	public void setInnerCode(String innerCode) {
		this.innerCode = innerCode;
	}

	/**
	 * 获取内部父节点编码
	 * @method getParentInnerCode
	 * @return {String}
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:47:11 
	 * @version V1.0
	 */
	public String getParentInnerCode() {
		return parentInnerCode;
	}

	/**
	 * 设置内部父节点编码
	 * @method setParentInnerCode
	 * @param {String} parentInnerCode
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:47:33 
	 * @version V1.0
	 */
	public void setParentInnerCode(String parentInnerCode) {
		this.parentInnerCode = parentInnerCode;
	}

	/**
	 * 获取级别
	 * @method getTreeLevel
	 * @return {Long}
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:48:02 
	 * @version V1.0
	 */
	public Long getTreeLevel() {
		return treeLevel;
	}

	/**
	 * 设置级别
	 * @method setTreeLevel
	 * @param {Long} treeLevel
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:48:26 
	 * @version V1.0
	 */
	public void setTreeLevel(Long treeLevel) {
		this.treeLevel = treeLevel;
	}

	/**
	 * 获取是否是叶子节点(Y/N)
	 * @method getIsLeaf
	 * @return {String}
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:49:01 
	 * @version V1.0
	 */
	public String getIsLeaf() {
		return isLeaf;
	}

	/**
	 * 设置是否是叶子节点
	 * @method setIsLeaf
	 * @param {String} isLeaf
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午05:49:32 
	 * @version V1.0
	 */
	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", innerCode: "+ getInnerCode() + ", parentInnerCode: " + getParentInnerCode();
	}
}
