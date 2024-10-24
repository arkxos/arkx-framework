package com.rapidark.framework.data.jpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * 自身树状结构实体基类
 * 
 * @author Darkness
 * @date 2020-09-15 17:08:49
 * @version V1.0
 */
@Getter
@Setter
@MappedSuperclass
public class TreeEntity extends IdLongEntity {

	@Column(name = "INNER_CODE")
	private String innerCode;

	@Column(name = "PARENT_INNER_CODE")
	private String parentInnerCode = "0";
	
	@Column(name = "TREE_LEVEL")
	private Long treeLevel = 1L;
	
	@Column(name = "IS_LEAF")
	private String isLeaf = "Y";
	
	@Column(name = "IS_TREE_LEAF")
	private int isTreeLeaf = 1;

	@Column(name = "SORT_ORDER")
	private long sortOrder = 0;// 排序号

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
	
	@Override
	public String toString() {
		return "innerCode: "+ getInnerCode() + ", parentInnerCode: " + getParentInnerCode() + ", "+ super.toString();
	}

}
