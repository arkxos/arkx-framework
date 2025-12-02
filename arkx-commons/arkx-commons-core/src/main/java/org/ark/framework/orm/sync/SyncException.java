package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:56
 * @since 1.0
 */

/**
 * 同步异常 表示在同步过程中发生的异常
 */
public class SyncException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 构造函数
     *
     * @param message
     *            错误消息
     */
    public SyncException(String message) {
        super(message);
        this.errorCode = "SYNC_ERROR";
    }

    /**
     * 构造函数
     *
     * @param message
     *            错误消息
     * @param cause
     *            原因异常
     */
    public SyncException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SYNC_ERROR";
    }

    /**
     * 构造函数
     *
     * @param message
     *            错误消息
     * @param errorCode
     *            错误码
     */
    public SyncException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message
     *            错误消息
     * @param cause
     *            原因异常
     * @param errorCode
     *            错误码
     */
    public SyncException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
}
