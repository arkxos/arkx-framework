package io.arkx.framework.commons.queueexecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Darkness
 * @date 2016年11月11日 下午5:20:19
 * @version V1.0
 */
public class QueueInfo<T> {

	private String id;

	private QueueStatus status = QueueStatus.New;

	private List<Element<T>> todoList = new ArrayList<>();

	public QueueInfo() {
	}

	public String getId() {
		return id;
	}

	public List<Element<T>> getTodoList() {
		return todoList;
	}

	public void addTodo(Element<T> todo) {
		todoList.add(todo);
		// todo.setTask(this);
	}

	public QueueStatus getStatus() {
		return status;
	}

	public void setStatus(QueueStatus status) {
		this.status = status;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTodoList(List<Element<T>> todoList) {
		this.todoList = todoList;
	}

	public boolean isFinished() {
		if (status == QueueStatus.New) {
			return false;
		}

		if (status == QueueStatus.Finished) {
			return true;
		}
		for (Element<T> todo : todoList) {
			if (!todo.isFinished()) {
				return false;
			}
		}
		status = QueueStatus.Finished;
		return false;
	}

	@Override
	public String toString() {
		int finishedCount = 0;
		for (Element<T> todo : todoList) {
			if (todo.isFinished()) {
				finishedCount++;
			}
		}
		return "task-" + getId() + "[" + status + "], allCount: " + todoList.size() + ", finished count: "
				+ finishedCount;
	}

	public boolean inited() {
		return status != QueueStatus.New;
	}

	public void changeStatusInited() {
		status = QueueStatus.Inited;
	}

}
