package com.rapidark.framework.common.queueexecutor;

import java.util.List;

/**
 *  
 * @author Darkness
 * @date 2016年11月17日 下午4:40:12
 * @version V1.0
 */
public interface Task<T> {
	
	String getId();

	List<T> preparedTodoList(String taskId);

	boolean executeTodo(String taskId, T todo);

}
