package io.arkx.framework.commons.collection;

import java.util.*;

/**
 * @class org.ark.framework.collection.Enumerator
 * 迭代器
 * 
 * @author Darkness
 * @date 2012-8-6 下午9:58:35
 * @version V1.0
 */
public class Enumerator<E> implements Enumeration<E> {
	private Iterator<E> iterator;

	public Enumerator(Collection<E> collection) {
		this(collection.iterator());
	}

	public Enumerator(Collection<E> collection, boolean clone) {
		this(collection.iterator(), clone);
	}

	public Enumerator(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	public Enumerator(Iterator<E> iterator, boolean clone) {
		if (!clone) {
			this.iterator = iterator;
		} else {
			List list = new ArrayList();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}
			this.iterator = list.iterator();
		}
	}

	public boolean hasMoreElements() {
		return this.iterator.hasNext();
	}

	public E nextElement() throws NoSuchElementException {
		return this.iterator.next();
	}
}