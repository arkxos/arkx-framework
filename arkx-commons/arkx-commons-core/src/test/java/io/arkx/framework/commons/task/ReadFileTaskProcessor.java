package io.arkx.framework.commons.task;

import io.arkx.framework.commons.queueexecutor.Task;
import io.arkx.framework.commons.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  
 * @author Darkness
 * @date 2016年11月11日 下午5:35:51
 * @version V1.0
 */
public class ReadFileTaskProcessor implements Task<String> {
	
	public static final String ID = "read-file";

	private String id;
	
	public ReadFileTaskProcessor(String id) {
		this.id = ID + id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public List<String> preparedTodoList(String taskId) {
		List<String> result = new ArrayList<>();
		
		String folderPath = "D:\\ice-queen-data\\download\\raw-eod-quote-neteasy\\raw_eod_quote__2016-4_2016-11-14_snapshot\\datas";
		File file = new File(folderPath);
		String[] fileNames = file.list();
		
		for (String fileName : fileNames) {
			result.add(folderPath + File.separator + fileName);
		}
		
		return result;
	}
	
	@Override
	public boolean executeTodo(String taskId, String todo) {
//		System.out.println("do todo:" + todo.getElement());
		FileUtil.readText(todo);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
}
