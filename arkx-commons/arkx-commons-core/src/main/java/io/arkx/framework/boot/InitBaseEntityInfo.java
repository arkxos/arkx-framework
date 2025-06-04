package io.arkx.framework.boot;

import java.util.Date;

import org.ark.framework.infrastructure.repositories.extend.EntitySaveExtendAction;

import io.arkx.framework.Account;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.jdbc.BaseEntity;
import io.arkx.framework.data.jdbc.Entity;


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
