package com.arkxos.framework.boot;

import java.util.Date;

import org.ark.framework.infrastructure.repositories.extend.EntitySaveExtendAction;

import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.data.jdbc.BaseEntity;
import com.arkxos.framework.data.jdbc.Entity;
import com.rapidark.framework.Account;


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
