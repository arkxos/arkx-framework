package com.rapidark.framework.util.task;

import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class TreeTask extends AbstractTask implements TaskRunner {

    private boolean isRoot = true;
    private int level = 1;
    private List<TreeTask> children = new CopyOnWriteArrayList<>();

    protected TreeTask(String type, String id) {
        super(type, id);
    }

    public void addChild(TreeTask child) {
        child.setRoot(false);
        child.setLevel(level + 1);
        children.add(child);
    }

    public TaskRunner getRunner() {
        return this;
    }

    public Task findNeedExecuteTask() {
        if(this.getStatus() == TaskStatus.INIT) {
            return this;
        }

        for (TreeTask child : children) {
            Task needExecuteTask = child.findNeedExecuteTask();
            if (needExecuteTask != null) {
                return needExecuteTask;
            }
        }
        return null;
    }

    @Override
    public boolean isFinished() {
        if(!super.isFinished()) {
            return false;
        }
        for (TreeTask child : this.children) {
            if(!child.isFinished()) {
                return false;
            }
        }
        return true;
    }

    public void print() {
        print(System.out);
    }

//    public String toTreeString(TreeTask treeTask, boolean parentIsLastChild) {
//        // "│  " "└──" "├──"
//        String result = "";
//        String content = treeTask.toString();
//        if(treeTask.isRoot) {
//            result += "======================================================\n";
//            result += content + "\n";
//        } else {
//            for (int i = 1; i < treeTask.level; i++) {
//                result += " ";
//            }
//            result += "└──";
//            result += content + "\n";
//        }
//        for (TreeTask child : treeTask.children) {
//            result += child.toTreeString();
//        }
//        return result;
//    }

    public void print(PrintStream os) {
        StringBuilder sb = new StringBuilder();
        traversePreOrder(sb, "", "", this, true);
        os.print(sb.toString());
    }

    public void traversePreOrder(StringBuilder sb, String padding, String pointer, TreeTask node, boolean isLastNode) {
        sb.append(padding);
        sb.append(pointer);
        sb.append(node.toString());
        sb.append("\n");

        StringBuilder paddingBuilder = new StringBuilder(padding);
        if(isLastNode) {
            paddingBuilder.append("   ");
        } else {
            paddingBuilder.append("│  ");
        }

        String paddingForBoth = paddingBuilder.toString();
        String pointerForRight = "└──";

        for (int i = 0; i < node.getChildren().size(); i++) {
            TreeTask child = node.getChildren().get(i);

            boolean isLastChild = i == node.getChildren().size() - 1;
            String pointerForLeft = isLastChild ? "└──" : "├──";
            traversePreOrder(sb, paddingForBoth, pointerForLeft, child, isLastChild);
        }
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "-" + this.getStatus() + "-" + this.getId();
    }
}
