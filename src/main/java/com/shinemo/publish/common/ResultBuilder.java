package com.shinemo.publish.common;

import java.io.Serializable;

public class ResultBuilder<T> implements Serializable {

	private static final long serialVersionUID = -5382128855948257372L;

	private T value;

	private int status;
	
	private String msg;
	
	private String remark;
	
	private Throwable throwable;

	private boolean success;

	public ResultBuilder<T> setValue(T value) {
		this.value = value;
		return this;
	}

	public ResultBuilder<T> setStatus(int status) {
		this.status = status;
		return this;
	}
	
	public ResultBuilder<T> setRemark(String remark) {
		this.remark = remark;
		return this;
	}

	public ResultBuilder<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public ResultBuilder<T> setThrowable(Throwable throwable) {
		this.throwable = throwable;
		return this;
	}
	
	
	
	public ResultBuilder<T> setError(Errors errors) {
		this.status = errors.getCode();
		this.msg = errors.getMsg();
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public ResultBuilder<T> setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public int getStatus() {
		return status;
	}

	/**
	 * success=true
	 * @return
	 */
	public Result<T> build() {
		if (null != value) {
			success = true;
		} else if (status>0) {
			success = false;
		}
		return new Result<T>(success, value, status,msg, throwable,remark);
	}

	public T getValue() {
		return value;
	}


	public Throwable getThrowable() {
		return throwable;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getRemark() {
		return remark;
	}
	
	
}
