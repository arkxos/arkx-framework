package io.arkx.framework.commons.queueexecutor.scheduler.component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.commons.queueexecutor.Element;

/**
 * @author Darkness
 * @date 2015-1-9 下午10:46:08
 * @version V1.0
 * @since infinity 1.0
 */
public class HashSetDuplicateRemover<T> implements DuplicateRemover<T> {

	private Set<String> elementIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	@Override
	public boolean isDuplicate(Element<T> element) {
		return !elementIds.add(element.getId());
	}

	@Override
	public void resetDuplicateCheck() {
		elementIds.clear();
	}

	@Override
	public int getTotalElementsCount() {
		return elementIds.size();
	}

}
