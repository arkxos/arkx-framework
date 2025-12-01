package org.ark.framework.orm;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Filter;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.lang.ClassUtil;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ark.framework.orm.sql.DBContext;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;


/**
 * @class org.ark.framework.orm.SchemaSet
 * @author Darkness
 * @date 2012-3-8 下午2:00:14
 * @version V1.0
 */
@Slf4j
public abstract class SchemaSet<T extends Schema> implements Serializable, Cloneable {

//	private static Logger logger = Logger.getLogger(SchemaSet.class);

	private static final long serialVersionUID = 1L;
	private Class<T> schemaClass;
	private T schema;



	@SuppressWarnings("unchecked")
	public T getSchema() {
		try {
			if(schemaClass == null) {
//				System.out.println(this.getClass());
//				System.out.println(ClassUtil.getGenerateParameter(this.getClass()));
				schemaClass = (Class<T>)ClassUtil.getGenerateParameter(this.getClass());
			}
			if(schema == null)
				schema = schemaClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return schema;
	}

	public void initDeleteQueryBuilder(Query queryBuilder) {
		queryBuilder.setBatchMode(true);
		for (int k = 0; k < elementCount; ++k) {
			Schema schema = elementData[k];
			queryBuilder.add(schema.getPrimaryKeyValue("delete"));
			queryBuilder.addBatch();
		}
	}
	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean delete() {
		Query queryBuilder = getSession().createQuery(getSchema().DeleteSQL);
		initDeleteQueryBuilder(queryBuilder);
//		try {
		return queryBuilder.executeNoQuery() != -1;
//		} catch (SQLException e) {
//			logger.warn("操作表" + getSchema().TableCode + "时发生错误!");
//			e.printStackTrace();
//		}
//		return false;
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean deleteAndInsert() {
		try {
			// this.setAutoCommit(false);
			this.delete();
			this.insert();
			// this.commit();
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			// try {
			// this.rollback();
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			return false;
		} finally {
			// try {
			// this.setAutoCommit(true);
			// this.close();
			// } catch (SQLException e) {
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean deleteAndBackup(String backupOperator, String backupMemo) {
		try {
			try {
				// this.setAutoCommit(false);
				this.delete();
				this.getBackUpSchema(backupOperator, backupMemo).insert();
				// this.commit();
				return true;
			} catch (Throwable e) {
				e.printStackTrace();
				// try {
				// this.rollback();
				// } catch (SQLException e1) {
				// e1.printStackTrace();
				// }
				return false;
			} finally {
				// try {
				// this.setAutoCommit(true);
				// this.close();
				// } catch (SQLException e) {
				// e.printStackTrace();
				// }
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean backup() {
		return backup(null, null);
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean backup(String backupOperator, String backupMemo) {
		try {
			return this.getBackUpSchema(backupOperator, backupMemo).insert();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean deleteAndBackup() {
		return deleteAndBackup(null, null);
	}

	public boolean insert() {
		try {
//			try {
			if (elementCount == 0) {
				return true;
			}

			Query queryBuilder = getSession().createQuery(getSchema().InsertAllSQL);

			initInsertQueryBuilder(queryBuilder);

			return queryBuilder.executeNoQuery() != -1;
//			} catch (SQLException e) {
//				logger.warn("操作表" + getSchema().TableCode + "时发生错误:" + e.getMessage());
//				throw e;
//			}

		} catch (Exception e) {
			log.warn("由批处理模式转换成非批处理模式:");
			int failCount = 0;
			for (int k = 0; k < elementCount; ++k) {
				try {
					Query queryBuilder = getSession().createQuery(getSchema().InsertAllSQL);
					T schema = elementData[k];
					// queryBuilder.add(schema.getPrimaryKeyValue("delete"));
					schema.setInsertParams(queryBuilder);
					queryBuilder.executeNoQuery();
				} catch (SQLException e1) {
					failCount++;
					e1.printStackTrace();
				}
			}
			log.warn("共" + elementCount + "条记录，" + (elementCount - failCount) + "条记录导入成功，" + failCount + "条记录失败...");
		}
		return true;
	}

	private String _poolName;
	private String getPoolName() {
		if(StringUtil.isEmpty(_poolName)) {
			if(DBContext.getCurrentContext() != null)
				_poolName = DBContext.getCurrentContext().getPoolName();
		}
		return _poolName;
	}

	public void initInsertQueryBuilder(Query queryBuilder) {
		try {
			queryBuilder.setBatchMode(true);

			for (int k = 0; k < this.elementCount; ++k) {
				T schema = this.elementData[k];

				schema.setInsertParams(queryBuilder);

				queryBuilder.addBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected SchemaSet(int initialCapacity, int capacityIncrement) {
		bOperateFlag = false;
		if (initialCapacity < 0) {
			initialCapacity = 0;
			log.warn("SchemaSet的初始容量不能小于0，将被默认置0...");
		}

		elementData = createSchemaSet(initialCapacity);
		this.capacityIncrement = capacityIncrement;
		elementCount = 0;
	}

	public abstract T[] createSchemaSet(int initialCapacity);

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean update() {
		Query queryBuilder = getSession().createQuery(getSchema().getUpdateSql());

		initUpdateQueryBuilder(queryBuilder);

//		try {
		return queryBuilder.executeNoQuery() != -1;
//		} catch (Throwable e) {
//			logger.getLogger().warn("操作表" + getSchema().TableCode + "时发生错误:" + e.getMessage());
//			throw new RuntimeException(e);
//		}
	}

	public void initUpdateQueryBuilder(Query queryBuilder) {
		queryBuilder.setBatchMode(true);
		for (int k = 0; k < this.elementCount; ++k) {
			T schema = this.elementData[k];
			schema.setUpdataParams(queryBuilder);
			queryBuilder.addBatch();
		}
	}

	protected SchemaSet(int initialCapacity) {
		this(initialCapacity, 0);
	}

	protected SchemaSet() {
		this(10);
	}

	public boolean add(T s) {
		if (s == null || s.TableCode != getSchema().TableCode) {
			log.warn("传入的参数不是一个" + getSchema().TableCode + "Schema");
			return false;
		}
		ensureCapacityHelper(elementCount + 1);
		elementData[elementCount] = s;
		elementCount++;
		return true;
	}

	public boolean add(SchemaSet<T> aSet) {
		if (aSet == null)
			return false;
		int n = aSet.size();
		ensureCapacityHelper(elementCount + n);
		for (int i = 0; i < n; i++)
			elementData[elementCount + i] = aSet.getObject(i);

		elementCount += n;
		return true;
	}

	/**
	 * 将另一个SchemaSet中的所有Schema对象添加到当前SchemaSet中
	 * 此方法与add(SchemaSet)功能相同，但命名保持与Collection接口一致
	 *
	 * @param schemaSet 要添加的SchemaSet
	 * @return 如果添加成功则返回true，否则返回false
	 */
	public boolean addAll(SchemaSet<T> schemaSet) {
		return add(schemaSet);
	}

	/**
	 * 将任意泛型类型的SchemaSet中的所有Schema对象添加到当前SchemaSet中
	 * 此方法处理通配符泛型，确保在使用SchemaSet<?>时也能正常工作
	 *
	 * @param schemaSet 要添加的任意泛型类型的SchemaSet
	 * @return 如果添加成功则返回true，否则返回false
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean addAllWildcard(SchemaSet<?> schemaSet) {
		if (schemaSet == null)
			return false;

		if (schemaSet.isEmpty())
			return true;

		// 检查表代码是否匹配
		String sourceTableCode = schemaSet.getSchema().TableCode;
		String targetTableCode = getSchema().TableCode;

		if (!sourceTableCode.equals(targetTableCode)) {
			log.warn("尝试将表 {} 的SchemaSet添加到表 {} 的SchemaSet，表代码不匹配",
					sourceTableCode, targetTableCode);
			return false;
		}

		// 使用原始类型进行操作，绕过泛型检查
		// 因为我们已经验证了表代码匹配，这个操作在运行时应该是安全的
		int n = schemaSet.size();
		ensureCapacityHelper(elementCount + n);

		for (int i = 0; i < n; i++) {
			Schema schema = schemaSet.getObject(i);
			// 这里的类型转换在运行时是安全的，因为我们已经验证了表代码匹配
			elementData[elementCount + i] = (T)schema;
		}

		elementCount += n;
		return true;
	}

	/**
	 * 将一个集合中的所有Schema对象添加到当前SchemaSet中
	 *
	 * @param collection 包含要添加的Schema对象的集合
	 * @return 如果全部添加成功则返回true，否则返回false
	 */
	public boolean addAll(Collection<T> collection) {
		if (collection == null || collection.isEmpty())
			return false;

		int n = collection.size();
		ensureCapacityHelper(elementCount + n);

		boolean allSuccess = true;
		for (T schema : collection) {
			if (schema == null || schema.TableCode != getSchema().TableCode) {
				log.warn("集合中包含null或类型不匹配的Schema对象: {}",
						schema == null ? "null" : schema.TableCode);
				allSuccess = false;
				continue;
			}

			elementData[elementCount++] = schema;
		}

		return allSuccess;
	}

	public boolean remove(T aSchema) {
		if (aSchema == null)
			return false;
		for (int i = 0; i < elementCount; i++)
			if (aSchema.equals(elementData[i])) {
				int j = elementCount - i - 1;
				if (j > 0)
					System.arraycopy(elementData, i + 1, elementData, i, j);
				elementCount--;
				elementData[elementCount] = null;
				return true;
			}

		return false;
	}

	public boolean removeRange(int index, int length) {
		if ((index < 0) || (length < 0) || (index + length > this.elementCount)) {
			return false;
		}
		if (this.elementCount > index + length) {
			System.arraycopy(this.elementData, index + length, this.elementData, index, length);
		}
		for (int i = 0; i < length; i++) {
			this.elementData[(this.elementCount - i - 1)] = null;
		}
		this.elementCount -= length;
		return true;
	}

	public void clear() {
		for (int i = 0; i < this.elementCount; i++) {
			this.elementData[i] = null;
		}
		this.elementCount = 0;
	}

	public boolean isEmpty() {
		return this.elementCount == 0;
	}

	public T getObject(int index) {
		if (index > this.elementCount) {
			throw new RuntimeException("SchemaSet索引过大," + index);
		}
		return this.elementData[index];
	}

	public boolean set(int index, T aSchema) {
		if (index > this.elementCount) {
			throw new RuntimeException("SchemaSet索引过大," + index);
		}
		this.elementData[index] = aSchema;
		return true;
	}

	public boolean set(SchemaSet<T> aSet) {
		this.elementData = aSet.elementData;
		this.elementCount = aSet.elementCount;
		this.capacityIncrement = aSet.capacityIncrement;
		return true;
	}

	public int size() {
		return this.elementCount;
	}

	private void ensureCapacityHelper(int minCapacity) {
		int oldCapacity = this.elementData.length;
		if (minCapacity > oldCapacity) {
			Object[] oldData = this.elementData;
			int newCapacity = this.capacityIncrement > 0 ? oldCapacity + this.capacityIncrement : oldCapacity * 2;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			this.elementData = createSchemaSet(newCapacity);
			System.arraycopy(oldData, 0, this.elementData, 0, this.elementCount);
		}
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public void setOperateColumns(String colNames[]) {
		if (colNames == null || colNames.length == 0) {
			bOperateFlag = false;
			return;
		}
		operateColumnOrders = new int[colNames.length];
		int i = 0;
		int k = 0;
		for (; i < colNames.length; i++) {
			boolean flag = false;
			for (int j = 0; j < Columns.length; j++) {
				if (!colNames[i].toString().toLowerCase().equals(Columns[j].getColumnName().toLowerCase()))
					continue;
				operateColumnOrders[k] = j;
				k++;
				flag = true;
				break;
			}

			if (!flag)
				throw new RuntimeException("指定的列名" + colNames[i] + "不正确");
		}

		bOperateFlag = true;
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public void setOperateColumns(int colOrder[]) {
		if (colOrder == null || colOrder.length == 0) {
			bOperateFlag = false;
			return;
		}
		for (int i = 0; i < elementCount; i++)
			elementData[i].setOperateColumns(colOrder);

		operateColumnOrders = colOrder;
		bOperateFlag = true;
	}

	public DataTable toDataTable() {
		DataColumn dcs[];
		Object values[][];
		DataTable dt;
		if (bOperateFlag) {
			dcs = new DataColumn[operateColumnOrders.length];
			values = new Object[elementCount][Columns.length];
			for (int i = 0; i < operateColumnOrders.length; i++) {
				DataColumn dc = new DataColumn();
				dc.setColumnName(Columns[operateColumnOrders[i]].getColumnName());
				dc.setColumnType(Columns[operateColumnOrders[i]].getColumnType());
				dcs[i] = dc;
			}

			for (int i = 0; i < elementCount; i++) {
				for (int j = 0; j < operateColumnOrders.length; j++)
					values[i][j] = elementData[i].getV(operateColumnOrders[j]);

			}

			dt = new DataTable(dcs, values);
			return dt;
		}
		dcs = new DataColumn[Columns.length];
		values = new Object[elementCount][Columns.length];
		for (int i = 0; i < Columns.length; i++) {
			DataColumn dc = new DataColumn();
			dc.setColumnName(Columns[i].getColumnName());
			dc.setColumnType(Columns[i].getColumnType());
			dcs[i] = dc;
		}

		for (int i = 0; i < elementCount; i++) {
			for (int j = 0; j < Columns.length; j++)
				values[i][j] = elementData[i].getV(j);

		}

		return new DataTable(dcs, values);
	}

	public Object clone() {
		SchemaSet<T> set = newInstance();
		for (int i = 0; i < size(); i++)
			set.add((T) elementData[i].clone());

		return set;
	}

	public void sort(Comparator c) {
		T[] newData = createSchemaSet(elementCount);
		System.arraycopy(elementData, 0, newData, 0, elementCount);
		Arrays.sort(newData, c);
		elementData = newData;
	}

	public void sort(String columnName) {
		sort(columnName, "desc", false);
	}

	public void sort(String columnName, String order) {
		sort(columnName, order, false);
	}

	public void sort(String columnName, String order, final boolean isNumber) {
		final String cn = columnName;
		final String od = order;
		sort(new Comparator() {

			public int compare(Object obj1, Object obj2) {
				DataRow dr1 = ((T) obj1).toDataRow();
				DataRow dr2 = ((T) obj2).toDataRow();
				Object v1 = dr1.get(cn);
				Object v2 = dr2.get(cn);
				if ((v1 instanceof Number) && (v2 instanceof Number)) {
					double d1 = ((Number) v1).doubleValue();
					double d2 = ((Number) v2).doubleValue();
					if (d1 == d2)
						return 0;
					if (d1 > d2)
						return "asc".equalsIgnoreCase(od) ? 1 : -1;
					else
						return "asc".equalsIgnoreCase(od) ? -1 : 1;
				}
				if ((v1 instanceof Date) && (v2 instanceof Date)) {
					Date d1 = (Date) v1;
					Date d2 = (Date) v1;
					if ("asc".equalsIgnoreCase(od))
						return d1.compareTo(d2);
					else
						return -d1.compareTo(d2);
				}
				if (isNumber) {
					double d1 = 0.0D;
					double d2 = 0.0D;
					try {
						d1 = Double.parseDouble(String.valueOf(v1));
						d2 = Double.parseDouble(String.valueOf(v2));
					} catch (Exception exception) {
					}
					if (d1 == d2)
						return 0;
					if (d1 > d2)
						return "asc".equalsIgnoreCase(od) ? -1 : 1;
					else
						return "asc".equalsIgnoreCase(od) ? 1 : -1;
				}
				int c = dr1.getString(cn).compareTo(dr2.getString(cn));
				if ("asc".equalsIgnoreCase(od))
					return c;
				else
					return -c;
			}

		});
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public SchemaSet<T> filter(Filter filter) {
		SchemaSet<T> set = newInstance();
		for (int i = 0; i < elementData.length; i++)
			if (filter.filter(elementData[i]))
				set.add((T) elementData[i].clone());

		return set;
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public SchemaSet getBackUpSchema(String backupOperator, String backupMemo) {
		Class s = null;
		try {
			s = Class.forName("com.xdarkness.schema.B" + getSchema().TableCode + "Set");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		SchemaSet bSet = null;
		try {
			bSet = (SchemaSet) s.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		for (int k = 0; k < this.elementCount; ++k) {
			Schema schema = this.elementData[k];
			bSet.add((Schema) schema.getBackUpSchema(backupOperator, backupMemo));
		}
		return bSet;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.elementCount; i++) {
			sb.append(this.elementData[i] + "\n");
		}
		return sb.toString();
	}

	protected abstract SchemaSet<T> newInstance();

	@Getter
	protected T elementData[];
	protected int elementCount;
	private int capacityIncrement;
	//protected String NameSpace;
	protected SchemaColumn Columns[];

	protected boolean bOperateFlag;
	protected int operateColumnOrders[];
	public Session getSession() {
		if(this.session ==  null) {
			session = SessionFactory.currentSession();
		}
		return session;
	}

	private Session session;

	public void setSession(Session session) {
		this.session = session;
	}

}

