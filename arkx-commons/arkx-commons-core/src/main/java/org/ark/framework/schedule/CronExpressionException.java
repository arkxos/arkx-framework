package org.ark.framework.schedule;

/**
 * @class org.ark.framework.schedule.CronExpressionException
 *
 * @author Darkness
 * @date 2013-1-31 下午12:21:08
 * @version V1.0
 */
public class CronExpressionException extends Exception {
    private static final long serialVersionUID = 1L;

    public CronExpressionException(String message) {
        super(message);
    }
}
