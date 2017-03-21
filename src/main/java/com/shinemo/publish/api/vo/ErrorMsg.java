/**
 * 
 */
package com.shinemo.publish.api.vo;

/**
 * @author david
 *
 */
public class ErrorMsg {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ErrorMsg{" + "message='" + message + '\'' + '}';
	}
}
