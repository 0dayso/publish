/**
 * 
 */
package com.shinemo.publish.api.base;

import java.io.Serializable;

/**
 * @author david
 *
 */
public class ApiResponse<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean success;
	private T data;
	private String msg;
	private Integer status;

	public static ApiResponseBuilder builder(){
		return new ApiResponseBuilder();
	}
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

	/**
	 * @return the msg
	 */
	public final String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public final void setMsg(String msg) {
		this.msg = msg;
	}

//	public void flush() {
//
//	}
	
	public static class ApiResponseBuilder<T>{
		private boolean success;
		private T data;
		private String msg;
		private Integer status;
		
		public ApiResponseBuilder success(boolean success){
			this.success = success;
			return this;
		}
		public ApiResponseBuilder data(T data){
			this.data = data;
			return this;
		}
		public ApiResponseBuilder msg(String msg){
			this.msg = msg;
			return this;
		}
		public ApiResponseBuilder status(Integer status){
			this.status = status;
			return this;
		}
		public ApiResponse build(){
			ApiResponse res = new ApiResponse();
			res.status = this.status;
			res.msg = this.msg;
			res.data = this.data;
			res.success = this.success;
			return res;
		}
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
}

