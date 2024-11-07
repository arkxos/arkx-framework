package com.rapidark.framework.util.task;

public class HandleSheetDataTask extends TreeTask {

    private String data;

    public HandleSheetDataTask(String data) {
        super("", "");
    }

    @Override
    public void run(TaskContext ctx) {
        System.out.println("[HandleSheetDataTask]" + data);
    }
}
