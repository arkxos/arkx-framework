package com.rapidark.framework.util.task;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class TreeTask extends AbstractTask implements TaskRunner {

    private boolean isRoot = true;

    private List<TreeTask> children = new ArrayList<>();

    protected TreeTask(String type, String id) {
        super("TreeTask", "");
    }

    public void addChild(TreeTask child) {
        child.setRoot(false);
        children.add(child);
    }

    public TaskRunner getRunner() {
        return this;
    }

}
