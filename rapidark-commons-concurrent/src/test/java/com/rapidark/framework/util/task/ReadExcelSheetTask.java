package com.rapidark.framework.util.task;

public class ReadExcelSheetTask extends TreeTask {

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
