package com.rapidark.framework.util.task;

import com.rapidark.framework.util.task.util.RandomUtil;

public class ReadExcelTask extends TreeTask {

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
