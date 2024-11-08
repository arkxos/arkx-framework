package com.rapidark.framework.util.task;

import com.rapidark.framework.util.task.util.RandomUtil;

import java.util.List;

public class AnalyzeFilesTask extends TreeTask {

    private List<String> files;

    protected AnalyzeFilesTask(List<String> files) {
        super("", "AnalyzeFilesTask");
        this.files = files;
    }


    @Override
    public void run(TaskContext ctx) {
        for (String file : this.files) {
            this.addChild(new ReadExcelTask(file));
        }
    }
    
}

class ReadExcelTask extends TreeTask {

    private String file;

    public ReadExcelTask(String file) {
        super("ReadExcelTask", "");
        this.file = file;
    }

    @Override
    public void run(TaskContext ctx) {
        boolean isBig = RandomUtil.randomInt(2) == 1;
        int sheetCount = RandomUtil.randomInt(6);

        if(isBig) {
            for (int i = 0; i < sheetCount; i++) {
                this.addChild(new ReadExcelSheetTask(file, i));
            }
        } else {
            for (int i = 0; i < sheetCount; i++) {
                String data = "data from 【"+i+"】" + file;
                this.addChild(new HandleSheetDataTask(data));
            }
        }
    }

}

class ReadExcelSheetTask extends TreeTask {

    private String filePath;
    private int sheetNo;

    protected ReadExcelSheetTask(String filePath, int sheetNo) {
        super("ReadExcelSheetTask", "");
        this.filePath = filePath;
        this.sheetNo = sheetNo;
    }

    @Override
    public void run(TaskContext ctx) {
        System.out.println("ReadExcelSheet: [" + sheetNo + "]" + filePath);
    }

}

class HandleSheetDataTask extends TreeTask {

    private String data;

    public HandleSheetDataTask(String data) {
        super("", "");
    }

    @Override
    public void run(TaskContext ctx) {
        System.out.println("[HandleSheetDataTask]" + data);
    }
}
