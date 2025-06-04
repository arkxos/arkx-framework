package com.arkxos.framework.commons.util;

/**
 * An exception which is raised when there is a problem parsing a moeny
 * field.
 *
 * @author Andrew Leppard
 */
public class MoneyFormatException extends Throwable {

    // Reason for exception
    private String reason = null;

    /**
     * Create a new money format exception.
     *
     * @param reason for the exception.
     */
    public MoneyFormatException(String reason) {
        this.reason = reason;
    }

    /**
     * Return the reason this exception was raised.
     *
     * @return the reason why the string isn't a valid symbol
     */
    public String getReason() {
        return reason;
    }

    /**
     * Convert the exception to a string
     *
     * @return	string version of the exception
     */
    public String toString() {
	return getReason();
    }
}
