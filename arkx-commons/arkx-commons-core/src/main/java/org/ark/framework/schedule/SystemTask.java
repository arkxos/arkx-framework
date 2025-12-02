package org.ark.framework.schedule;

/**
 * @class org.ark.framework.schedule.SystemTask
 *
 * @author Darkness
 * @date 2013-1-31 下午12:21:53
 * @version V1.0
 */
public abstract class SystemTask extends AbstractTask {

    protected boolean isRunning = false;
    protected String cronExpression;

    public String getType() {
        return "General";
    }

    public abstract void execute();

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public String setCronExpression(String expr) {
        return this.cronExpression = expr;
    }
}
