package org.ark.framework.jaf;

import com.arkxit.framework.boot.BaseUIFacade;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.exception.ServiceException;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.data.jdbc.BaseEntity;
import com.rapidark.framework.data.jdbc.Criteria;
import com.rapidark.framework.data.jdbc.Entity;
import com.rapidark.framework.data.jdbc.Restrictions;
import com.rapidark.framework.i18n.LangMapping;

/**   
 * @class org.ark.framework.jaf.EnableDisableUIFacade
 * @extends org.ark.framework.jaf.BaseUIFacade
 * 禁用/启用方式的Entity UI Facade基类
 * @author Darkness
 * @date 2013-2-1 下午01:39:25 
 * @version V1.0   
 */
public abstract class EnableDisableUIFacade<T extends Entity> extends BaseUIFacade<T> {

	/**
	 * @private
	 */
	@Override
	protected void bindGridCriteria(Criteria criteria, Mapx<String, Object> searchParams) {
		super.bindGridCriteria(criteria, searchParams);
		
		criteria.add(Restrictions.or(Restrictions.eq(BaseEntity.UseFlag, "Y"), Restrictions.eq(BaseEntity.UseFlag, "N")));
	}
	
	/**
	 * 启用Entity，ids格式为："a,b,c..."
	 * @method enable
	 * @param {String} ids 
	 * @author Darkness
	 * @date 2012-10-18 下午10:14:33
	 * @version V1.0
	 */
	public void enable(String ids) {
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		//ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		// here ids :  "'1','2','3'"
		ids = "'" + ids + "'";
		
		getEntityRepository().enable(ids);
	}
	
	/**
	 * 禁用Entity，ids格式为："a,b,c..."
	 * @method disable
	 * @param {String} ids 
	 * @author Darkness
	 * @date 2012-10-18 下午10:14:33
	 * @version V1.0
	 */
	public void disable(String ids) {
		if (ObjectUtil.empty(ids)) {
			throw new ServiceException(LangMapping.get("Common.InvalidID"));
		}
		
		// ids: "1, 2, 3",after replace: ids = "1','2','3";
		ids = ids.replaceAll(",", "','");
		
		// here ids :  "'1','2','3'"
		ids = "'" + ids + "'";
		
		getEntityRepository().disable(ids);
	}
}
