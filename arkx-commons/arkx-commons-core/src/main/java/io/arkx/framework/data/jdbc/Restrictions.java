package io.arkx.framework.data.jdbc;

import java.util.Date;

import io.arkx.framework.commons.util.StringUtil;



/**   
 * @class org.ark.framework.orm.query.Restrictions
 * 约束
 * 
 * 方法 说明
Restrictions.eq 等于
Restrictions.allEq 使用Map，使用key/value进行多个等于的比对
Restrictions.gt 大于 >
Restrictions.ge 大于等于 >=
Restrictions.lt 小于 <
Restrictions.le 小于等于 <=
Restrictions.between 对应SQL的BETWEEN子句
Restrictions.like 对应SQL的LIKE子句
Restrictions.in 对应SQL的in子句
Restrictions.and and关係
Restrictions.or or关係
 * @author Darkness
 * @date 2012-9-15 上午10:01:34 
 * @version V1.0   
 */
public class Restrictions {

	public static final String IN = "in";
	public static final String OR = "or";
	public static final String LIKE = "like";
	public static final String EQ = "=";
	
//	public static Restrictions between(String filed, Date startValue, Date endValue) {
//		return new Restrictions("bewteen", filed, value);
//	}
	
	public static Restrictions gt(String filed, String value) {
		if(value == null) {
			return null;
		}
		return new Restrictions(">", filed, value);
	}
	
	public static Restrictions gt(String filed, Date value) {
		if(value == null) {
			return null;
		}
		return new Restrictions(">", filed, value);
	}
	
	public static Restrictions ge(String filed, String value) {
		if(value == null) {
			return null;
		}
		return new Restrictions(">=", filed, value);
	}
	
	public static Restrictions lt(String filed, String value) {
		if(value == null) {
			return null;
		}
		return new Restrictions("<", filed, value);
	}
	
	public static Restrictions lt(String filed, Date value) {
		if(value == null) {
			return null;
		}
		return new Restrictions("<", filed, value);
	}
	
	public static Restrictions le(String filed, String value) {
		if(value == null) {
			return null;
		}
		return new Restrictions("<=", filed, value);
	}
	
	public static Restrictions or(Restrictions restrictions1, Restrictions restrictions2) {
		
		return new Restrictions(OR, restrictions1, restrictions2);
	}
	
	/**
	 * 会过滤空
	 * 
	 * @author Darkness
	 * @date 2012-11-13 上午11:34:08 
	 * @version V1.0
	 */
	public static Restrictions like(String filed, String value) {
		if(StringUtil.isEmpty(value)) {
			return null;
		}
		return new Restrictions(LIKE, filed, "%" + value+ "%");
	}
	
	/**
	 * 会过滤空
	 * 
	 * @author Darkness
	 * @date 2012-11-22 下午12:22:19 
	 * @version V1.0
	 */
	public static Restrictions beforeLike(String filed, String value) {
		if(StringUtil.isEmpty(value)) {
			return null;
		}
		return new Restrictions(LIKE, filed, value+ "%");
	}
	
	/**
	 * 会过滤空
	 * 
	 * @author Darkness
	 * @date 2012-11-22 下午12:23:30 
	 * @version V1.0
	 */
	public static Restrictions endLike(String filed, String value) {
		if(StringUtil.isEmpty(value)) {
			return null;
		}
		return new Restrictions(LIKE, filed, "%" + value);
	}
	
	/**
	 * 非
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午05:56:58 
	 * @version V1.0
	 */
	public static Restrictions not(String filed, String value) {
		if(StringUtil.isEmpty(value)) {
			return null;
		}
		return new Restrictions("!=", filed, value);
	}
	
	/**
	 * 不会过滤空
	 * 
	 * @author Darkness
	 * @date 2012-11-13 上午11:33:33 
	 * @version V1.0
	 */
	public static Restrictions eq(String field, Object value) {
//		if(StringUtil.isEmpty(value)) {
//			return null;
//		}
		return new Restrictions("=", field, value);
	}
	
	public static Restrictions in(String filed, String value) {
		return new Restrictions(IN, filed, value);
	}
	
	private String sqlOperator;
	private String field;
	private Object value;
	
	private boolean isRestriction = false;
	private Restrictions restrictions;
	private Restrictions restrictions2;
	
	private Restrictions(String sqlOperator, String filed, Object value) {
		this.sqlOperator = sqlOperator;
		this.field = filed;
		this.value = value;
	}
	
	private Restrictions(String sqlOperator, Restrictions restrictions, Restrictions restrictions2) {
		isRestriction = true;
		this.sqlOperator = sqlOperator;
		this.restrictions = restrictions;
		this.restrictions2 = restrictions2;
	}
	
	public boolean isRestriction() {
		return isRestriction;
	}
	
	public boolean isNull() {
		return restrictions == null && restrictions2 == null;
	}
	
	public boolean isSingle() {
		return restrictions == null || restrictions2 == null;
	}
	
	public Restrictions getSingle() {
		if(restrictions !=null) {
			return restrictions;
		}
		
		return restrictions2;
	}
	
	public Restrictions getFirstRestrictions() {
		return restrictions;
	}
	
	public Restrictions getSecondRestrictions() {
		return restrictions2;
	}
	
	public String getSqlOperator() {
		return sqlOperator;
	}

	public void setSqlOperator(String sqlOperator) {
		this.sqlOperator = sqlOperator;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
