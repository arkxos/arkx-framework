package com.rapidark.framework.util.task;

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
