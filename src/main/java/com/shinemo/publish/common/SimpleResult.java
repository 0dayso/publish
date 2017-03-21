/**
 * 
 */
package com.shinemo.publish.common;


/**
 * @author david
 * 
 */
public class SimpleResult<T> extends BasicResult {

	private static final long serialVersionUID = -357405810301351905L;

	private T data;

	/**
	 * @return the data
	 */
	public final T getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public final void setData(T data) {
		this.data = data;
	}
	
	public static <T> SimpleResult<T> buildSuccessResult(T data){
		SimpleResult<T> s = new SimpleResult<T>();
		s.setData(data);
		s.setSuccess(true);
		s.setStatus(200);
		return s;
	}
	
	public static <T> SimpleResult<T> buildSuccessResult(){
		SimpleResult<T> s = new SimpleResult<T>();
		s.setSuccess(true);
		s.setStatus(200);
		return s;
	}
	
	public static <T> SimpleResult<T> buildSuccessResult(Integer status){
		SimpleResult<T> s = new SimpleResult<T>();
		s.setSuccess(true);
		s.setStatus(status);
		return s;
	}
	
	public static <T> SimpleResult <T> buildFailResult(String message, Throwable exception){
		SimpleResult<T> s = new SimpleResult<T>();
		s.setMessage(message);
		s.setException(exception);
		s.setSuccess(false);
		s.setStatus(500);
		return s;
	}
	
	public static <T> SimpleResult <T> buildFailResult(String message){
		return buildFailResult(message, null);
	}
	
	public static <T> SimpleResult <T> buildFailResult(String message, Boolean success, Throwable exception, int status){
		SimpleResult<T>  s = new SimpleResult<T>();
		s.setMessage(message);
		s.setException(exception);
		s.setSuccess(success);
		s.setStatus(status);
		return s;
	}
	
	public static <T> SimpleResult <T> buildFailResult(String message, int status){
		return buildFailResult(message, false, null, status);
	}

}
