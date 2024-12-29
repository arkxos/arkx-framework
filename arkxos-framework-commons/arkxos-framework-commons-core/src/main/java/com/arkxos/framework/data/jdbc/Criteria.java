package com.arkxos.framework.data.jdbc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.ark.framework.infrastructure.entityfactoryframework.EntityBuilderFactory;
import org.ark.framework.orm.sql.DBUtil;

import com.arkxos.framework.annotation.Column;
import com.arkxos.framework.annotation.Entity;
import com.arkxos.framework.annotation.Ingore;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTableUtil;
import com.arkxos.framework.commons.collection.IPageData;
import com.arkxos.framework.commons.collection.IPageInfo;
import com.arkxos.framework.commons.collection.PageDataTable;
import com.arkxos.framework.commons.util.lang.ReflectionUtil;


/**
 * @class org.ark.framework.orm.query.Criteria
 * 规格约束查询模式
 * 
 * 示例： 
 * <pre>
 * Criteria criteria = getSession().createCriteria(ParticipantParamValue.class);
 * criteria.add(Restrictions.eq(ParticipantParamValue.ParticipantValueId, getId()));
 * criteria.add(Restrictions.like(ParticipantParamValue.ParticipantValue, "abc")); 
 * criteria.add(Restrictions.in(ParticipantParamValue.ParticipantValue, "'a', 'b', 'c'"));
 * 
 * Date startTime = DateUtil.parseDateTime("2012-02-22 11:25:14");
		Date endTime = DateUtil.parseDateTime("2012-02-28 11:25:14");
		criteria.add(Restrictions.gt(Person.Birthday, startTime));
		criteria.add(Restrictions.lt(Person.Birthday, endTime));
		
 * criteria.addOrder(Order.asc(ParticipantParam.SortOrder));
 * 
 * List<ParticipantParamValue> paramValues = criteria.findEntities();
 * </pre>
 * @author Darkness
 * @date 2012-9-15 上午9:47:44
 * @version V1.0
 */
public class Criteria {

	private Class<? extends com.arkxos.framework.data.jdbc.Entity> domainObjectClass;
	private List<Restrictions> restrictions = new ArrayList<Restrictions>();
	private List<Order> orders = new ArrayList<Order>();

	private Query qb;

	 Criteria(Transaction transaction, Class<? extends com.arkxos.framework.data.jdbc.Entity> domainObjectClass) {
		this.domainObjectClass = domainObjectClass;

		String tableName = null;
		if (domainObjectClass.isAnnotationPresent(Entity.class)) {
			tableName = domainObjectClass.getAnnotation(Entity.class).name();
		} else {
			tableName = domainObjectClass.getSimpleName();
		}

		qb = new Query(transaction, "select * from " + tableName + " where 1=1");
	}

	public Criteria add(Restrictions criteria) {
		restrictions.add(criteria);
		return this;
	}

	public Criteria addOrder(Order order) {
		orders.add(order);
		return this;
	}
	
	/**
	 * 是否存在该排序
	 * 
	 * @author Darkness
	 * @date 2013-3-28 下午03:24:07 
	 * @version V1.0
	 */
	public boolean isExistOrder(Order order) {
		for (Order _order : orders) {
			if(_order.equals(order)) {
				return true;
			}
		}
		return false;
	}

	public DataTable dataTable() {

		initQuery();

		return qb.executeDataTable();
	}

	@SuppressWarnings("unchecked")
	public <T> T findEntity() {

		initQuery();
		DataTable dataTable = qb.executeDataTable();
		return (T) EntityBuilderFactory.buildEntityFromDataTable(domainObjectClass, dataTable);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findEntities() {

		initQuery();
		DataTable dataTable = qb.executeDataTable();
		List<T> result = (List<T>) EntityBuilderFactory.buildEntitiesFromDataTable(domainObjectClass, dataTable);
		
		if(result == null) {
			return new ArrayList<>();
		}
		
		return result;
	}

	private void setQueryBuilderWithRestriction(Restrictions restriction){
		if (Restrictions.IN.equals(restriction.getSqlOperator())) {
			qb.append(restriction.getField() + " IN (" + restriction.getValue() + ")");
		} else {
			qb.append(restriction.getField() + " " + restriction.getSqlOperator() + " ? ", restriction.getValue());
		}
	}
	
	private void initQuery() {
		for (Restrictions restriction : restrictions) {
			
			if (restriction == null || (restriction.isRestriction() && restriction.isNull())) {
				continue;
			}
			
			qb.append(" and ");
			
			if(restriction.isRestriction()) {
				
				if(restriction.isSingle()) {
					setQueryBuilderWithRestriction(restriction.getSingle());
				} else {
					qb.append("(");
					
					setQueryBuilderWithRestriction(restriction.getFirstRestrictions());
					
					qb.append(" " + restriction.getSqlOperator() + " ");
					
					setQueryBuilderWithRestriction(restriction.getSecondRestrictions());
					
					qb.append(")");
				}
			}  else {
				setQueryBuilderWithRestriction(restriction);
			}
		}

		boolean isFirst = true;
		for (Order order : orders) {

			if (!isFirst) {
				qb.append(",");
			} else {
				qb.append(" order by ");
			}

			qb.append(" " + order.getField() + " " + order.getOrder());

			if (isFirst) {
				isFirst = false;
			}
		}
	}

	private <T> void initQuery(T example) {

		Field[] fields = ReflectionUtil.getDeclaredFields(example.getClass());
		for (Field field : fields) {

			Ingore ingore = field.getAnnotation(Ingore.class);
			if (ingore != null) {
				continue;
			}

			String columnName = field.getName();

			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				columnName = column.name();
			}

			Object fieldValue = ReflectionUtil.getFieldValue(example, field.getName());
			if (fieldValue == null) {
				continue;
			}

			qb.append(" and " + columnName + " = ?", fieldValue);
		}
	}

	public IPageData page(IPageInfo pageInfo) {

		PageDataTable result = new PageDataTable();

		result.setPageEnabled(pageInfo.isPageEnabled());

		initQuery();

		if (pageInfo.isPageEnabled()) {
			result.setTotal(DBUtil.getCount(qb));
			result.setPageIndex(pageInfo.getPageIndex());
			result.setPageSize(pageInfo.getPageSize());
			
			List<? extends com.arkxos.framework.data.jdbc.Entity> entities = qb.findPagedEntities(domainObjectClass,pageInfo.getPageSize(), pageInfo.getPageIndex());
			
			result.setData(DataTableUtil.toDataTable(entities));
		} else {
			
			List<? extends com.arkxos.framework.data.jdbc.Entity> entities = qb.findEntities( domainObjectClass);
			
			result.setData(DataTableUtil.toDataTable(entities));
		}
		return result;
	}

	/**
	 * 根据example查询实体
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午05:02:21 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	public <T> T findEntityByExample(T entityExample) {

		initQuery(entityExample);

		DataTable dataTable = qb.executeDataTable();
		
		return (T) EntityBuilderFactory.buildEntityFromDataTable(domainObjectClass, dataTable);
	}

	/**
	 *  根据example查询实体集合
	 * 
	 * @author Darkness
	 * @date 2012-11-25 下午05:02:41 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findEntitiesByExample(T entityExample) {

		initQuery(entityExample);

		DataTable dataTable = qb.executeDataTable();
		List<T> result = (List<T>) EntityBuilderFactory.buildEntitiesFromDataTable(domainObjectClass, dataTable);
		
		if(result == null) {
			return new ArrayList<T>();
		}
		return result;
	}

}
