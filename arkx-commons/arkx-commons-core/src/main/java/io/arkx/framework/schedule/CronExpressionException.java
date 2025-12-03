package io.arkx.framework.schedule;

/**
 * Cron表达式异常
 *
 */
public class CronExpressionException extends Exception {

    private static final long serialVersionUID = 1L;

    public CronExpressionException(String message) {
        super(message);
    }

}
