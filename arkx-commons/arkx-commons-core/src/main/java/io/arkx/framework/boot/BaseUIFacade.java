package io.arkx.framework.boot;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.ark.framework.infrastructure.repositories.BaseRepository;

import com.arkxos.framework.Constant;
import io.arkx.framework.annotation.Alias;
import io.arkx.framework.annotation.Priv;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTableUtil;
import com.arkxos.framework.commons.collection.IPageData;
import com.arkxos.framework.commons.collection.IPageInfo;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.exception.ServiceException;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.commons.util.lang.ClassUtil;
import com.arkxos.framework.cosyui.control.DataGridAction;
import com.arkxos.framework.cosyui.web.UIFacade;
import com.arkxos.framework.data.jdbc.BaseEntity;
import com.arkxos.framework.data.jdbc.Criteria;
import com.arkxos.framework.data.jdbc.Entity;
import com.arkxos.framework.data.jdbc.Order;
import com.arkxos.framework.data.jdbc.Restrictions;
import com.arkxos.framework.data.jdbc.TreeEntity;
import com.arkxos.framework.i18n.LangMapping;

/**   
 * @class org.ark.framework.jaf.BaseUIFacade
 * Entity UI Facade基类
 * @author Darkness
 * @date 2012-11-4 下午09:49:34 
 * @version V1.0   
 */
@Alias("BaseUIFacade")
public abstract class BaseUIFacade<T extends Entity> extends UIFacade {

	protected abstract <ES extends BaseRepository<T>> ES getEntityRepository();
	
	private Class<T> genericClass;
	
	/**
	 * @private
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:05:40 
	 * @version V1.0
	 */
	@SuppressWarnings("unchecked")
	protected Class<T> getGenericClass() {

		if (genericClass == null) {
			Type type = getClass().getGenericSuperclass();
			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			genericClass = (Class<T>) trueType;
		}
		return genericClass;
	}
	
	/**
	 * 新增窗体初始化数据，新建类似的时候会传入copyId
	 * @method init
	 * @param {String} id
	 * @param {String} copyId
	 * @author Darkness
	 * @date 2012-10-18 下午10:00:11
	 * @version V1.0
	 */
	@Priv
	public void init(String id, String copyId) {

		if (!StringUtil.isEmpty(copyId)) {
			id = copyId;
		}

		if (StringUtil.isEmpty(id)) {// new
			initEmpty();
			return;
		}

		// eidt or copy new
		T entity = getEntityRepository().findById(id);
		this.Response.putAll(ClassUtil.objectToMapx(entity));
//		this.Response.remove("ID");
		
		initAfter(entity);
	}
	
	/**
	 * 初始化之后会调用
	 * @method initAfter
	 * @param {Entity} entity
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:06:18 
	 * @version V1.0
	 */
	protected void initAfter(T entity) {
	}
	
	/**
	 * 新建的时候会调用
	 * @method initEmpty
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:06:53 
	 * @version V1.0
	 */
	protected void initEmpty() {
		try {
			this.Response.putAll(ClassUtil.objectToMapx(getGenericClass().newInstance()));
			this.Response.putAll(this.Request);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 绑定表格之前调用，返回为false则不执行绑定表格
	 * @method bindGridBefore
	 * @param {Mapx<String,Object>} map(查询参数集合)
	 * @return {boolean}
	 * @author Darkness
	 * @date 2013-2-1 下午02:07:15 
	 * @version V1.0
	 */
	protected boolean bindGridBefore(Mapx<String, Object> mapx) {
		return true;
	}
	
	/**
	 * 绑定表格之后调用
	 * @method bindGridBefore
	 * @param {IPageData} pageData(查询的数据集合)
	 * @param {Mapx<String,Object>} map(查询参数集合)
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:08:38 
	 * @version V1.0
	 */
	protected void bindGridAfter(IPageData pageData, Mapx<String, Object> params) {
	}
	
	/**
	 * @private
	 */
	@Deprecated
	protected void bindGridAfter(IPageData pageData) {
	}
	
	/**
	 * 获取系统中的所有元数据绑定到表格
	 * @method bindGrid
	 * @param {DataGridAction} dga(表格行为对象)
	 * @author Darkness
	 * @date 2012-10-18 下午10:17:51
	 * @version V1.0
	 */
	@Priv
	public void bindGrid(DataGridAction dga) {
		
		boolean needBind = bindGridBefore(dga.getParams());
		if(!needBind) {
			return;
		}

		Criteria criteria = getSession().createCriteria(getGenericClass());
		
		bindGridCriteria(criteria, dga.getParams());
		
		String sortString = dga.getParam(Constant.DataGridSortString);
		if(!StringUtil.isEmpty(sortString)) {
			String[] sortInfos = sortString.split(" ");
			for (int i=0; i<sortInfos.length;) {
				String sortField = sortInfos[i];
				String sortType = sortInfos[i+1];
				
				if(sortType.equals("ASC")) {
					criteria.addOrder(Order.asc(sortField));
				} else if(sortType.equals("DESC")) {
					criteria.addOrder(Order.desc(sortField));
				} 
				
				i += 2;
			}
		}
		
		if(!criteria.isExistOrder(Order.desc(BaseEntity.SortOrder))
				&& !criteria.isExistOrder(Order.asc(BaseEntity.SortOrder))) {
			criteria.addOrder(Order.desc(BaseEntity.SortOrder));
		}
		
		IPageInfo pageInfo = dga;
		IPageData pageData = criteria.page(pageInfo);

		if(TreeEntity.class.isAssignableFrom(getGenericClass())) {
			DataTable dt = (DataTable)pageData.getData();
			dt = DataTableUtil.sortTreeDataTable(dt, TreeEntity.INNER_CODE, TreeEntity.PARENT_INNER_CODE);
			pageData.setData(dt);
		}
		
		bindGridAfter(pageData);
		bindGridAfter(pageData, dga.getParams());
		
		dga.bindData(pageData);
	}
	
	/**
	 * 表格查询调用
	 * String simpleSearchValue = searchParams.getString("simpleSearchValue");

		criteria.add(
				Restrictions.or(
						Restrictions.like(Project.Name, simpleSearchValue),
						Restrictions.like(Project.DeviceName, simpleSearchValue)));
						
	 * @method bindGridCriteria
	 * @param {Criteria} criteria(实体查询规格)
	 * @param {Mapx<String,Object>} searchParams(查询参数集合)
	 * @author Darkness
	 * @date 2013-2-1 下午02:10:07 
	 * @version V1.0
	 */
	protected void bindGridCriteria(Criteria criteria, Mapx<String, Object> searchParams) {
	}
	
	/**
	 * 保存Entity，新建类似的时候会传入copyId
	 * @method save
	 * @param {Mapx<String,Object>} params(表单数据集合)
	 * @param {String} copyId(复制实体Id)
	 * @author Darkness
	 * @date 2012-10-18 下午09:47:15
	 * @version V1.0
	 */
	@Priv("Add||SimilarCreate")
	public void save(Mapx<String, Object> params, String copyId) {

		T entity = null;
		try {
			entity = ClassUtil.mapToObject(getGenericClass(), params);
		} catch (Exception e) {
			throw new ServiceException("请检查UIFacade的泛型是否指定...");
		} 
		
		boolean isNew = StringUtil.isEmpty(entity.getId());
		
		if(!beforeSave(entity, params, isNew)){
			return;
		}
		
		if(!StringUtil.isEmpty(copyId)) {
			getEntityRepository().saveWithCopy(entity, copyId);
		} else if (isNew) {
			getEntityRepository().save(entity);
		} else {
			
			T entityOld = getEntityRepository().findById(entity.getId());
			
			ClassUtil.applyToObject(entityOld, entity);
			
			beforeRepositoryUpdate(entityOld);
			
			getEntityRepository().update(entityOld);
			
			entity = entityOld;
		}
		
		Response.putAll(ClassUtil.objectToMapx(entity));
		Response.setSuccessMessage("保存成功");
		afterSave(entity, params, isNew);
	}
	
	protected void beforeRepositoryUpdate(T entityOld) {
	}

	/**
	 * @private
	 */
	@Deprecated
	protected boolean beforeSave(T entity, Mapx<String, Object> params) {
		return beforeSave(entity, params, true);
	}
	
	/**
	 * 保存之前调用，返回false则不执行保存
	 * @method beforeSave
	 * @param {Entity} entity(参数集合转化成的Entity)
	 * @param {Mapx<String,Object>} params(参数集合)
	 * @param {boolean} isNew(是否新增)
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:12:29 
	 * @version V1.0
	 */
	protected boolean beforeSave(T entity, Mapx<String, Object> params, boolean isNew) {
		return true;
	}
	
	/**
	 * @private
	 */
	@Deprecated
	protected void afterSave(T entity, Mapx<String, Object> params) {
		afterSave(entity, params, true);
	}
	
	/**
	 * 保存之后调用
	 * @method afterSave
	 * @param {Entity} entity(参数集合转化成的Entity)
	 * @param {Mapx<String,Object>} params(参数集合)
	 * @param {boolean} isNew(是否新增)
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:12:29 
	 * @version V1.0
	 */
	protected void afterSave(T entity, Mapx<String, Object> params, boolean isNew) {
	}

	/**
	 * 删除Entity，ids格式为："a,b,c..."
	 * @method delete
	 * @param {String} ids
	 * @author Darkness
	 * @date 2012-10-18 下午10:14:33
	 * @version V1.0
	 */
	@Priv("Delete")
	public void delete(String ids) {
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		// ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		//here ids :  "'1','2','3'"
		ids = "'" + ids + "'";
		
		beforeDelete(ids);
		
		Criteria criteria = getSession().createCriteria(getGenericClass());
		criteria.add(Restrictions.in(Entity.Id, ids));
		
		List<T> entities = criteria.findEntities();
		
		getEntityRepository().delete(ids);
		
		Response.setSuccessMessage("删除成功");
		afterDelete(entities, ids);
	}
	
	@Priv("Delete")
	public void enable(String ids) {
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		// ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		//here ids :  "'1','2','3'"
		ids = "'" + ids + "'";
		
		getEntityRepository().enable(ids);
	}
	
	@Priv("Delete")
	public void disable(String ids) {
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		// ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		//here ids :  "'1','2','3'"
		ids = "'" + ids + "'";
		
		getEntityRepository().disable(ids);
	}

	/**
	 * 删除之后调用
	 * @method afterDelete
	 * @param {List<Entity>} entities(删除的Entity集合)
	 * @param {String} ids(删除的ids)
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:12:29 
	 * @version V1.0
	 */
	protected void afterDelete(List<T> entities, String ids) {
	}

	/**
	 * 删除之前调用
	 * @method beforeDelete
	 * @param {String} ids(删除的ids)
	 * 
	 * @author Darkness
	 * @date 2013-2-1 下午02:12:29 
	 * @version V1.0
	 */
	protected void beforeDelete(String ids) {
	}
	
}

