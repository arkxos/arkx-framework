package com.rapidark.framework.data.lightning.filter;

import com.google.common.base.Predicate;

public class StringEqualFilter implements Predicate<String> {
	
	private String target;
	
	public StringEqualFilter(String target) {
		this.target = target;
	}

	@Override
	public boolean apply(String input) {
		return target.equals(input);
	}

}
