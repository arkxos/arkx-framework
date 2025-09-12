package io.arkx.framework.data.common.entity;

import io.arkx.framework.commons.collection.tree.TreeNodeData;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
public class TreeEntity<ID> extends BaseEntity<ID> implements TreeNodeData<ID> {

	@Column(name = "parent_id")
	private ID parentId;

	private String name;

	@Column(name = "is_leaf")
	private int isLeaf = 1;

	@Column(name = "sort_order")
	private long sortOrder = 0;// 排序号

	/**
	 * 是否是顶级节点
	 * @method isTop
	 * @return {boolean}
	 * @author Darkness
	 * @date 2012-9-11 下午1:38:55
	 * @version V1.0
	 */
	public boolean isRoot() {
		return parentId == null;
	}

}
