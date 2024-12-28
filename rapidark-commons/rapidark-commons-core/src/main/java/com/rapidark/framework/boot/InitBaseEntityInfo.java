package com.rapidark.framework.boot;

import java.util.Date;

import org.ark.framework.infrastructure.repositories.extend.EntitySaveExtendAction;

import com.rapidark.framework.Account;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.data.jdbc.BaseEntity;
import com.rapidark.framework.data.jdbc.Entity;


/**   
 * 
 * @author Darkness
 * @date 2013-3-14 下午04:06:49 
 * @version V1.0   
 */
public class InitBaseEntityInfo extends EntitySaveExtendAction {

	@Override
	protected void save(Entity entity) {
		if(BaseEntity.class.isAssignableFrom(entity.getClass())) {
			BaseEntity baseEntity = (BaseEntity)entity;
			baseEntity.setCreateTime(new Date());
			baseEntity.setUpdateTime(baseEntity.getCreateTime());
			
			if(!StringUtil.isEmpty(Account.getId())) {
				baseEntity.setCreatorId(Account.getId());
				baseEntity.setUpdatorId(Account.getId());
			}
		}
	}

}
