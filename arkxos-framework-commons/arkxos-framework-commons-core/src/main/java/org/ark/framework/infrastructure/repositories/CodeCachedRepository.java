package org.ark.framework.infrastructure.repositories;

import com.rapidark.framework.data.jdbc.CodeEntity;

/**   
 * @class org.ark.framework.infrastructure.repositories.CodeCachedRepository
 * 带编码的缓存仓储
 * @author Darkness
 * @date 2013-3-17 下午07:56:53 
 * @version V1.0   
 */
public class CodeCachedRepository<T extends CodeEntity> extends BaseRepository<T> {
	
	/**
	 * 根据code查找对象
	 * 
	 * @author Darkness
	 * @date 2013-3-17 下午08:03:33 
	 * @version V1.0
	 */
	public T findByCode(String code) {
		for (T entity : findAllAsc()) {
			if (entity.getCode().equals(code)) {
				return entity;
			}
		}
		return null;
	}
	
	/**
	 * 判断编码是否存在
	 * 
	 * @author Darkness
	 * @date 2013-3-17 下午08:05:56 
	 * @version V1.0
	 */
	public boolean isCodeExist(String code) {
		for (T entity : findAll()) {
			if (entity.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}
}
