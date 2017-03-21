package com.shinemo.publish.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 5947203836182608272L;

	private boolean success;

	private T value;

	private int status;
	
	private String msg;

	private Throwable throwable;
	
	private String remark;

	public Result() {

	}
	
	public Result(boolean success,String msg) {
		this.success = success;
		this.msg = msg;
	}
	
	public Result(boolean success, T value, int status, String msg,Throwable throwable,String remark) {
		this.success = success;
		this.value = value;
		this.status = status;
		this.throwable = throwable;
		this.msg = msg;
		this.remark = remark;
	}

	public Result(boolean success, T value, int status,Throwable throwable) {
		this.success = success;
		this.value = value;
		this.status = status;
		this.throwable = throwable;
	}

	public boolean isSuccess() {
		return success;
	}

	public Result<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public T getValue() {
		return value;
	}

	public Result<T> setValue(T value) {
		this.value = value;
		return this;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public Result<T> setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getThrowableStackTrace() {
		if (null != throwable) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			throwable.printStackTrace(new PrintStream(out));
			return out.toString();
		}
		return "无异常被捕获";
	}

	public boolean isEmpty() {
		return null == value;
	}

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean hasValue() {
		return isSuccess() && isNotEmpty();
	}

	public String getRemark() {
		return remark;
	}

	public Result<T> setRemark(String remark) {
		this.remark = remark;
		return this;
	}
	

}
