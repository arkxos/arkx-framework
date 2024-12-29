package com.rapidark.framework.commons.simplequeue;

import java.util.List;

public interface QueueTask extends ElementProcessor {

	String taskName();
	
	List<String> initElements();
	
}
