package com.arkxit.framework.boot;

import org.ark.framework.infrastructure.repositories.extend.EntitySaveExtendAction;

import com.rapidark.framework.NoUtil;
import com.rapidark.framework.annotation.EntityAnnotationManager;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.data.jdbc.Criteria;
import com.rapidark.framework.data.jdbc.Entity;
import com.rapidark.framework.data.jdbc.Restrictions;
import com.rapidark.framework.data.jdbc.Session;
import com.rapidark.framework.data.jdbc.SessionFactory;
import com.rapidark.framework.data.jdbc.TreeEntity;

/**
 * 
 * @author Darkness
 * @date 2013-3-14 下午06:32:00
 * @version V1.0
 */
public class HandleTreeEntity extends EntitySaveExtendAction {

	@Override
	protected void save(Entity entity) {
		if (TreeEntity.class.isAssignableFrom(entity.getClass())) {

			TreeEntity treeEntity = (TreeEntity) entity;

			if (StringUtil.isEmpty(treeEntity.getParentInnerCode()) || "0".equals(treeEntity.getParentInnerCode())) {
				treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName() + "InnerCode", 4));
				treeEntity.setParentInnerCode(treeEntity.getInnerCode());

				int sortOrder = getSession().createQuery("SELECT SORT_ORDER FROM " + EntityAnnotationManager.getTableName(treeEntity.getClass()) + " ORDER BY SORT_ORDER").executeInt();
				treeEntity.setSortOrder(sortOrder + 1L);
			} else {
				Criteria criteria = getSession().createCriteria(entity.getClass());
				criteria.add(Restrictions.eq(TreeEntity.InnerCode, treeEntity.getParentInnerCode()));
				TreeEntity parentEntity = criteria.findEntity();

				if (StringUtil.isEmpty(treeEntity.getInnerCode())) {
					treeEntity.setInnerCode(NoUtil.getMaxNo(treeEntity.getClass().getSimpleName() + "InnerCode", parentEntity.getInnerCode(), 4));
				}
				treeEntity.setParentInnerCode(parentEntity.getInnerCode());
				treeEntity.setTreeLevel(parentEntity.getTreeLevel() + 1L);


				long orderflag = getSession().createQuery("SELECT max(SORT_ORDER) FROM " + EntityAnnotationManager.getTableName(treeEntity.getClass()) + " WHERE INNER_CODE LIKE ? ORDER BY SORT_ORDER", parentEntity.getInnerCode() + "%").executeLong();

				treeEntity.setSortOrder(orderflag + 1L);

				getSession().createQuery("update " + EntityAnnotationManager.getTableName(entity.getClass()) + " set Is_Leaf='N' where Inner_Code=?", parentEntity.getInnerCode()).executeNoQuery();
				getSession().createQuery("update " + EntityAnnotationManager.getTableName(entity.getClass()) + " set sort_order=sort_order+1 where sort_order>?", orderflag).executeNoQuery();
			}
		}
	}
	
	private Session getSession() {
		return SessionFactory.currentSession();
	}

}
