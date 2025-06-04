package io.arkx.framework.util.task;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import ch.qos.logback.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class TreeTask extends AbstractTask implements TaskRunner {

    private boolean isRoot = true;
    private int level = 1;
    private TreeTask parent;
    private List<TreeTask> children = new ArrayList<>();
    private double totalPercent;
    private final AtomicInteger childCount = new AtomicInteger();
    private final AtomicInteger childFinishCount = new AtomicInteger();

    protected TreeTask(String type, String id) {
        super(type, id);
    }

    // 默认耗时在子任务上，父任务只是负责拆分子任务
    // 如果任务自身耗时较大，可调整此参数
    public int getSelfTaskPercent() {
        return 1;
    }

    public int getChildrenTaskPercent() {
        return 100 - getSelfTaskPercent();
    }

    public void addChild(TreeTask child) {
        childCount.incrementAndGet();

        child.setRoot(false);
        child.setLevel(level + 1);

        child.setParent(this);
        children.add(child);
    }

    public TaskRunner getRunner() {
        return this;
    }

    public TreeTask findNeedExecuteTask() {
        if(this.isWaittingForExecute()) {
            return this;
        }

        if(this.isFinished()) {
            return null;
        }

        if(this.getStatus() == TaskStatus.RUNNING) {
            return null;
        }

        for (TreeTask child : children) {
            TreeTask needExecuteTask = child.findNeedExecuteTask();
            if (needExecuteTask != null) {
                return needExecuteTask;
            }
        }
        return null;
    }

//    @Override
//    public boolean isFinished() {
////        if(!super.isFinished()) {
////            return false;
////        }
////        for (TreeTask child : this.children) {
////            if(!child.isFinished()) {
////                return false;
////            }
////        }
////        return true;
//        return this.isFinished()
//    }

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
        os.print(toTreeString());
    }

    public String toTreeString() {
        StringBuilder sb = new StringBuilder();
        traversePreOrder(sb, "", "", this, true);
        return sb.toString();
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
        return "["+getGlobalExecuteOrder()+"][" + this.getProgressPercent() + "%]["+this.getCost()+"]" + this.getClass().getSimpleName() + "-" + this.getStatus() + "-" + this.getId();
    }

    @Override
    public void triggerCompleted() {
        super.triggerCompleted();

        for (TreeTask child : children) {
            child.triggerCompleted();
        }
    }

    @Override
    public void finish() {
        if(this.children.isEmpty()) {
            this.realFinished();
        }
    }

    public void onChildFinish(TreeTask finishedChildTask) {
        this.childFinishCount.incrementAndGet();
        System.out.println("children finish,task id: " + getId() + ",finish child id: " + finishedChildTask.getId() + "," + this.childCount.get() + "," + this.childFinishCount.get());
        for (TreeTask child : this.children) {
            if(!child.isFinished()) {
                return;
            }
        }

        this.realFinished();
    }

    private void realFinished() {
        this.setEndTime(System.nanoTime());
        this.setFinished(true);
        this.setProgressPercent(100D);

        this.triggerCompleted();

        this.parent.onChildFinish(this);
    }

    private void triggerPercentListener() {
        if(this.getProgress() != null) {
            this.getProgress().call(this, this.totalPercent);
        }
    }

    @Override
    public void setProgressPercent(double progressPercent) {
        super.setProgressPercent(progressPercent);

        this.totalPercent = new BigDecimal(getSelfTaskPercent()+"").multiply(new BigDecimal(progressPercent+"")).setScale(2, RoundingMode.HALF_UP).doubleValue();
        triggerPercentListener();

        this.parent.onChildProgressPercent();
    }

    public void onChildProgressPercent() {
        int childrenSize = children.size();
        List<TreeTask> notFinishedChildrens = this.children.stream().filter(item->!item.isFinished()).toList();
        long notFinishCount = notFinishedChildrens.size();
        if(notFinishCount == 0) {
            this.setTotalPercent(100D);
        } else {
            // 默认自身执行占比为 1%，子任务占比为 99%
            BigDecimal preChildPercent = div(getChildrenTaskPercent(), childrenSize);

            BigDecimal totalPercent = new BigDecimal("0");
            for (TreeTask notFinishedTask : notFinishedChildrens) {
                double myProcessPercent = notFinishedTask.getTotalPercent();

                BigDecimal currentSheetPercent = new BigDecimal("" + myProcessPercent).multiply(preChildPercent);
                totalPercent = totalPercent.add(currentSheetPercent);
            }
            long finishdCount = childrenSize - notFinishCount;
            totalPercent = totalPercent.add(preChildPercent.multiply(new BigDecimal(finishdCount))).setScale(2, RoundingMode.HALF_DOWN);
            // 加上自身耗时占比
            totalPercent = totalPercent.add(new BigDecimal(getSelfTaskPercent()));
            double caculatedPercent = totalPercent.doubleValue();
            // 理论上存在未完成子任务，总进度不会达到 100%，不排除子任务过多的精度问题
            if(caculatedPercent > 100D) {
                caculatedPercent = 99D;
            }
            this.setTotalPercent(caculatedPercent);
        }

        triggerPercentListener();
    }

    /**
     * 求商运算(a/b)
     * <p>
     * 默认采用10位浮点精度计算 (用来解决浮点运算时精度丢失,不理想的情况)
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal div(Number a, Number b) {
        return getBigDecimal(a).divide(getBigDecimal(b), 4, RoundingMode.HALF_UP);
    }

    /**
     * 将数字或数字字符串封装成BigDecimal
     *
     * @param number
     * @return
     */
    public static BigDecimal getBigDecimal(Object number) {
        if (number == null) {
            return new BigDecimal("0");
        }
        if (number instanceof String && StringUtil.isNullOrEmpty(number + "")) {
            return new BigDecimal("0");
        }
        try {
            if (number instanceof BigDecimal decimal) {
                return decimal;
            } else if (number instanceof Double || number instanceof Float) {
                return new BigDecimal(number.toString());
            } else if (number instanceof Integer integer) {
                return new BigDecimal(integer);
            } else if (number instanceof Long long1) {
                return new BigDecimal(long1);
            } else {
                String val = number.toString();
                if(isNumber(val)) {
                    return new BigDecimal(val);
                }
                return new BigDecimal("0");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new BigDecimal("0");
        }
    }

    private static Pattern numberPatter = Pattern.compile("^[\\d\\.E\\,\\+\\-]*$");

    /**
     * 是否是数字
     */
    public static boolean isNumber(String str) {
        if (StringUtil.isNullOrEmpty(str)) {
            return false;
        }
        return numberPatter.matcher(str).find();
    }

}
