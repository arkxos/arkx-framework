package io.arkx.framework.data.jdbc;

import java.io.Serializable;

import io.arkx.framework.annotation.Ingore;
import io.arkx.framework.commons.util.UuidUtil;


/**
 * @class org.ark.framework.infrastructure.domainbase.Entity
 * 所有实体对象的基类，提供主键id字段
 * 
 * @author Darkness
 * @date 2012-9-25 下午6:32:04
 * @version V1.0
 */
public abstract class Entity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * 主键id字段名称常量
     * @property Id
     * @type {Number} 
     * @static
     */
	@Ingore
	public static final String Id = "id";// 主键
	
	// @Id
	// @GeneratedValue(generator = "system-uuid")
	// @GenericGenerator(name = "system-uuid", strategy = "uuid")
	// An Object that represents the primary identifier value for the class.
	private String id;

	/**
	 * 默认构造器
	 * @method Entity
	 */
	protected Entity() {
	}

	/**
	 * 根据id构造对象
	 * @method Entity
	 * @param {String} id
	 */
	protected Entity(String id) {
		this.id = id;
		if (this.id == null) {
			this.id = generateId();
		}
	}

	/**
	 * 给当前实体对象生成一个新的id
	 * @method generateNewId
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午04:52:33 
	 * @version V1.0
	 */
	public void generateNewId() {
		this.id = generateId();
	}

	/**
	 * 生成uuid，不包含“-”
	 * @static
	 * 
	 * @author Darkness
	 * @date 2013-3-13 下午03:14:17 
	 * @version V1.0
	 */
	public static String generateId() {
		return UuidUtil.base58Uuid();//UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 获取主键
	 * @method getId
	 * @return {String} 主键值
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午04:53:06 
	 * @version V1.0
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置主键
	 * @method setId
	 * @param {String} id
	 * 
	 * @author Darkness
	 * @date 2013-1-31 下午04:53:36 
	 * @version V1.0
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 复写equals方法，如果id相同则返回true
	 * @method equals
	 * @param {Object} entity
	 * @return {boolean}
	 */
	@Override
	public boolean equals(Object entity) {
		if (entity == null || !(entity instanceof Entity)) {
			return false;
		}
		return getId().equals(((Entity) entity).getId());
	}

	/**
	 * Serves as a hash function for this type.
	 * 
	 * @return A hash code for the current Key property.
	 * @private
	 */
	@Override
	public int hashCode() {
		if (this.id == null) {
			return 0;
		}
		return this.id.hashCode();
	}

	public static void main(String[] args) {
		System.out.println(generateId().length());
	}

}
