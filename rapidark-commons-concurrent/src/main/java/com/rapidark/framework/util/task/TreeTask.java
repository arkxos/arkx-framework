package com.rapidark.framework.util.task;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeTask extends BaseTask implements TaskRunner {

    private List<TreeTask> children = new ArrayList<>();

    protected TreeTask(String type, String id) {
        super(null);
    }

    public void addChild(TreeTask child) {
        children.add(child);
    }

    @Override
    public TaskRunner getRunner() {
        return this;
    }

}
