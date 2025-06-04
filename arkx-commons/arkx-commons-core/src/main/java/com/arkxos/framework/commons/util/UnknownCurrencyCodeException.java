package com.arkxos.framework.commons.util;

/**
 * An exception which is raised when an unrecognised currency code is passed to the Currency class.
 * 
 * @author Andrew Leppard
 * @see Currency
 */
public class UnknownCurrencyCodeException extends Throwable {

	private static final long serialVersionUID = 1L;
	
	// Reason for exception
	private String reason = null;

	/**
	 * Create a new unknown currency code exception.
	 * 
	 * @param reason for the exception
	 */
	public UnknownCurrencyCodeException(String reason) {
		this.reason = reason;
	}

	/**
	 * Return the reason this exception was raised.
	 * 
	 * @return the reason why the string isn't a valid currency code
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Convert the exception to a string
	 * 
	 * @return string version of the exception
	 */
	@Override
	public String toString() {
		return getReason();
	}
}