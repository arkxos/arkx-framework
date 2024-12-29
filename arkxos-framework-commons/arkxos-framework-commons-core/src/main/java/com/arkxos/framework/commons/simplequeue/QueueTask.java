package com.arkxos.framework.commons.simplequeue;

import java.util.List;

public interface QueueTask extends ElementProcessor {

	String taskName();
	
	List<String> initElements();
	
}
