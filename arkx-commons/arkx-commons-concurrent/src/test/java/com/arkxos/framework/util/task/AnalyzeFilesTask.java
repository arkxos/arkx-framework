package com.arkxos.framework.util.task;

import java.util.List;

import com.arkxos.framework.util.task.util.RandomUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnalyzeFilesTask extends TreeTask {

    private List<String> files;

    protected AnalyzeFilesTask(List<String> files) {
        super("", "AnalyzeFilesTask");
        this.files = files;
    }

    @Override
    public void run(TaskContext ctx) {
        int i = 1;
        for (String file : this.files) {
            this.addChild(new ReadExcelTask(file));
        }
    }
    
}

class ReadExcelTask extends TreeTask {

    private String file;

    public ReadExcelTask(String file) {
        super("ReadExcelTask", file);
        this.file = file;
    }

    @Override
    public void run(TaskContext ctx) {
        boolean isBig = RandomUtil.randomInt(1) == 1;
        int sheetCount = RandomUtil.randomInt(1, 5);

        if(isBig) {
            for (int i = 0; i < sheetCount; i++) {
                this.addChild(new ReadExcelSheetTask(file, i));
            }
        } else {
            for (int i = 0; i < sheetCount; i++) {
                String data = "data from 【"+i+"】" + file;
                this.addChild(new HandleSheetDataTask(file, i+1, data));
            }
        }
    }

}

class ReadExcelSheetTask extends TreeTask {

    private String filePath;
    private int sheetNo;

    protected ReadExcelSheetTask(String filePath, int sheetNo) {
        super("ReadExcelSheetTask", filePath + "["+sheetNo+"]");
        this.filePath = filePath;
        this.sheetNo = sheetNo;
    }

    @Override
    public void run(TaskContext ctx) {
        String data = "data from 【"+sheetNo+"】" + filePath;
        this.addChild(new HandleSheetDataTask(filePath, sheetNo, data));
        System.out.println("ReadExcelSheet: [" + sheetNo + "]" + filePath);
    }

}

class HandleSheetDataTask extends TreeTask {

    private String data;

    public HandleSheetDataTask(String filePath, int sheetNo, String data) {
        super("", filePath + "["+sheetNo+"]");
    }

    @Override
    public void run(TaskContext ctx) {
        System.out.println("[HandleSheetDataTask]" + data);
    }
}
