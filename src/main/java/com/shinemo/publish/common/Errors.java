package com.shinemo.publish.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Errors implements Serializable {

	SUCCESS(0, "操作成功"), 
	FAILURE(1, "系统繁忙，请稍后重试"), 
	ERROR_500(500, "系统繁忙",true), 
	ERROR_NULL_OBJECT(9999, "Null object",true),
	
	ERROR_838(838, "系统繁忙，请稍后重试", true),
	E_NO_AUTH(2, "没有权限"),
	E_NOTEXIST(4, "操作对象不存在"),
	E_INVALIDUSER(5, "非法用户"),
	E_STATUS_INVALID(5, "状态不对"),
	E_UN_KNOWN(3, "未知错误"); 
	
	
	
	private Errors(int code, String msg,boolean throwable) {
		this.code = code;
		this.msg = msg;
		this.throwable = throwable;
	}
	
	private Errors(int code, String msg) {
		this(code,msg,false);
	}

	private static final Map<Integer, Errors> codeToError;
	static {
		Map<Integer, Errors> m = new HashMap<Integer, Errors>();
		for (Errors error : Errors.values()) {
			m.put(error.getCode(), error);
		}
		codeToError = Collections.unmodifiableMap(m);
	}

	public static Errors getErrorFromCode(int code) {
		return codeToError.containsKey(code) ? codeToError.get(code) : Errors.E_UN_KNOWN;
	}

	private static final long serialVersionUID = 1L;

	private final int code;

	private final String msg;
	
	private final boolean throwable; //是否抛出异常

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
	
	public boolean isThrowable() {
		return throwable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Error Code(").append(code).append(") Msg:").append(msg);
		return builder.toString();
	}

}
