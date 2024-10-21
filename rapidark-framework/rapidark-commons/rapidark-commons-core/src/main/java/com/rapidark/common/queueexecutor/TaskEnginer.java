package com.rapidark.common.queueexecutor;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.rapidark.common.utils.FileUtil;
import com.rapidark.common.queueexecutor.processor.ElementProcessor;
import com.rapidark.common.utils.Handler;

/**
 *  
 * @author Darkness
 * @date 2016年11月11日 下午5:23:12
 * @version V1.0
 */
public class TaskEnginer {

	private String dataRootPath;
	private MultiThreadedQueueExecutor executor;
	private Map<String, QueueInfo> tasks = new LinkedHashMap<>();
	private ScheduledExecutorService flushThreadPool;
	private ScheduledExecutorService watchThreadPool;
	
	public TaskEnginer(String dataRootPath) {
		this.dataRootPath = dataRootPath;
		FileUtil.mkdir(dataRootPath);
		init();
	}
	
	@SuppressWarnings("rawtypes")
	private void init() {
		System.out.println("init task enginer");
		executor = new MultiThreadedQueueExecutor("task-enginer", "任务引擎", new ElementProcessor<Object>() {
			
			@Override
			public void process(Element<Object> element, MultiThreadedQueueExecutor<Object> executor) {
				Object todo = element.getSource();
				try {
					Task taskProcessor = null;// findProcessor(todo.getTask());
					boolean isSuccess = taskProcessor.executeTodo(taskProcessor.getId(), todo);
					if(isSuccess) {
//						todo.finished();
					} else {
//						todo.error();
					}
				} catch (Exception e) {
					e.printStackTrace();
//					todo.error();
				}
				
//				if(todo.isError()) {
//					executor.addElement(new Element<Object>(todo.getId() + "-" + todo.getStatus(), todo));// 重试
//				}
			}
			
		})
		.thread(5)
		.onFinish(() -> {
			onFinished();
		});
	}
	
	private Handler finishedHandler;
	
	private Task findProcessor(QueueInfo task) {
		Task taskProcessor = taskProcessors.get(task.getId());
		return taskProcessor;
	}
	
	private void onFinished() {
		flush();
		flushThreadPool.shutdown();
		if(watchThreadPool != null) {
			watchThreadPool.shutdown();
		}
		if(finishedHandler != null ) {
			finishedHandler.execute();
		}
	}
	
	public void onFinished(Handler finishedHandler) {
		this.finishedHandler = finishedHandler;
	}
	
	@SuppressWarnings("rawtypes")
	private void startTask(String taskId) {
		QueueInfo task = tasks.get(taskId);
		if(task.isFinished()) {
			return;
		}
		if(!task.inited()) {
			Task taskProcessor = taskProcessors.get(taskId);
			List<Object> todos = taskProcessor.preparedTodoList(taskId);
			for (Object todo : todos) {
//				task.addTodo(new Todo(DigestUtils.md5Hex(todo.toString()), todo));
			}
			task.changeStatusInited();
		}
		List<Object> todoList = task.getTodoList();
		for (Object todo : todoList) {
//			if(!todo.isFinished()) {
//				executor.addElement(new Element<Object>(todo.getId(), todo));
//			}
		}
		executor.startAsync();
	}
	
	public Map<String, QueueInfo> getTasks() {
		return tasks;
	}

	private Map<String, Task> taskProcessors = new HashMap<>();
	
	public boolean addTask(Task taskProcessor) {
		if(tasks.containsKey(taskProcessor.getId())) {
			return false;
		}
		QueueInfo task = new QueueInfo<>();
		task.setId(taskProcessor.getId());
		tasks.put(task.getId(), task);
		taskProcessors.put(taskProcessor.getId(), taskProcessor);
		return true;
	}
	
	private void addTask(QueueInfo task) {
		tasks.put(task.getId(), task);
	}
	
	public void deleteTask(String taskId) {
		tasks.remove(taskId);
		FileUtil.delete(taskInfoFilePath(taskId));
	}

	public boolean containsTask(String id) {
		return tasks.containsKey(id);
	}
	
	public QueueInfo getTask(String id) {
		return tasks.get(id);
	}

	public void start() {
		initFlushThread();
		initTasks();
	}
	
	public void startAndWatch() {
		initWatchThread();
		start();
	}
	
	private void initTasks() {
		File folder = new File(dataRootPath);
		String[] taskFiles = folder.list();
		if(taskFiles != null) {
			for (String taskFile : taskFiles) {
				if (!taskFile.endsWith(".json")) {
					continue;
				}
				QueueInfo task = JSON.parseObject(FileUtil.readText(dataRootPath + File.separator + taskFile), QueueInfo.class);
				addTask(task);
			}
		}
	}
	
	public void run() {
		boolean allFinished = true;
		for (QueueInfo task : tasks.values()) {
			if(!task.isFinished()) {
				allFinished = false;
				startTask(task.getId());
			}
		}
		if(allFinished) {
			onFinished();
		}
	}
	
	private void initFlushThread() {
		flushThreadPool = Executors.newScheduledThreadPool(1);
    	flushThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                flush();
            }

        }, 5, 5, TimeUnit.SECONDS);
    }
	
	private void initWatchThread() {
		watchThreadPool = Executors.newScheduledThreadPool(1);
    	watchThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        }, 5, 5, TimeUnit.SECONDS);
	}
	
	private void watch() {
		for (QueueInfo task : tasks.values()) {
			System.out.println(LocalDateTime.now() + "  " + task);
		}
	}
	
	private void flush() {
		for (QueueInfo task : tasks.values()) {
			FileUtil.writeText(taskInfoFilePath(task.getId()), JSON.toJSONString(task, true));
		}
	}
	
	private String taskInfoFilePath(String taskId) {
		return dataRootPath + File.separator + taskId + ".json";
	}
	
}
