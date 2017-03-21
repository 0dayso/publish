/**
 * 
 */
package com.shinemo.publish.common;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author david
 * 
 */
public class BasicResult implements Serializable {

	private static final long serialVersionUID = 517161603122400300L;
	private boolean success = false;
	private String message;
	private Throwable exception;
	private int status;

	/**
	 * @return the success
	 */
	public final boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public final void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public final void setMessage(String message) {
		this.message = message;
	}
	
	public final Throwable getException() {
		return exception;
	}

	public final void setException(Throwable exception) {
		this.exception = exception;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}