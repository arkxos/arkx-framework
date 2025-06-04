package com.arkxos.framework.extend;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.CacheMapx;
import io.arkx.framework.commons.collection.ReadOnlyList;
import io.arkx.framework.core.FrameworkException;

/**
 * 扩展服务抽象基类
 * @author Darkness
 * @date 2012-8-7 下午9:21:09 
 * @version V1.0
 */
public class AbstractExtendService<T extends IExtendItem> implements IExtendService<T> {
	protected CacheMapx<String, T> itemMap = new CacheMapx<>();

	protected List<T> itemList = new ReadOnlyList<>(new ArrayList<>());

	/**
	 * 查找扩展服务的实例
	 */
	@SuppressWarnings("unchecked")
	protected static <S extends IExtendService<?>> S findInstance(Class<S> clazz) {
		if (clazz == null) {
			throw new FrameworkException("ExtendService class can't be empty!");
		}
		IExtendService<?> service = ExtendManager.findExtendServiceByClass(clazz.getName());
		if (service == null) {
			throw new FrameworkException("ExtendService not found,class is " + clazz.getName());
		}
//		@SuppressWarnings("unchecked")
//		S service = (S) config.getInstance();
		return (S)service;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void register(IExtendItem item) {
		itemMap.put(item.getExtendItemID(), (T) item);
		prepareItemList();
	}

	@Override
	public T get(String id) {
		if (id == null) {
			return null;
		}
		return itemMap.get(id);
	}

	@Override
	public T remove(String id) {
		T ret = itemMap.remove(id);
		prepareItemList();
		return ret;
	}

	protected void prepareItemList() {
		itemList = new ReadOnlyList<>(itemMap.values());
	}

	/**
	 * 注意：有可能返回null
	 */
	@Override
	public List<T> getAll() {
		return itemList;
	}

	public int size() {
		return itemList.size();
	}

	@Override
	public void destory() {
		itemMap.clear();
		itemMap = null;
		itemList = null;
	}

}
